/**
 * jetg
 * Copyright 2015-2016 David Yu
 *
 * Copyright 2010-2014 Guoqiang Chen. All rights reserved.
 * Email: subchen@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dyuproject.jetg;

import java.io.*;
import java.util.Map;
import com.dyuproject.jetg.JetConfig.CompileStrategy;
import com.dyuproject.jetg.compiler.JavaCompiler;
import com.dyuproject.jetg.compiler.JavaSource;
import com.dyuproject.jetg.parser.*;
import com.dyuproject.jetg.parser.code.Code;
import com.dyuproject.jetg.parser.*;
import com.dyuproject.jetg.parser.JetTemplateParser.TemplateContext;
import com.dyuproject.jetg.resource.*;
import com.dyuproject.jetg.runtime.*;
import com.dyuproject.jetg.utils.ExceptionUtils;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JetTemplate {
    private static final Logger log = LoggerFactory.getLogger(JetTemplate.class);

    private final JetEngine engine;
    private final Resource resource;
    private final String encoding;
    private final boolean reloadable;
    private final File javaClassFile;
    private long lastCompiledTimestamp;
    private JetPage pageObject;

    protected JetTemplate(JetEngine engine, Resource resource) {
        JetConfig config = engine.getConfig();
        CompileStrategy compileStrategy = config.getCompileStrategy();

        this.engine = engine;
        this.resource = resource;
        this.encoding = config.getOutputEncoding();
        this.reloadable = config.isTemplateReloadable() && compileStrategy != CompileStrategy.none;
        this.javaClassFile = engine.getClassLoader().getGeneratedJavaClassFile(resource.getQualifiedClassName());
        this.lastCompiledTimestamp = javaClassFile.lastModified();

        // compile and load
        switch (compileStrategy) {
        case precompile:
        case always:
            compileAndLoadClass();
            break;
        case auto:
            if (lastCompiledTimestamp > resource.lastModified()) {
                try {
                    loadClassFile();
                } catch (Throwable e) {
                    // 无法 load 的话，尝试重新编译
                    log.warn(e.getClass().getName() + ": " + e.getMessage());
                    log.warn("Try to recompile this template.");
                    compileAndLoadClass();
                }
            } else {
                compileAndLoadClass();
            }
            break;
        case none:
            if (resource instanceof SourceCodeResource) {
                // source code 的情况下必须编译
                compileAndLoadClass();
            } else if (resource instanceof CompiledClassResource) {
                try {
                    loadClassFile();
                } catch (Exception e) {
                    throw ExceptionUtils.uncheck(e);
                }
            } else {
                throw new IllegalStateException("Invalid resource when " + JetConfig.COMPILE_STRATEGY + " is " + compileStrategy);
            }
            break;
        }
    }
    
    public JetPage getJetPage() {
        return pageObject;
    }

    // 检测模板是否已更新
    protected void checkLastModified() {
        if (reloadable && lastCompiledTimestamp < resource.lastModified()) {
            synchronized (this) {
                // double check
                if (lastCompiledTimestamp < resource.lastModified()) {
                    compileAndLoadClass();
                }
            }
        }
    }

    // 从 disk 的缓存文件中读取 class
    private void loadClassFile() throws Exception {
        Class<?> compiledKlass;

        if (resource instanceof CompiledClassResource) {
            log.info("Loading template from classpath： {}.", resource.getName());
            compiledKlass = ((CompiledClassResource) resource).getCompiledClass();
        } else {
            log.info("Loading template class file: {}", javaClassFile.getAbsolutePath());
            compiledKlass = engine.getClassLoader().loadClass(resource.getQualifiedClassName());

            // 判断编码匹配
            if (!encoding.equals(compiledKlass.getDeclaredField("$ENC").get(null))) {
                throw new IllegalStateException("The encoding of last compiled template class is not " + encoding);
            }
        }

        pageObject = (JetPage) compiledKlass.newInstance();
    }

    // 编译 source 为 class， 然后 load class
    private void compileAndLoadClass() {
        boolean notPrecompileThread = !"JetPreCompiler".equals(Thread.currentThread().getName());
        if (notPrecompileThread) {
            log.info("Loading template source file: " + resource.getAbsolutePath());
        }

        // generateJavaSource
        String source = generateJavaSource(resource);
        if (notPrecompileThread && log.isInfoEnabled() && engine.getConfig().isCompileDebug()) {
            File javaSourceFile = engine.getClassLoader().getGeneratedJavaSourceFile(resource.getQualifiedClassName());
            StringBuilder sb = new StringBuilder(source.length() + 128);
            sb.append("generateJavaSource: ");
            sb.append(javaSourceFile.getAbsolutePath());
            sb.append("\n");
            sb.append("===========================================================================\n");
            sb.append(source);
            sb.append("\n");
            sb.append("===========================================================================");
            log.info(sb.toString());
        }

        JavaCompiler javaCompiler = engine.getJavaCompiler();

        // compile
        long ts = System.currentTimeMillis();
        JavaSource javaSource = new JavaSource(resource.getQualifiedClassName(), source, javaCompiler.getOutputdir());
        Class<?> cls = javaCompiler.compile(javaSource);
        if (notPrecompileThread) {
            ts = System.currentTimeMillis() - ts;
            log.info("generateJavaClass: {}, {}ms", javaClassFile.getAbsolutePath(), ts);
        }
        try {
            lastCompiledTimestamp = javaClassFile.lastModified();
            pageObject = (JetPage) cls.newInstance();
        } catch (Exception e) {
            throw ExceptionUtils.uncheck(e);
        }
    }

    private String generateJavaSource(Resource resource) {
        char[] source = resource.getSource();
        ANTLRInputStream is = new ANTLRInputStream(source, source.length);
        is.name = resource.getAbsolutePath(); // set source file name, it will be displayed in error report.

        JetTemplateLexer lexer = new JetTemplateLexer(is);
        lexer.removeErrorListeners(); // remove ConsoleErrorListener
        lexer.addErrorListener(JetTemplateErrorListener.getInstance());

        JetTemplateParser parser = new JetTemplateParser(new CommonTokenStream(lexer));
        parser.removeErrorListeners(); // remove ConsoleErrorListener
        parser.addErrorListener(JetTemplateErrorListener.getInstance());
        parser.setErrorHandler(new JetTemplateErrorStrategy());

        TemplateContext templateParseTree = parser.template();
        JetTemplateCodeVisitor visitor = new JetTemplateCodeVisitor(engine, engine.getVariableResolver(), engine.getSecurityManager(), parser, resource);
        Code code = templateParseTree.accept(visitor);
        return code.toString();
    }

    public void render(Map<String, Object> context, Writer out) {
        JetContext ctx = new JetContext(context);
        JetWriter writer = JetWriter.create(out, encoding);
        render(new JetPageContext(this, ctx, writer));
    }

    public void render(Map<String, Object> context, OutputStream out) {
        JetContext ctx = new JetContext(context);
        JetWriter writer = JetWriter.create(out, encoding);
        render(new JetPageContext(this, ctx, writer));
    }

    public void render(JetContext context, Writer out) {
        if (context == null) {
            context = new JetContext(null);
        } else if (context.isSimpleModel()) {
            // simpleModel 的情况代表是用户自己 new 出来的 JetContext
            // 为了防止 #set 污染 context，这里重新 new 一个新的。
            context = new JetContext(context.getContext());
        }
        JetWriter writer = JetWriter.create(out, encoding);
        render(new JetPageContext(this, context, writer));
    }

    public void render(JetContext context, OutputStream out) {
        if (context == null) {
            context = new JetContext(null);
        } else if (context.isSimpleModel()) {
            // simpleModel 的情况代表是用户自己 new 出来的 JetContext
            // 为了防止 #set 污染 context，这里重新 new 一个新的。
            context = new JetContext(context.getContext());
        }
        JetWriter writer = JetWriter.create(out, encoding);
        render(new JetPageContext(this, context, writer));
    }

    public void render(JetContext context, JetWriter writer) {
        render(new JetPageContext(this, context, writer));
    }

    private void render(JetPageContext ctx) {
        try {
            if (engine.getGlobalVariables() != null) {
                ctx.getContext().setGlobalVariables(engine.getGlobalVariables());
            }
            pageObject.render(ctx);
        } catch (Throwable e) {
            // JSP use response.getWriter() to ignore all io exceptions.
            // I use response.getOutputStream() and only ignore ClientAbortException.
            if ("org.apache.catalina.connector.ClientAbortException".equals(e.getClass().getName())) {
                log.warn(e.toString());
            } else {
                throw ExceptionUtils.uncheck(e);
            }
        }
    }

    public JetEngine getEngine() {
        return engine;
    }

    public String getName() {
        return resource.getName();
    }
}
