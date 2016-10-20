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

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.*;
import com.dyuproject.jetg.JetConfig.CompileStrategy;
import com.dyuproject.jetg.compiler.JavaCompiler;
import com.dyuproject.jetg.compiler.JetTemplateClassLoader;
import com.dyuproject.jetg.parser.VariableResolver;
import com.dyuproject.jetg.resource.Resource;
import com.dyuproject.jetg.resource.SourceCodeResource;
import com.dyuproject.jetg.resource.loader.CompiledClassResourceLoader;
import com.dyuproject.jetg.resource.loader.ResourceLoader;
import com.dyuproject.jetg.utils.*;
import com.dyuproject.jetg.utils.finder.AnnotationClassLookupUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JetEngine {
    private static final Logger log = LoggerFactory.getLogger(JetEngine.class);
    public static final String VERSION = Version.getVersion(JetEngine.class);

    private JetConfig config;
    private ResourceLoader resourceLoader;
    private VariableResolver resolver;
    private JetTemplateClassLoader classLoader;
    private ConcurrentResourceCache resourceCache;
    private ConcurrentTemplateCache templateCache;
    private JavaCompiler javaCompiler;
    private JetSecurityManager securityManager;
    private JetGlobalVariables globalVariables;

    public static JetEngine create() {
        return new JetEngine(new JetConfig().loadClasspath(JetConfig.DEFAULT_CONFIG_FILE));
    }

    public static JetEngine create(File configFile) {
        return new JetEngine(new JetConfig().loadFile(configFile));
    }

    public static JetEngine create(Properties properties) {
        return new JetEngine(new JetConfig().load(properties));
    }

    // 提供给 JetWebEngine 用
    protected JetEngine() {
    }

    protected JetEngine(JetConfig config) {
        load(config);
    }

    protected void load(JetConfig config) {
        this.config = config.build();
        this.resolver = createVariableResolver();
        this.resourceLoader = createResourceLoader();
        this.classLoader = new JetTemplateClassLoader(config);
        this.resourceCache = new ConcurrentResourceCache();
        this.templateCache = new ConcurrentTemplateCache();
        this.securityManager = createSecurityManager();
        this.globalVariables = createGlobalVariables();

        if (config.getCompileStrategy() == CompileStrategy.precompile) {
            startPreCompileTask();
        }
    }

    /**
     * 根据一个绝对路径，判断资源文件是否存在.
     */
    public boolean lookupResource(String name) {
        name = PathUtils.getStandardizedName(name);
        return resourceCache.get(name) != null;
    }

    /**
     * 根据一个绝对路径，获取一个资源对象.
     *
     * @throws ResourceNotFoundException
     */
    public Resource getResource(String name) throws ResourceNotFoundException {
        name = PathUtils.getStandardizedName(name);
        Resource resource = resourceCache.get(name);
        if (resource == null) {
            throw new ResourceNotFoundException(name);
        }
        return resource;
    }

    /**
     * 根据一个绝对路径，获取一个模板对象.
     *
     * @throws ResourceNotFoundException
     */
    public JetTemplate getTemplate(String name) throws ResourceNotFoundException {
        name = PathUtils.getStandardizedName(name);
        JetTemplate template = templateCache.get(name);
        template.checkLastModified();
        return template;
    }

    /**
     * 直接从源代码中创建一个新的模板对象.
     *
     * <p>返回的对象内部没有缓存，每次都会重新进行解析和编译，如果需要缓存，请在外面直接实现。</p>
     *
     * @since 1.1.0
     */
    public JetTemplate createTemplate(String source) {
        Resource resource = new SourceCodeResource(source);
        return new JetTemplate(this, resource);
    }

    protected VariableResolver getVariableResolver() {
        return resolver;
    }

    protected JetTemplateClassLoader getClassLoader() {
        return classLoader;
    }

    protected JavaCompiler getJavaCompiler() {
        JavaCompiler javaCompiler = this.javaCompiler;
        if (javaCompiler == null) {
            // 在 compileStrategy == none 的情况下，采用延迟加载，可以有效避免没有 javax.tools.JavaCompiler 的情况
            synchronized (this) {
                if ((javaCompiler = this.javaCompiler) == null) {
                    this.javaCompiler = javaCompiler = JavaCompiler.create(this.classLoader, config);
                }
            }
        }
        return javaCompiler;
    }

    public JetSecurityManager getSecurityManager() {
        return securityManager;
    }

    public JetGlobalVariables getGlobalVariables() {
        return globalVariables;
    }

    /**
     * 获取模板配置.
     */
    public JetConfig getConfig() {
        return config;
    }

    /**
     * 获取模板引擎的版本号.
     */
    public String getVersion() {
        return VERSION;
    }

    private VariableResolver createVariableResolver() {
        VariableResolver resolver = new VariableResolver();
        for (String pkg : config.getImportPackages()) {
            resolver.addImportPackage(pkg);
        }
        for (String klassName : config.getImportClasses()) {
            resolver.addImportClass(klassName);
        }
        for (String method : config.getImportMethods()) {
            resolver.addMethodClass(method);
        }
        for (String function : config.getImportFunctions()) {
            resolver.addFunctionClass(function);
        }
        for (String tag : config.getImportTags()) {
            resolver.addTagClass(tag);
        }
        for (String variable : config.getImportVariables()) {
            int pos = variable.lastIndexOf(" ");
            String defination = variable.substring(0, pos);
            String id = variable.substring(pos + 1);
            resolver.addGlobalVariable(defination, id);
        }

        if (config.isImportAutoscan()) {
            log.info("Starting to autoscan the JetMethods, JetFunctions, JetTags implements...");
            autoScanClassImplements(resolver);
        }

        return resolver;
    }

    // 自动扫描 annotation
    @SuppressWarnings({ "unchecked" })
    private void autoScanClassImplements(VariableResolver resolver) {
        List<String> scanPackages = config.getImportAutoscanPackages();
        //@formatter:off
        Class<? extends Annotation>[] annoClasses = (Class<? extends Annotation>[]) new Class<?>[] {
            JetAnnotations.Methods.class,
            JetAnnotations.Functions.class,
            JetAnnotations.Tags.class,
        };
        //@formatter:on
        long ts = System.currentTimeMillis();
        Set<Class<?>> klasses = AnnotationClassLookupUtils.getClasses(scanPackages, true, annoClasses, config.isImportAutoscanSkiperrors());
        ts = System.currentTimeMillis() - ts;

        log.info("Successfully to find {} classes, cost {} ms.", klasses.size(), ts);

        for (Class<?> klass : klasses) {
            for (Annotation anno : klass.getAnnotations()) {
                if (anno instanceof JetAnnotations.Methods) {
                    resolver.addMethodClass(klass);
                } else if (anno instanceof JetAnnotations.Functions) {
                    resolver.addFunctionClass(klass);
                } else if (anno instanceof JetAnnotations.Tags) {
                    resolver.addTagClass(klass);
                }
            }
        }
    }

    private ResourceLoader createResourceLoader() {
        try {
            ResourceLoader resourceLoader;
            if (config.getCompileStrategy() == CompileStrategy.none) {
                // 这种情况下，使用自定义的 ResourceLoader
                resourceLoader = new CompiledClassResourceLoader();
            } else {
                resourceLoader = (ResourceLoader) config.getTemplateLoader().newInstance();
            }
            resourceLoader.initialize(this, config.getTemplatePath(), config.getInputEncoding());
            return resourceLoader;
        } catch (Exception e) {
            throw ExceptionUtils.uncheck(e);
        }
    }

    private JetSecurityManager createSecurityManager() {
        Class<?> klass = config.getSecurityManager();
        if (klass == null) {
            return null;
        }
        try {
            JetSecurityManager manager = (JetSecurityManager) klass.newInstance();
            manager.initialize(this);
            return manager;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JetGlobalVariables createGlobalVariables() {
        Class<?> klass = config.getGlobalVariables();
        if (klass == null) {
            return null;
        }
        try {
            return (JetGlobalVariables) klass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void startPreCompileTask() {
        // 启动预编译线程
        Thread thread = new Thread() {
            @Override
            public void run() {
                List<String> resources = resourceLoader.loadAll();
                log.info("Find {} templates to precompile ...", resources.size());
                int succ = 0;
                int fail = 0;
                long ts = System.currentTimeMillis();
                for (String name : resources) {
                    try {
                        getTemplate(name);
                        succ++;
                    } catch (Exception e) {
                        fail++;
                        log.error("precompile error.", e);
                    }
                }
                ts = System.currentTimeMillis() - ts;
                log.info("Completed precompile templates in {} ms, success = {}, failure = {}.", ts, succ, fail);
            }
        };
        thread.setName("JetPreCompiler");
        thread.setDaemon(true);
        thread.start();
    }

    private class ConcurrentResourceCache extends ConcurrentCache<String, Resource> {
        @Override
        protected Resource doGetValue(String name) {
            return JetEngine.this.resourceLoader.load(name);
        }
    }

    private class ConcurrentTemplateCache extends ConcurrentCache<String, JetTemplate> {
        @Override
        protected JetTemplate doGetValue(String name) {
            Resource resource = JetEngine.this.getResource(name);
            return new JetTemplate(JetEngine.this, resource);
        }
    }
}
