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
package com.dyuproject.jetg.parser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.SourceVersion;

import com.dyuproject.jetg.JetContext;
import com.dyuproject.jetg.JetEngine;
import com.dyuproject.jetg.JetSecurityManager;
import com.dyuproject.jetg.parser.code.BlockCode;
import com.dyuproject.jetg.parser.code.Code;
import com.dyuproject.jetg.parser.code.DefineExpressionCode;
import com.dyuproject.jetg.parser.code.EmitCode;
import com.dyuproject.jetg.parser.code.ForExpressionCode;
import com.dyuproject.jetg.parser.code.LineCode;
import com.dyuproject.jetg.parser.code.MacroCode;
import com.dyuproject.jetg.parser.code.ProcCode;
import com.dyuproject.jetg.parser.code.ScopeCode;
import com.dyuproject.jetg.parser.code.SegmentCode;
import com.dyuproject.jetg.parser.code.SegmentListCode;
import com.dyuproject.jetg.parser.code.TagCode;
import com.dyuproject.jetg.parser.code.TemplateClassCode;
import com.dyuproject.jetg.parser.code.TextCode;
import com.dyuproject.jetg.parser.JetTemplateParser;
import com.dyuproject.jetg.parser.JetTemplateParser.Alt_block_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Alt_else_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Alt_elseif_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Alt_for_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Alt_if_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Arg_decl_expression_listContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Assign_expressionContext;
import com.dyuproject.jetg.parser.JetTemplateParser.BlockContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Block_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Break_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Call_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.ConstantContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Content_blockContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Context_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Continue_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Control_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Define_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Define_expressionContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Define_expression_listContext;
import com.dyuproject.jetg.parser.JetTemplateParser.DirectiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Else_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Elseif_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Emit_blockContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Emit_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_array_getContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_array_listContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_class_castContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_compare_conditionContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_compare_equalityContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_compare_notContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_compare_relationalContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_conditional_ternaryContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_constantContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_field_accessContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_function_callContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_groupContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_hash_mapContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_identifierContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_instanceofContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_math_binary_basicContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_math_binary_bitwiseContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_math_binary_shiftContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_math_unary_prefixContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_math_unary_suffixContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_method_invocationContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_new_arrayContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_new_objectContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_static_field_accessContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expr_static_method_invocationContext;
import com.dyuproject.jetg.parser.JetTemplateParser.ExpressionContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Expression_listContext;
import com.dyuproject.jetg.parser.JetTemplateParser.For_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.For_expressionContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Hash_map_entry_listContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Header_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.If_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Include_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Invalid_block_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Invalid_context_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Invalid_control_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Macro_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Misplaced_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Optional_define_expressionContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Proc_blockContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Proc_content_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Proc_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Proc_emit_blockContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Proc_ignore_newline_blockContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Put_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Set_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Set_expressionContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Static_type_nameContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Stop_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Tag_directiveContext;
import com.dyuproject.jetg.parser.JetTemplateParser.TemplateContext;
import com.dyuproject.jetg.parser.JetTemplateParser.TextContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Text_newlineContext;
import com.dyuproject.jetg.parser.JetTemplateParser.TypeContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Type_argumentsContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Type_array_suffixContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Type_listContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Type_nameContext;
import com.dyuproject.jetg.parser.JetTemplateParser.ValueContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Value_iterationContext;
import com.dyuproject.jetg.parser.JetTemplateParser.Value_optionsContext;
import com.dyuproject.jetg.parser.JetTemplateParserVisitor;
import com.dyuproject.jetg.parser.support.ClassUtils;
import com.dyuproject.jetg.parser.support.NumberClassUtils;
import com.dyuproject.jetg.parser.support.PrimitiveClassUtils;
import com.dyuproject.jetg.parser.support.PromotionUtils;
import com.dyuproject.jetg.parser.support.TypedKlass;
import com.dyuproject.jetg.parser.support.TypedKlassUtils;
import com.dyuproject.jetg.resource.Resource;
import com.dyuproject.jetg.runtime.JetTagContext;
import com.dyuproject.jetg.utils.PathUtils;
import com.dyuproject.jetg.utils.StringEscapeUtils;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

// Visitor 模式访问器，用来生成 Java 代码
public class JetTemplateCodeVisitor extends AbstractParseTreeVisitor<Code> implements JetTemplateParserVisitor<Code> {
    
    public static final HashMap<String,Boolean> NON_VOID_CALL = new HashMap<String, Boolean>();
    static final HashMap<String,Integer> HEADERS = new HashMap<String, Integer>();
    
    static final int H_IMPORT = 1,
            H_EXTENDS = 2;
    static
    {
        NON_VOID_CALL.put("get", Boolean.TRUE);
        NON_VOID_CALL.put("is", Boolean.TRUE);
        
        HEADERS.put("#import", H_IMPORT);
        HEADERS.put("#extends", H_EXTENDS);
    }
    
    private final JetEngine engine;
    private final Resource resource;
    private final JetTemplateParser parser;
    private final VariableResolver resolver;
    private final JetSecurityManager securityManager;
    private final boolean globalSafeCall;
    //private final boolean trimDirectiveLine;
    private final boolean trimDirectiveComments;
    private final String commentsPrefix;
    private final String commentsSuffix;
    private final String importedProcSuffix;
    private String varNewLine, varActiveNewLine;
    private boolean countLeadingSpaces;
    private boolean trimComments, trimIfComments, trimForComments, trimElseComments;
    private boolean validContextDirective, validBreakOrContinue;
    private boolean emitContext, nestedContext;
    private boolean templateBlock;
    private boolean ignoreNewLine, tempIgnoreNewline;
    private int currentIndent;
    private int inlineIfChildCount, inlineIfCurrentCount;
    private boolean indentInlineIf, conditionalInlineIf;
    private BlockCode procInsideIf;

    private TemplateClassCode tcc; //
    private ScopeCode scopeCode; // 当前作用域对应的 Code
    private Map<String, MacroCode> macroMap; // 宏定义
    private Map<String, ProcCode> procMap;
    private Map<String, TextCode> textCache; // 文本内容缓存(可以减少冗余 Text)
    private Deque<String> forStack; // 维护嵌套 #for 的堆栈，可以识别是否在嵌入在 #for 里面, 内部存储当前 for 的真实变量名
    private int uuid = 1; // 计数器

    public JetTemplateCodeVisitor(JetEngine engine, VariableResolver resolver, JetSecurityManager securityManager, JetTemplateParser parser, Resource resource) {
        this.engine = engine;
        this.importedProcSuffix = engine.getConfig().getTemplateSuffix().replace('.', '_');
        this.parser = parser;
        this.resolver = resolver;
        this.securityManager = securityManager;
        this.resource = resource;
        this.globalSafeCall = engine.getConfig().isSyntaxSafecall();
        //this.trimDirectiveLine = engine.getConfig().isTrimDirectiveLine();
        this.trimDirectiveComments = engine.getConfig().isTrimDirectiveComments();
        this.commentsPrefix = engine.getConfig().getTrimDirectiveCommentsPrefix();
        this.commentsSuffix = engine.getConfig().getTrimDirectiveCommentsSuffix();

        this.textCache = new HashMap<String, TextCode>(32);
        this.forStack = new ArrayDeque<String>(8);

        // 专门处理是否存在未完成的解析 (1.2.8 在 parser.g4 中添加 EOF，此处代码已经无用)
        //Token token = parser.getCurrentToken();
        //if (token.getType() != Token.EOF) {
        //    throw reportError("Invalid " + token.getText() + " directive in here.", token);
        //}
    }

    @Override
    public Code visitTemplate(TemplateContext ctx) {
        tcc = new TemplateClassCode(engine, resource);

        scopeCode = tcc.getMethodCode();
        scopeCode.define(Code.CONTEXT_NAME, TypedKlass.JetContext);
        
        for (JetTemplateParser.Header_directiveContext h : ctx.header_directive())
            h.accept(this);
        
        validContextDirective = true;
        
        for (JetTemplateParser.Macro_directiveContext m : ctx.macro_directive())
            m.accept(this);
        
        templateBlock = true;
        scopeCode.setBodyCode(ctx.block().accept(this));
        templateBlock = false;
        conditionalInlineIf = false;
        tempIgnoreNewline = false;
        
        validContextDirective = false;
        
        for (JetTemplateParser.Proc_directiveContext p : ctx.proc_directive())
            p.accept(this);
        
        return tcc;
    }
    
    @Override
    public Code visitHeader_directive(Header_directiveContext ctx) {
        final String directive = ctx.TEXT_DIRECTIVE_LIKE().getText(),
                text = ctx.TEXT_PLAIN().getText().trim();
        final Integer type = HEADERS.get(directive);
        switch (type == null ? 0 : type.intValue())
        {
            case H_IMPORT:
                tcc.addImport(text, ctx.TEXT_SINGLE_HASH() != null);
                break;
            case H_EXTENDS:
                tcc.baseClass = text;
                break;
            default:
                reportError("Unknown header directive: " + directive, ctx);
        }
        
        return Code.EMPTY;
    }
    
    @Override
    public Code visitBlock(BlockContext ctx) {
        /*List<ParseTree> children = ctx.children;
        if (children == null || children.size() == 0)
            return Code.EMPTY;
        */
        final boolean nestedContext = this.nestedContext; // push
        if (!nestedContext)
            this.nestedContext = ctx.getParent().getClass() != JetTemplateParser.TemplateContext.class;
        
        //childCount = children.size();
        
        Code c = visitBlock(ctx.getParent(), ctx.children, 
                this.nestedContext, 
                false, 
                DirectiveContext.class);
        
        this.nestedContext = nestedContext; // pop
        
        return c;
    }
    
    @Override
    public Code visitContent_block(Content_blockContext ctx) {
        ignoreNewLine = ctx.getStart().getLine() == ctx.getStop().getLine();
        
        Code c = visitBlock(ctx.getParent(), ctx.children, 
                false, 
                true, 
                DirectiveContext.class);
        
        // always reset it
        currentIndent = 0;
        
        tempIgnoreNewline = false;
        ignoreNewLine = false;
        
        return c;
    }

    @Override
    public Code visitProc_block(Proc_blockContext ctx)
    {
        ignoreNewLine = ctx.getStart().getLine() == ctx.getStop().getLine();
        
        List<ParseTree> children = ctx.children;
        Code c = visitBlock(ctx.getParent(), children.subList(1, children.size()), 
                false, 
                true, 
                Proc_content_directiveContext.class);
        
        // always reset it
        currentIndent = 0;
        
        tempIgnoreNewline = false;
        ignoreNewLine = false;
        
        return c;
    }
    
    @Override
    public Code visitProc_ignore_newline_block(Proc_ignore_newline_blockContext ctx)
    {
        ignoreNewLine = true;
        
        List<ParseTree> children = ctx.children;
        Code c = visitBlock(ctx.getParent(), children.subList(2, children.size()), 
                false, 
                true, 
                Proc_content_directiveContext.class);
        
        // always reset it
        currentIndent = 0;
        
        ignoreNewLine = false;
        
        return c;
    }
    
    @Override
    public Code visitProc_emit_block(Proc_emit_blockContext ctx)
    {
        emitContext = true;
        
        List<ParseTree> children = ctx.children;
        Code c = visitBlock(ctx.getParent(), children.subList(3, children.size()), 
                false, 
                true, 
                Void.class);
        
        // always reset it
        currentIndent = 0;
        
        emitContext = false;
        
        return c;
    }
    
    @Override
    public Code visitEmit_block(Emit_blockContext ctx)
    {
        emitContext = true;
        
        final int indent = currentIndent; // push
        
        currentIndent = 0;
        
        BlockCode c = visitBlock(ctx.getParent(), ctx.children, 
                false, 
                true, 
                Void.class);
        
        c.addChild(Code.NEWLINE);
        
        currentIndent = indent; // pop
        
        emitContext = false;
        
        return c;
    }

    private int addPrintlnTo(BlockCode code, int printlnCount, TextCode tc) {
        if (printlnCount == 1) {
            if (varActiveNewLine == null)
                code.addLine("$out.println();");
            else
                code.addLine("if (" + varActiveNewLine + " != 0) $out.println();");
        } else {
            if (varActiveNewLine == null)
                code.addLine("$out.printLine(" + printlnCount + ");");
            else
                code.addLine("$out.printLine(" + varActiveNewLine + " == 0 ? " + (printlnCount-1) + " : " + printlnCount + ");");
        }
        
        varActiveNewLine = null;
        
        return 0;
    }

    private BlockCode visitBlock(ParserRuleContext parentContext, List<ParseTree> children, 
            final boolean insideDirective, final boolean contentBlock, 
            final Class<?> classDirective) {
        int size = children == null ? 0 : children.size();
        BlockCode code = scopeCode.createBlockCode(size);
        if (size == 0) return code;
        
        boolean clearProcInsideIf = false, 
                ignoreNewLine = contentBlock || insideDirective;
        
        ParseTree node = null, prev = null;
        Code c = null;
        TextCode tc = null;
        int printlnCount = 0;
        for (int i = 0; i < size; i++) {
            if (clearProcInsideIf) {
                clearProcInsideIf = false;
                procInsideIf = null;
            }
            
            prev = node;
            node = children.get(i);
            
            if (!conditionalInlineIf && (i == 0 || c == TextCode.NEWLINE || tc != null))
                indentInlineIf = classDirective.isAssignableFrom(node.getClass());
            
            c = node.accept(this);
            
            if (tc != null) {
                if (c == TextCode.NEWLINE) {
                    indentInlineIf = false;
                    code.addLine(tc.printSpace + tc.leadingSpaces + ");");
                    tc = null;
                    
                    if (conditionalInlineIf) {
                        conditionalInlineIf = false;
                        code.addLine("$out.$pop(false);");
                        ignoreNewLine = false;
                    } else if (!this.ignoreNewLine) {
                        printlnCount = 1;
                        ignoreNewLine = false;
                    }
                    
                    continue;
                }
                
                if (c.proc || node instanceof ValueContext) {
                    // all spaces -> value/proc call
                    code.addChild(c);
                    ignoreNewLine = c.proc; // ignore the next newline if proc
                    tc = null;
                    continue;
                }
                
                if (tempIgnoreNewline) {
                    // an inline if-else statement with their first child being text/value
                    code.addLine(TextCode.PRINT_INDENT + tc.leadingSpaces + ");");
                    code.addChild(c);
                    ignoreNewLine = false;
                    tc = null;
                    continue;
                }
                
                tc = null;
            }
            
            if (c == TextCode.NEWLINE) {
                indentInlineIf = false;
                if (conditionalInlineIf) {
                    conditionalInlineIf = false;
                    code.addLine("$out.$pop(false);");
                    continue;
                }
                
                if (this.ignoreNewLine)
                    continue;
                
                if (ignoreNewLine) {
                    ignoreNewLine = false;
                } else {
                    printlnCount++;
                }
                continue;
            }
            
            if (!(node instanceof TextContext) || c instanceof EmitCode)
            {
                if (printlnCount != 0)
                    printlnCount = addPrintlnTo(code, printlnCount, null);
                
                //if (c.proc && (prev == null || prev instanceof Text_newlineContext))
                //    ignoreNewLine = true;
                ignoreNewLine = !c.readNextNewLine && 
                        classDirective.isAssignableFrom(node.getClass());
                
                if (procInsideIf != null) {
                    // an if statement that wraps a proc
                    clearProcInsideIf = true;
                }
                
                code.addChild(c);
                continue;
            }
            
            tc = (TextCode)c;
            if (i == 0 && insideDirective && tc.allSpaces && 
                    i != size - 1 && (children.get(i+1) instanceof Text_newlineContext)) {
                // trims the extra spaces after the if/elseif/for/else
                ignoreNewLine = true;
                tc = null;
                continue;
            }
            
            if (printlnCount != 0)
                printlnCount = addPrintlnTo(code, printlnCount, tc);
            
            if (tc.allSpaces && tc.countLeadingSpaces)
                continue;
            
            if (!tc.allSpaces || 
                    (i != size - 1 && !classDirective.isAssignableFrom(children.get(i+1).getClass()))) {
                ignoreNewLine = addLineTo(code, tc, prev, parentContext, classDirective, children, i, size);
            } else {
                ignoreNewLine = false;
            }
            
            tc = null;
        }
        
        if (tc != null) {
            // trailing space
            if (contentBlock || parentContext instanceof TemplateContext)
                code.addLine(tc.printSpace + tc.leadingSpaces + ");");
            currentIndent = 0;
        } else if (procInsideIf != null) {
            // last statement, so remove the last println just like how
            // we ignore the last line when inside a content block
            if (contentBlock)
                procInsideIf.removeLastChild();
            procInsideIf = null;
        }
        
        varNewLine = null;
        
        if (printlnCount == 0 || (contentBlock && --printlnCount == 0)) {
            varActiveNewLine = null;
            return code;
        }
        
        addPrintlnTo(code, printlnCount, null);
        
        return code;
    }
    
    /**
     * Return true to ignore the next new line.
     */
    private boolean addLineTo(BlockCode code, TextCode textCode, ParseTree prev,
            ParserRuleContext parentContext, final Class<?> classDirective, 
            List<ParseTree> children, int i, int size) {
        // 文本节点
        if (/*trimDirectiveLine || */trimDirectiveComments) {
            //ParseTree prev = (i > 0) ? children.get(i - 1) : null;
            ParseTree next = (i < size - 1) ? children.get(i + 1) : null;
            
            boolean trimLeft;
            //boolean keepLeftNewLine = false;
            if (prev == null) {
                trimLeft = nestedContext;//!(parentContext instanceof TemplateContext);
            } else {
                trimLeft = classDirective.isAssignableFrom(prev.getClass());
                /*if (trimLeft) {
                    // inline directive, 对于一个内联的 #if, #for 指令，后面有要求保留一个 NewLine
                    // @see https://github.com/subchen/jetbrick-template/issues/25
                    ParserRuleContext directive = (ParserRuleContext) ((DirectiveContext) prev).getChild(0);
                    if (directive instanceof If_directiveContext || directive instanceof For_directiveContext) {
                        if (directive.getStart().getLine() == directive.getStop().getLine()) {
                            keepLeftNewLine = true; // 保留一个 NewLine
                        }
                    }
                }*/
            }

            boolean trimRight;
            if (next == null) {
                trimRight = nestedContext; //!(parentContext instanceof TemplateContext);
            } else {
                trimRight = classDirective.isAssignableFrom(next.getClass());
            }

            // trim 指令两边的注释
            if (textCode.trimComments(trimLeft, trimRight, commentsPrefix, commentsSuffix))
                return true;
            /*if (trimDirectiveComments) {
                textCode.trimComments(trimLeft, trimRight, commentsPrefix, commentsSuffix);
            }
            // trim 指令两边的空白内容
            if (trimDirectiveLine) {
                textCode.trimEmptyLine(trimLeft, trimRight, keepLeftNewLine);
            }*/
            
            // trim 掉 #tag 和 #macro 指令最后一个多余的 '\n'
            /*if (next == null) {
                if (parentContext instanceof Tag_directiveContext || parentContext instanceof Macro_directiveContext) {
                    textCode.trimLastNewLine();
                }
            }*/
        }
        
        if (textCode.isEmpty())
            return false;
        
        if (textCode.allSpaces) {
            code.addLine(textCode.printSpace + textCode.leadingSpaces + ");");
            return false;
        }
        
        // 如果有相同内容的Text，则从缓存中读取
        String cacheText = textCode.cacheText();
        TextCode old = textCache.get(cacheText);
        if (old == null) {
            old = textCode;
            textCache.put(cacheText, textCode);
            // add text into field
           tcc.addField(textCode.getId(), cacheText);
        }
        
        code.addLine(old.toString(!ignoreNewLine && textCode.countLeadingSpaces, 
                textCode.leadingSpaces, textCode.indent, textCode.addNewline, 
                textCode.print, textCode.printSpace));
        
        return false;
    }

    @Override
    public Code visitText(TextContext ctx) {
        if (emitContext) {
            return new EmitCode(4 + currentIndent, ctx.getChild(0).getText());
        }
        
        varNewLine = null;
        
        Token token = ((TerminalNode) ctx.getChild(0)).getSymbol();
        String text = token.getText();
        switch (token.getType()) {
        case JetTemplateParser.TEXT_CDATA:
            text = text.substring(3, text.length() - 3);
            break;
        case JetTemplateParser.TEXT_ESCAPED_CHAR:
            text = text.substring(1);
            break;
        case JetTemplateParser.TEXT_ESCAPED_NEWLINE:
            text = "\n";
            break;
        }
        
        boolean addNewline = false;
        int indent = 0;
        String print = TextCode.PRINT, printSpace = TextCode.PRINT_SPACE;
        if (conditionalInlineIf) {
            print = TextCode.$PRINT;
            printSpace = TextCode.$PRINT_SPACE;
        } else if (inlineIfChildCount != 0) {
            if (0 == inlineIfCurrentCount++)
                indent = currentIndent;
            addNewline = !ignoreNewLine && inlineIfChildCount == inlineIfCurrentCount;
        } else {
            indent = currentIndent;
        }
        
        String id = getUid("txt");
        TextCode tc = new TextCode(id, text, countLeadingSpaces, trimComments, 
                indent, addNewline, print, printSpace);
        countLeadingSpaces = false;
        trimComments = false;
        
        currentIndent = tc.allSpaces && tc.countLeadingSpaces ? tc.leadingSpaces : 0;
        
        return tc;
    }
    
    @Override
    public Code visitText_newline(Text_newlineContext ctx)
    {
        if (emitContext)
            return LineCode.NEWLINE;
        
        varActiveNewLine = varNewLine;
        varNewLine = null;
        
        if (tempIgnoreNewline) {
            tempIgnoreNewline = false;
            ignoreNewLine = false;
            countLeadingSpaces = true;
        } else {
            countLeadingSpaces = !ignoreNewLine;
        }
        
        currentIndent = 0;
        
        return TextCode.NEWLINE;
    }
    
    private LineCode newValueCode(String source, ValueContext ctx, 
            boolean addIndent, int indent)
    {
        final StringBuilder sb = new StringBuilder();
        String print = TextCode.PRINT;
        boolean addNewline = false;
        if (conditionalInlineIf) {
            print = TextCode.$PRINT;
        } else if (inlineIfChildCount != 0) {
            if (0 == inlineIfCurrentCount++)
                sb.append(TextCode.PRINT_INDENT).append(indent).append(");");
            addNewline = !ignoreNewLine && inlineIfChildCount == inlineIfCurrentCount;
        } else if (addIndent) {
            sb.append(TextCode.PRINT_INDENT).append(indent).append(");");
        }
        
        sb.append(print).append(source).append(");");
        
        if (addNewline)
            sb.append("$out.println();");
        
        sb.append(" // line: ").append(ctx.getStart().getLine());
        
        return scopeCode.createLineCode(sb.toString());
    }

    @Override
    public Code visitValue(ValueContext ctx) {
        final int indent = currentIndent;
        final boolean addIndent = indent != 0 || (countLeadingSpaces && !templateBlock) ;
        varNewLine = null;
        countLeadingSpaces = false;
        
        Code code = ctx.expression().accept(this);
        
        currentIndent = 0; // reset after expression since it could have been a proc
        
        Value_optionsContext vo = ctx.value_options();
        
        String source = code.toString();

        // 如果返回值是 void，那么不需要 print 语句
        SegmentCode sc = code instanceof SegmentCode ? (SegmentCode)code : null;
        if (sc != null && Void.TYPE.equals(sc.getKlass())) {
            // TODO uncomment once proper call stack is implemented
            // for now, the proc calls inside an inline-if are assumed to ignore newlines.
            /*if (conditionalInlineIf) {
                return scopeCode.createLineCode(
                        source + ";$out.$pop(true); // line: " + ctx.getStart().getLine(),
                        sc.proc);
            }*/
            
            if (inlineIfChildCount != 0)
                inlineIfCurrentCount++;
            
            return scopeCode.createLineCode(
                    source + "; // line: " + ctx.getStart().getLine(),
                    sc.proc);
        }
        
        Value_iterationContext vi = ctx.value_iteration();
        if (vi == null) {
            if (vo == null) {
                if ("null".equals(source))
                    return newValueCode("(Object)null", ctx, addIndent, indent);
                
                if (((TerminalNode) ctx.getChild(0)).getSymbol().getType() == JetTemplateParser.VALUE_ESCAPED_OPEN)
                    return newValueCode("JetUtils.asEscapeHtml(" + source + ")", ctx, addIndent, indent);
                
                return newValueCode(source, ctx, addIndent, indent);
            }
            
            return newValueCode(new StringBuilder()
                    .append(vo.O_KEY().getText()).append('(')
                    .append(source)
                    .append(',').append(' ').append(vo.expression().getText())
                    .append(')').toString(), ctx, addIndent, indent);
        }
        
        String optionValue = vo == null ? null : vo.expression().getText();
        
        StringBuilder sb = new StringBuilder();
        
        BlockCode bc = scopeCode.createBlockCode(16);
        bc.readNextNewLine = bc.singlelineBlockWithEnd = 
                optionValue == null || optionValue.indexOf("\\n") == -1;
        
        scopeCode = scopeCode.push();
        
        SegmentCode typeCode = (SegmentCode)vi.type().accept(this);
        scopeCode.define("it", typeCode.getTypedKlass());
        
        final String type = typeCode.toString(),
                var = getUid("i"),
                text = vi.expression().accept(this).toString();
        
        final int leftParen = text.indexOf('('),
                doubleColon = text.indexOf("::");
        
        final boolean pushIndent = !bc.singlelineBlockWithEnd && indent != 0;
        
        varNewLine = bc.singlelineBlockWithEnd ? null : var;
        
        scopeCode = scopeCode.pop();
        
        bc.addLine("int " + var + " = 0;");
        bc.addLine("for (" + type + " it : " + source + ") {");
        
        if (vo != null) {
            bc.addLine(new StringBuilder().append("  ").append(vo.O_KEY().getText())
                    .append("($out, it")
                    .append(',').append(' ').append(optionValue)
                    .append(',').append(' ').append(var)
                    .append(')').append(';').toString());
        }
        
        if (pushIndent)
            bc.addLine("  $out.indent(" + indent + ");");
        else if (indent != 0)
            bc.addLine("  $out.printIndent(" + indent + ");");
        
        sb.append("  ");
        if (doubleColon == -1) {
            sb.append(text.substring(0, leftParen + 1));
        } else {
            String name = text.substring(0, doubleColon);
            TemplateClassCode.Import imp = tcc.getImport(name);
            
            if (imp == null) {
                reportError("Make sure you reference the correct import: " + name + 
                        " (Replace the dot with an underscore)", ctx);
            }
            
            sb.append(name).append(importedProcSuffix).append('.')
                // procName with (
                .append(text.substring(doubleColon + 2, leftParen + 1));
        }
            
        sb.append("$out, it, ") // item as first param
            .append(text.substring(leftParen + 1))
            .append("; // line: ").append(ctx.getStart().getLine());
        
        bc.addLine(sb.toString());
        
        if (pushIndent)
            bc.addLine("  $out.indent(-" + indent + ");");
        
        bc.addLine("  " + var + "++;");
        bc.addLine("}");
        
        if (inlineIfChildCount != 0)
            inlineIfCurrentCount++;
        
        return bc;
    }
    

    @Override
    public Code visitValue_iteration(Value_iterationContext ctx)
    {
        return ctx.expression().accept(this);
    }

    @Override
    public Code visitValue_options(Value_optionsContext ctx)
    {
        return ctx.expression().accept(this);
    }
    
    @Override
    public Code visitAlt_block_directive(Alt_block_directiveContext ctx)
    {
        return ctx.getChild(0).accept(this);
    }

    @Override
    public Code visitBlock_directive(Block_directiveContext ctx)
    {
        return ctx.getChild(0).accept(this);
    }
    
    @Override
    public Code visitControl_directive(Control_directiveContext ctx)
    {
        ParseTree child = ctx.getChild(0);
        
        if (!validBreakOrContinue && !(child instanceof Stop_directiveContext)) {
            reportError("The break/continue directive cannot exist outside a for loop.", 
                    ctx);
        }
            
        return child.accept(this);
    }

    @Override
    public Code visitContext_directive(Context_directiveContext ctx)
    {
        if (!validContextDirective) {
            reportError("Directives relying on context are not allowed inside a proc.", 
                    ctx);
        }
        
        return ctx.getChild(0).accept(this);
    }
    
    @Override
    public Code visitProc_content_directive(Proc_content_directiveContext ctx)
    {
        varNewLine = null;
        countLeadingSpaces = false;
        
        return ctx.getChild(0).accept(this);
    }

    @Override
    public Code visitDirective(DirectiveContext ctx) {
        varNewLine = null;
        countLeadingSpaces = false;
        
        return ctx.getChild(0).accept(this);
    }
    
    @Override
    public Code visitCall_directive(Call_directiveContext ctx)
    {
        Expression_listContext expression_list = ctx.expression_list();
        SegmentListCode segmentListCode = (expression_list == null) ? SegmentListCode.EMPTY : (SegmentListCode) expression_list.accept(this);
        Class<?>[] parameterTypes = segmentListCode.getParameterTypes();
        
        String text = ctx.getChild(0).getText();
        String name = text.substring(5, text.length() - 1).trim();
        
        final MacroCode macroCode = macroMap == null ? null : macroMap.get(name);
        if (macroCode == null)
            throw reportError("Undefined function or arguments mismatch: " + getMethodSignature(name, parameterTypes) + ".", ctx);
        
        // macro 参数匹配
        SegmentListCode defineListCode = macroCode.getDefineListCode();
        int size = (defineListCode == null) ? 0 : defineListCode.size();
        if (parameterTypes.length != size) {
            throw reportError("Arguments mismatch for #macro " + getMethodSignature(name, parameterTypes) + ".", ctx);
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            if (!ClassUtils.isAssignable(parameterTypes[i], defineListCode.getChild(i).getKlass())) {
                throw reportError("Arguments mismatch for #macro " + getMethodSignature(name, parameterTypes) + ".", ctx);
            }
        }

        // 生成 macro 调用 code
        StringBuilder sb = new StringBuilder(64);
        
        int indent = currentIndent;
        if (indent != 0) {
            sb.append("$out.indent(").append(indent).append(");");
        }
        
        sb.append("$macro_").append(name);
        sb.append("($ctx");
        if (segmentListCode.size() > 0) {
            sb.append(',').append(' ').append(segmentListCode.toString());
        }
        sb.append(')');
        
        if (indent != 0) {
            sb.append(";$out.indent(-").append(indent).append(')');
        }
        
        // TODO uncomment once proper call stack is implemented
        // for now, the proc calls inside an inline-if are assumed to ignore newlines.
        /*if (conditionalInlineIf) {
            sb.append(";$out.$pop(true)");
        }*/
        
        return scopeCode.createLineCode(
                sb.append("; // line: ").append(ctx.getStart().getLine()).toString(), 
                true);
    }
    
    @Override
    public Code visitArg_decl_expression_list(Arg_decl_expression_listContext ctx)
    {
        Define_expressionContext define_expression = ctx.define_expression();
        List<Optional_define_expressionContext> opt_def_expression_list = 
                ctx.optional_define_expression();
        SegmentListCode code = new SegmentListCode(1 + opt_def_expression_list.size());

        code.addChild((SegmentCode) define_expression.accept(this));
        
        int optionalCount = 0;
        for (Optional_define_expressionContext opt_def_expression : opt_def_expression_list) {
            DefineExpressionCode dec = (DefineExpressionCode)opt_def_expression.accept(this);
            if (optionalCount != 0) {
                if (dec.expr == null)
                    reportError("All optional method args must be declared last.", ctx);
                
                optionalCount++;
            } else if (dec.expr != null) {
                optionalCount++;
            }
            code.addChild(dec);
        }
        
        code.optionalCount = optionalCount;
        
        return code;
    }
    
    @Override
    public Code visitOptional_define_expression(Optional_define_expressionContext ctx) {
        return ctx.getChild(0).accept(this);
    }
    
    @Override
    public Code visitAssign_expression(Assign_expressionContext ctx) {
        String name = assert_java_identifier(ctx.IDENTIFIER(), true);
        SegmentCode code = (SegmentCode) ctx.expression().accept(this);
        TypeContext type = ctx.type();
        if (type == null)
            return new DefineExpressionCode(code.getTypedKlass(), name, ctx, code);

        SegmentCode c = (SegmentCode) type.accept(this);
        TypedKlass lhs = c.getTypedKlass();
        // 进行赋值语句类型检查
        if (!ClassUtils.isAssignable(lhs.getKlass(), code.getKlass())) { // 是否支持正常赋值
            if (!ClassUtils.isAssignable(code.getKlass(), lhs.getKlass())) { // 是否支持强制类型转换
                throw reportError("Type mismatch: cannot convert from " + code.getTypedKlass().toString() + " to " + lhs.toString(), ctx);
            }
        }
        
        return new DefineExpressionCode(lhs, name, ctx, code);
    }

    @Override
    public Code visitDefine_directive(Define_directiveContext ctx) {
        SegmentListCode define_expression_list = (SegmentListCode) ctx.define_expression_list().accept(this);
        BlockCode code = scopeCode.createBlockCode(define_expression_list.size());

        for (SegmentCode node : define_expression_list.getChildren()) {
            DefineExpressionCode c = (DefineExpressionCode) node;
            String name = c.getName();

            if (!scopeCode.define(name, c.getTypedKlass())) {
                throw reportError("Duplicate local variable " + name, c.getNode());
            }

            String typeName = c.getTypedKlass().asBoxedTypedKlass().toString();
            code.addLine(typeName + " " + name + " = (" + typeName + ") " + Code.CONTEXT_NAME + ".get(\"" + name + "\"); // line: " + c.getNode().getStart().getLine());
        }
        return code;
    }

    @Override
    public Code visitDefine_expression_list(Define_expression_listContext ctx) {
        List<Define_expressionContext> define_expression_list = ctx.define_expression();
        SegmentListCode code = new SegmentListCode(define_expression_list.size());

        for (Define_expressionContext define_expression : define_expression_list) {
            code.addChild((SegmentCode) define_expression.accept(this));
        }
        return code;
    }

    @Override
    public Code visitDefine_expression(Define_expressionContext ctx) {
        SegmentCode code = (SegmentCode) ctx.type().accept(this);
        String name = assert_java_identifier(ctx.IDENTIFIER(), true);
        return new DefineExpressionCode(code.getTypedKlass(), name, ctx);
    }

    @Override
    public Code visitSet_directive(Set_directiveContext ctx) {
        List<Set_expressionContext> set_expression_list = ctx.set_expression();
        BlockCode code = scopeCode.createBlockCode(set_expression_list.size());

        for (Set_expressionContext node : set_expression_list) {
            Code c = node.accept(this);
            if (c != null) {
                code.addChild(c);
            }
        }
        return code;
    }

    @Override
    public Code visitSet_expression(Set_expressionContext ctx) {
        String name = assert_java_identifier(ctx.IDENTIFIER(), true);
        SegmentCode code = (SegmentCode) ctx.expression().accept(this);

        boolean defining = false; // 是否同时定义一个变量

        TypedKlass lhs = null; // 变量类型
        TypeContext type = ctx.type();
        if (type != null) {
            defining = true;
            // 定义一个变量
            SegmentCode c = (SegmentCode) type.accept(this);
            lhs = c.getTypedKlass();
            if (!scopeCode.define(name, lhs)) {
                throw reportError("Duplicate local variable " + name, ctx.IDENTIFIER());
            }
        } else {
            // 直接赋值，如果变量没有定义，则先定义
            lhs = scopeCode.resolve(name, false);
            defining = (lhs == null);
            if (defining) {
                lhs = code.getTypedKlass();
                scopeCode.define(name, lhs);
            }
        }

        // 进行赋值语句类型检查
        if (!ClassUtils.isAssignable(lhs.getKlass(), code.getKlass())) { // 是否支持正常赋值
            if (!ClassUtils.isAssignable(code.getKlass(), lhs.getKlass())) { // 是否支持强制类型转换
                throw reportError("Type mismatch: cannot convert from " + code.getTypedKlass().toString() + " to " + lhs.toString(), ctx);
            }
        }

        BlockCode c = scopeCode.createBlockCode(2);
        StringBuilder sb = new StringBuilder();
        String source = lhs.getSource();
        if (defining)
            sb.append(source).append(' ');
        
        sb.append(name).append(" = ");
        
        if (lhs.getKlass() != code.getKlass()) // cast
            sb.append('(').append(source).append(')');
        
        sb.append(code.toString()).append("; // line: ").append(ctx.getStart().getLine());
        
        c.addLine(sb.toString());
        if (validContextDirective)
            c.addLine(Code.CONTEXT_NAME + ".put(\"" + name + "\", " + name + ");");
        return c;
    }
    
    @Override
    public Code visitPut_directive(Put_directiveContext ctx) {
        List<ExpressionContext> expression_list = ctx.expression();
        int size = expression_list.size();
        if (size == 0 || size % 2 == 1) {
            throw reportError("Mismatched arguments count for #put directive", ctx);
        }

        BlockCode code = scopeCode.createBlockCode(size / 2);
        for (int i = 0; i < size; i += 2) {
            SegmentCode name = (SegmentCode) expression_list.get(i).accept(this);
            SegmentCode value = (SegmentCode) expression_list.get(i + 1).accept(this);
            if (!String.class.equals(name.getKlass())) {
                throw reportError("The argument type can't cast to String.class for #put directive", name.getNode());
            }
            assert_not_void_expression(value);
            code.addLine(Code.CONTEXT_NAME + ".putAsParents(" + name.toString() + ", " + value.toString() + "); // line: " + ctx.getStart().getLine());
        }
        return code;
    }
    
    private BlockCode fillIfCode(final BlockCode code, int line, 
            boolean checkProc, 
            SegmentCode expr_code, BlockContext blockContext)
    {
        code.addLine("if (" + get_if_expression_source(expr_code) + ") { // line: " + line);
        scopeCode = scopeCode.push();
        
        BlockCode c = (BlockCode)blockContext.accept(this);
        code.addChild(c);
        scopeCode = scopeCode.pop();
        code.addLine("}");
        
        return checkProc && c.wrapsProc() ? c : null;
    }
    
    private Code visitIf(final ParserRuleContext ctx, 
            BlockCode code, 
            final int line, final int lineStop, final boolean simpleInlineIfElse, 
            final BlockContext block, final BlockContext blockElse, 
            final List<? extends ParserRuleContext> elseif_directive_list,
            final ParserRuleContext else_directive, 
            final ParserRuleContext expression) {
        final boolean trimIfComments = this.trimIfComments; // push
        final int indent = currentIndent; // push
        
        code.readNextNewLine = true;
        
        code.singlelineBlockWithEnd = line == lineStop;
        if (trimDirectiveComments)
            trimComments = this.trimIfComments = !code.singlelineBlockWithEnd;
        
        BlockCode wrapsProc = fillIfCode(code, line, !code.singlelineBlockWithEnd,
                (SegmentCode)expression.accept(this), block);
        
        // elseif ...
        for (ParserRuleContext elseif_directive : elseif_directive_list) {
            currentIndent = conditionalInlineIf ? 0 : indent;
            code.addChild(elseif_directive.accept(this));
            wrapsProc = null;
        }
        
        // else ...
        if (else_directive != null) {
            trimElseComments = this.trimIfComments; // necessary since for loop has else
            if (simpleInlineIfElse) {
                currentIndent = indent;
                inlineIfCurrentCount = 0;
                inlineIfChildCount = blockElse.children.size();
            } else {
                currentIndent = conditionalInlineIf ? 0 : indent;
            }
            code.addChild(else_directive.accept(this));
            wrapsProc = null;
        }
        
        if (wrapsProc != null) {
            this.procInsideIf = wrapsProc;
        }
        
        if (simpleInlineIfElse)
            inlineIfChildCount = 0;
        
        currentIndent = 0; // reset
        trimComments = this.trimIfComments = trimIfComments; // pop

        return code;
    }
    
    private boolean inspectInlineIf(boolean hasNewline, 
            BlockContext blockIf, BlockContext blockElse) {
        if (blockIf.children.get(0) instanceof DirectiveContext)
            return false;
        
        final boolean ok;
        if (hasNewline) {
            ok = blockElse == null || 
                    blockElse.children == null || 
                    blockElse.children.isEmpty() || 
                    !(blockElse.children.get(0) instanceof DirectiveContext);
            
            if (ok) {
                inlineIfCurrentCount = 0;
                inlineIfChildCount = blockIf.children.size();
            }
        } else {
            ok = blockElse != null && 
                    blockElse.children != null && 
                    !blockElse.children.isEmpty() && 
                    !(blockElse.children.get(0) instanceof DirectiveContext);
            
            if (ok) {
                tempIgnoreNewline = true;
                ignoreNewLine = true;
                currentIndent = 0;
            }
        }
        
        return ok;
    }
    
    @Override
    public Code visitAlt_if_directive(Alt_if_directiveContext ctx) {
        final int line = ctx.getStart().getLine(),
                lineStop = ctx.getStop().getLine();
        final boolean hasNewline = ctx.TEXT_NEWLINE() != null;
        final BlockCode code = scopeCode.createBlockCode(16);
        
        Alt_else_directiveContext else_directive = ctx.alt_else_directive();
        List<Alt_elseif_directiveContext> elseif_directive_list = ctx.alt_elseif_directive();
        
        BlockContext block = ctx.block(), blockElse = null;
        boolean simpleInlineIfElse = false, println = false;
        if (!ignoreNewLine && !conditionalInlineIf && !nestedContext && 
                line == lineStop && block.children != null) {
            if (!indentInlineIf) {
                println = hasNewline;
            } else if (elseif_directive_list.isEmpty() && inspectInlineIf(hasNewline, block, 
                    else_directive == null ? null : (blockElse = else_directive.block()))) {
                simpleInlineIfElse = hasNewline;
            } else {
                conditionalInlineIf = true;
                code.addLine("$out.$push(" + currentIndent + ");");
            }
        }
        
        visitIf(ctx, code, line, lineStop, simpleInlineIfElse, block, blockElse, 
                elseif_directive_list, else_directive, ctx.expression());
        
        if (println) {
            code.addLine("$out.println();");
            countLeadingSpaces = true;
        } else if (!hasNewline) {
            countLeadingSpaces = false;
        } else if (tempIgnoreNewline) {
            tempIgnoreNewline = false;
            ignoreNewLine = false;
            countLeadingSpaces = true;
        } else if (conditionalInlineIf) {
            conditionalInlineIf = false;
            code.addLine("$out.$pop(false);");
            countLeadingSpaces = true;
        } else {
            countLeadingSpaces = !ignoreNewLine;
        }
        
        return code;
    }
    
    @Override
    public Code visitIf_directive(If_directiveContext ctx) {
        final int line = ctx.getStart().getLine(),
                lineStop = ctx.getStop().getLine();
        final boolean hasNewline = ctx.TEXT_NEWLINE() != null;
        final BlockCode code = scopeCode.createBlockCode(16);
        
        Else_directiveContext else_directive = ctx.else_directive();
        List<Elseif_directiveContext> elseif_directive_list = ctx.elseif_directive();
        
        BlockContext block = ctx.block(), blockElse = null;
        boolean simpleInlineIfElse = false, println = false;
        if (!ignoreNewLine && !conditionalInlineIf && !nestedContext && 
                line == lineStop && block.children != null) {
            if (!indentInlineIf) {
                println = hasNewline;
            } else if (elseif_directive_list.isEmpty() && inspectInlineIf(hasNewline, block, 
                    else_directive == null ? null : (blockElse = else_directive.block()))) {
                simpleInlineIfElse = hasNewline;
            } else {
                conditionalInlineIf = true;
                code.addLine("$out.$push(" + currentIndent + ");");
            }
        }
        
        visitIf(ctx, code, line, lineStop, simpleInlineIfElse, block, blockElse, 
                elseif_directive_list, else_directive, ctx.expression());
        
        if (println) {
            code.addLine("$out.println();");
            countLeadingSpaces = true;
        } else if (!hasNewline) {
            countLeadingSpaces = false;
        } else if (tempIgnoreNewline) {
            tempIgnoreNewline = false;
            ignoreNewLine = false;
            countLeadingSpaces = true;
        } else if (conditionalInlineIf) {
            conditionalInlineIf = false;
            code.addLine("$out.$pop(false);");
            countLeadingSpaces = true;
        } else {
            countLeadingSpaces = !ignoreNewLine;
        }
        
        return code;
    }
    
    private BlockCode fillElseIfCode(BlockCode code, int line, 
            SegmentCode expr_code, BlockContext blockContext)
    {
        code.addLine("else if (" + get_if_expression_source(expr_code) + ") { // line: " + line);
        scopeCode = scopeCode.push();
        code.addChild(blockContext.accept(this));
        scopeCode = scopeCode.pop();
        code.addLine("}");
        
        return code;
    }
    
    @Override
    public Code visitAlt_elseif_directive(Alt_elseif_directiveContext ctx)
    {
        trimComments = trimIfComments;
        return fillElseIfCode(scopeCode.createBlockCode(16), 
                ctx.getStart().getLine(),
                (SegmentCode) ctx.expression().accept(this), ctx.block());
    }

    @Override
    public Code visitElseif_directive(Elseif_directiveContext ctx) {
        trimComments = trimIfComments;
        return fillElseIfCode(scopeCode.createBlockCode(16), 
                ctx.getStart().getLine(),
                (SegmentCode) ctx.expression().accept(this), ctx.block());
    }
    
    private BlockCode fillElseCode(BlockCode code, int line, 
            boolean isParentIf, BlockContext blockContext)
    {
        if (isParentIf) {
            code.addLine("else { // line: " + line);
        }

        scopeCode = scopeCode.push();
        code.addChild(blockContext.accept(this));
        scopeCode = scopeCode.pop();

        if (isParentIf) {
            code.addLine("}");
        }
        
        return code;
    }
    
    @Override
    public Code visitAlt_else_directive(Alt_else_directiveContext ctx)
    {
        trimComments = trimElseComments;
        return fillElseCode(scopeCode.createBlockCode(16), 
                ctx.getStart().getLine(),
                ctx.getParent() instanceof Alt_if_directiveContext, ctx.block());
    }

    @Override
    public Code visitElse_directive(Else_directiveContext ctx) {
        trimComments = trimElseComments;
        return fillElseCode(scopeCode.createBlockCode(16), 
                ctx.getStart().getLine(),
                ctx.getParent() instanceof If_directiveContext, ctx.block());
    }
    
    private BlockCode fillForCode(BlockCode code, 
            int line, int lineStop,  
            For_expressionContext for_expr_context, BlockContext blockContext,
            ParseTree else_directive)
    {
        final boolean validBreakOrContinue = this.validBreakOrContinue; // push
        final boolean trimForComments = this.trimForComments; // push
        final int indent = currentIndent; // push
        
        this.validBreakOrContinue = true;
        
        code.readNextNewLine = code.singlelineBlockWithEnd = line == lineStop;
        if (trimDirectiveComments)
            trimComments = this.trimForComments = !code.singlelineBlockWithEnd;
        
        TypeContext typeContext;
        Code for_block, for_else_block = null;
        if (!validContextDirective && (typeContext = for_expr_context.type()) != null) {
            SegmentCode typeCode = (SegmentCode)typeContext.accept(this);
            String type = typeCode.toString(),
                    value = for_expr_context.accept(this).toString(),
                    var = for_expr_context.IDENTIFIER().getText(),
                    ivar = var + "$$i";
            
            if (scopeCode.define(ivar, TypedKlass.INT))
                code.addLine("int " + ivar + " = 0;");
            else
                code.addLine(ivar + " = 0;");
            scopeCode = scopeCode.push();
            scopeCode.define(var, typeCode.getTypedKlass());
            for_block = blockContext.accept(this);
            scopeCode = scopeCode.pop();
            
            this.validBreakOrContinue = validBreakOrContinue; // pop
            
            // for-else
            if (else_directive != null) {
                trimElseComments = this.trimForComments;
                currentIndent = indent;
                for_else_block = else_directive.accept(this);
            }
            
            currentIndent = 0; // reset
            trimComments = this.trimForComments = trimForComments; // pop
            
            code.addLine("for (" + type + " " + var + " : " + value + ") { // line: " + line);
            code.addChild(for_block);
            code.addLine("  " + ivar + "++;");
            code.addLine("}");
            
            // for else ...
            if (for_else_block != null) {
                code.addLine("if (" + ivar + " == 0) { // line: " + line);
                code.addChild(for_else_block);
                code.addLine("}");
            }
            
            return code;
        }
        
        String id_for = getUid("for");

        scopeCode = scopeCode.push();
        // 注意：for循环变量的作用域要放在 for 内部， 防止出现变量重定义错误
        ForExpressionCode for_expr_code = (ForExpressionCode) for_expr_context.accept(this);
        // for block
        forStack.push(id_for);
        for_block = blockContext.accept(this);
        forStack.pop();
        scopeCode = scopeCode.pop();
        
        this.validBreakOrContinue = validBreakOrContinue; // pop
        
        // for-else
        if (else_directive != null) {
            trimElseComments = this.trimForComments;
            currentIndent = indent;
            for_else_block = else_directive.accept(this);
        }
        
        currentIndent = 0; // reset
        trimComments = this.trimForComments = trimForComments; // pop
        
        // 生成代码
        String id_foritem = getUid("foritem");
        String typeName = for_expr_code.getKlassName();
        String itemName = for_expr_code.getName();

        if (validContextDirective)
            code.addLine("Object " + id_foritem + " = context.get(\"" + itemName + "\"); // save it");
        code.addLine("JetForIterator " + id_for + " = new JetForIterator(" + for_expr_code.toString() + ");");
        code.addLine("while (" + id_for + ".hasNext()) { // line: " + line);

        // class item = (class) it.next() ...
        code.addLine("  " + typeName + " " + itemName + " = (" + typeName + ") " + id_for + ".next();");
        
        if (validContextDirective)
            code.addLine("  context.put(\"" + itemName + "\", " + itemName + ");");
        code.addChild(for_block);
        code.addLine("}");
        if (validContextDirective)
            code.addLine("context.put(\"" + itemName + "\", " + id_foritem + "); // reset it");


        // for else ...
        if (for_else_block != null) {
            code.addLine("if (" + id_for + ".empty()) { // line: " + line);
            code.addChild(for_else_block);
            code.addLine("}");
        }

        return code;
    }
    
    @Override
    public Code visitAlt_for_directive(Alt_for_directiveContext ctx)
    {
        return fillForCode(scopeCode.createBlockCode(16), 
                ctx.getStart().getLine(), ctx.getStop().getLine(), 
                ctx.for_expression(), ctx.block(), ctx.alt_else_directive());
    }

    @Override
    public Code visitFor_directive(For_directiveContext ctx) {
        return fillForCode(scopeCode.createBlockCode(16), 
                ctx.getStart().getLine(), ctx.getStop().getLine(), 
                ctx.for_expression(), ctx.block(), ctx.else_directive());
    }

    @Override
    public Code visitFor_expression(For_expressionContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        SegmentCode code = (SegmentCode) ctx.expression().accept(this);
        assert_not_void_expression(code);

        TypedKlass resultKlass = null;
        TypeContext type = ctx.type();
        if (type != null) {
            // 手动定义返回变量的类型
            SegmentCode c = (SegmentCode) type.accept(this);
            resultKlass = c.getTypedKlass();
        } else {
            // 根据 expression 来进行自动类型推导
            Class<?> rhsKlass = code.getKlass();
            if (rhsKlass.isArray()) {
                resultKlass = TypedKlass.create(rhsKlass.getComponentType(), code.getTypeArgs());
            } else if (Map.class.isAssignableFrom(rhsKlass)) {
                resultKlass = TypedKlass.create(Map.Entry.class, code.getTypeArgs());
            } else if (Collection.class.isAssignableFrom(rhsKlass)) {
                if (code.getTypeArgs() != null && code.getTypeArgs().length == 1) {
                    resultKlass = code.getTypeArgs()[0];
                }
            }
        }

        if (resultKlass == null) {
            resultKlass = TypedKlass.Object;
        }

        // 必须是 Boxed 对象，因为需要强制类型转换 from iterator.next()
        resultKlass = resultKlass.asBoxedTypedKlass();

        if (!scopeCode.define(name, resultKlass) && (validContextDirective || null == ctx.type())) {
            throw reportError("Duplicate local variable " + name, ctx.IDENTIFIER());
        }

        return new ForExpressionCode(resultKlass, name, code.toString(), ctx);
    }

    @Override
    public Code visitBreak_directive(Break_directiveContext ctx) {
        assert_inside_of_for_directive(ctx, "#break");

        ExpressionContext expression = ctx.expression();
        String source;
        if (expression != null) {
            SegmentCode c = (SegmentCode) expression.accept(this);
            source = get_if_expression_source(c);
        } else {
            source = "true";
        }
        source = "if (" + source + ") break; // line: " + ctx.getStart().getLine();
        return scopeCode.createLineCode(source);
    }

    @Override
    public Code visitContinue_directive(Continue_directiveContext ctx) {
        assert_inside_of_for_directive(ctx, "#continue");

        ExpressionContext expression = ctx.expression();
        String source;
        if (expression != null) {
            SegmentCode c = (SegmentCode) expression.accept(this);
            source = get_if_expression_source(c);
        } else {
            source = "true";
        }
        source = "if (" + source + ") continue; // line: " + ctx.getStart().getLine();
        return scopeCode.createLineCode(source);
    }

    @Override
    public Code visitStop_directive(Stop_directiveContext ctx) {
        ExpressionContext expression = ctx.expression();
        String source;
        if (expression != null) {
            SegmentCode c = (SegmentCode) expression.accept(this);
            source = get_if_expression_source(c);
        } else {
            source = "true";
        }
        source = "if (" + source + ") return; // line: " + ctx.getStart().getLine();
        return scopeCode.createLineCode(source);
    }

    @Override
    public Code visitInclude_directive(Include_directiveContext ctx) {
        Expression_listContext expression_list = ctx.expression_list();
        SegmentListCode childrenCode = (SegmentListCode) expression_list.accept(this);
        if (childrenCode.size() > 2) {
            throw reportError("Arguments mismatch for #include directive.", ctx);
        }

        // argument 1: file
        SegmentCode fileCode = childrenCode.getChild(0);
        ExpressionContext fileExpression = expression_list.expression(0);
        if (!String.class.equals(fileCode.getKlass())) {
            throw reportError("Type mismatch: the first argument cannot convert from " + fileCode.getKlassName() + " to String", fileExpression);
        }

        // argument 2: parameters
        SegmentCode parametersCode = null;
        if (childrenCode.size() > 1) {
            parametersCode = childrenCode.getChild(1);
            if (!(Map.class.equals(parametersCode.getKlass()))) {
                throw reportError("Type mismatch: the second argument cannot convert from " + parametersCode.getKlassName() + " to Map", expression_list.expression(1));
            }
        }

        // 如果 file 是常量，那么进行 file.exists() 校验
        if (fileExpression instanceof Expr_constantContext) {
            String file = fileCode.toString();
            file = file.substring(1, file.length() - 1);
            file = StringEscapeUtils.unescapeJava(file);
            file = PathUtils.getAbsolutionName(resource.getName(), file);
            if (!engine.lookupResource(file)) {
                throw reportError("FileNotFoundException: " + file, fileExpression);
            }
        }

        // 生成代码
        StringBuilder source = new StringBuilder();
        source.append("JetUtils.asInclude($ctx, ");
        source.append(fileCode.toString());
        source.append(", (Map<String, Object>)");
        source.append((parametersCode != null) ? parametersCode.toString() : "null");
        source.append("); // line: ");
        source.append(ctx.getStart().getLine());
        return scopeCode.createLineCode(source.toString());
    }

    @Override
    public Code visitTag_directive(Tag_directiveContext ctx) {
        String text = ctx.getChild(0).getText();
        String name = text.substring(5, text.length() - 1).trim();

        TagCode tagCode = scopeCode.createTagCode();
        tagCode.setTagId(getUid("tag"));
        scopeCode = tagCode.getMethodCode();
        scopeCode.define(Code.CONTEXT_NAME, TypedKlass.JetContext);
        scopeCode.setBodyCode(ctx.content_block().accept(this)); // add body content
        scopeCode = scopeCode.pop();

        // finding tag function
        Expression_listContext expression_list = ctx.expression_list();
        SegmentListCode segmentListCode = (expression_list == null) ? SegmentListCode.EMPTY : (SegmentListCode) expression_list.accept(this);
        Class<?>[] parameterTypes = segmentListCode.getParameterTypes(JetTagContext.class);
        Method method = resolver.resolveTagMethod(name, parameterTypes);
        if (method == null) {
            throw reportError("Undefined tag definition: " + getMethodSignature(name, parameterTypes), ctx);
        }

        tagCode.setMethod(method);
        tagCode.setExpressionListCode(segmentListCode);
        return tagCode;
    }
    
    @Override
    public Code visitEmit_directive(Emit_directiveContext ctx)
    {
        return ctx.emit_block().accept(this);
    }

    @Override
    public Code visitProc_directive(Proc_directiveContext ctx)
    {
        String text = ctx.getChild(0).getText();
        String name = text.substring(1, text.length() - 1).trim();

        ProcCode procCode = new ProcCode();
        procCode.setName(name);

        scopeCode = procCode.getMethodCode();
        //scopeCode.define(Code.CONTEXT_NAME, TypedKlass.JetContext);

        // 处理参数
        Arg_decl_expression_listContext define_expression_list = ctx.arg_decl_expression_list();
        if (define_expression_list != null) {
            SegmentListCode define_list_code = (SegmentListCode) define_expression_list.accept(this);
            procCode.setDefineListCode(define_list_code);

            // 设置参数 Context
            for (SegmentCode node : define_list_code.getChildren()) {
                DefineExpressionCode c = (DefineExpressionCode) node;
                scopeCode.define(c.getName(), c.getTypedKlass());
            }
        }

        // 需要先定义 proc，这样可以支持 proc 的递归调用 (issue 102)
        if (procMap == null) {
            procMap = new HashMap<String, ProcCode>(8);
        }
        MacroCode old = procMap.put(name, procCode);
        if (old != null) {
            throw reportError("Duplicate proc definition " + name, ctx);
        }
        tcc.addProc(procCode);
        
        Proc_ignore_newline_blockContext inlineBlock;
        Proc_emit_blockContext emitBlock = ctx.proc_emit_block();
        if (emitBlock != null) {
            if (!procCode.hasArgs()) {
                reportError("The proc definition " + name + 
                        " must have at least one arg", ctx);
            }
            
            // emit method body
            procCode.returnType = emitBlock.TEXT_PLAIN().getText().trim();
            scopeCode.setBodyCode(emitBlock.accept(this));
        } else if (null != (inlineBlock=ctx.proc_ignore_newline_block())) { 
            // ignore newline
            scopeCode.setBodyCode(inlineBlock.accept(this)); // add body content
        } else {
            // 访问 proc body
            scopeCode.setBodyCode(ctx.proc_block().accept(this)); // add body content
        }
        scopeCode = scopeCode.pop();

        return Code.EMPTY;
    }

    @Override
    public Code visitMacro_directive(Macro_directiveContext ctx) {
        String text = ctx.getChild(0).getText();
        String name = text.substring(7, text.length() - 1).trim();

        MacroCode macroCode = scopeCode.createMacroCode();
        macroCode.setName(name);

        scopeCode = macroCode.getMethodCode();
        scopeCode.define(Code.CONTEXT_NAME, TypedKlass.JetContext);

        // 处理参数
        Define_expression_listContext define_expression_list = ctx.define_expression_list();
        if (define_expression_list != null) {
            SegmentListCode define_list_code = (SegmentListCode) define_expression_list.accept(this);
            macroCode.setDefineListCode(define_list_code);

            // 设置参数 Context
            for (SegmentCode node : define_list_code.getChildren()) {
                DefineExpressionCode c = (DefineExpressionCode) node;
                scopeCode.define(c.getName(), c.getTypedKlass());
            }
        }

        // 需要先定义 macro，这样可以支持 macro 的递归调用 (issue 102)
        if (macroMap == null) {
            macroMap = new HashMap<String, MacroCode>(8);
        }
        MacroCode old = macroMap.put(name, macroCode);
        if (old != null) {
            throw reportError("Duplicate macro definition " + name, ctx);
        }
        tcc.addMacro(macroCode);

        // 访问 macro body
        scopeCode.setBodyCode(ctx.content_block().accept(this)); // add body content
        scopeCode = scopeCode.pop();

        return Code.EMPTY;
    }

    @Override
    public Code visitInvalid_block_directive(Invalid_block_directiveContext ctx)
    {
        throw reportError("Misplaced or missing arguments for " + ctx.getText() + " directive.", ctx);
    }
    
    @Override
    public Code visitInvalid_control_directive(Invalid_control_directiveContext ctx)
    {
        throw reportError("Missing arguments for " + ctx.getText() + " directive.", ctx);
    }
    
    @Override
    public Code visitInvalid_context_directive(Invalid_context_directiveContext ctx)
    {
        throw reportError("Missing arguments for " + ctx.getText() + " directive.", ctx);
    }
    
    @Override
    public Code visitMisplaced_directive(Misplaced_directiveContext ctx) {
        throw reportError(ctx.getText() + " is a top-level only directive.", ctx);
    }

    @Override
    public Code visitExpr_group(Expr_groupContext ctx) {
        SegmentCode code = (SegmentCode) ctx.expression().accept(this);
        String source = "(" + code.toString() + ")";
        return new SegmentCode(code.getTypedKlass(), source, ctx);
    }

    @Override
    public Code visitExpr_constant(Expr_constantContext ctx) {
        return ctx.getChild(0).accept(this);
    }

    @Override
    public Code visitExpr_array_list(Expr_array_listContext ctx) {
        String source = "Collections.EMPTY_LIST";
        Expression_listContext expression_list = ctx.expression_list();
        if (expression_list != null) {
            Code code = expression_list.accept(this);
            source = "Arrays.asList(" + code.toString() + ")";
        }
        return new SegmentCode(List.class, source, ctx);
    }

    @Override
    public Code visitExpr_hash_map(Expr_hash_mapContext ctx) {
        String source = "Collections.EMPTY_MAP";
        Hash_map_entry_listContext hash_map_entry_list = ctx.hash_map_entry_list();
        if (hash_map_entry_list != null) {
            Code code = hash_map_entry_list.accept(this);
            source = "JetUtils.asMap(" + code.toString() + ")";
        }
        return new SegmentCode(Map.class, source, ctx);
    }

    @Override
    public Code visitHash_map_entry_list(Hash_map_entry_listContext ctx) {
        List<ExpressionContext> expression_list = ctx.expression();
        SegmentListCode code = new SegmentListCode(expression_list.size());
        for (ExpressionContext expression : expression_list) {
            code.addChild((SegmentCode) expression.accept(this));
        }
        return code;
    }

    @Override
    public Code visitExpr_identifier(Expr_identifierContext ctx) {
        String name = assert_java_identifier(ctx.IDENTIFIER(), false);
        
        // 特殊处理 for 变量
        if ("for".equals(name)) {
            assert_inside_of_for_directive(ctx, "Local variable \"for\"");
            // 强制映射成 JetForStatus $for
            String forStatus = forStack.peek(); // 取出 forStatus 的实际变量名
            return new SegmentCode(TypedKlass.JetForStatus, forStatus, ctx);
        }

        // 找到变量的类型
        TypedKlass resultKlass = scopeCode.resolve(name, false);
        if (resultKlass == null) {
            // 没有定义过，继续向上深度查找
            resultKlass = scopeCode.resolve(name, true);

            // 没有定义过，则查找全局定义
            if (resultKlass == null) {
                resultKlass = resolver.resolveVariable(name);
            }
            if (scopeCode.define(name, resultKlass, true)) {
                if (resultKlass == TypedKlass.Object) {
                    //removed unsed warning in 1.2.0
                    //log.warn("line " + ctx.getStart().getLine() + ": Implicit definition for context variable: " + resultKlass.toString() + " " + name);
                }
            }
        }

        return new SegmentCode(resultKlass, name, ctx);
    }

    @Override
    public Code visitExpr_field_access(Expr_field_accessContext ctx) {
        SegmentCode code = (SegmentCode) ctx.expression().accept(this);
        String name = ctx.IDENTIFIER().getText();

        assert_not_null_constantContext(code.getNode());

        // 进行类型推导，找到方法的返回类型
        code = code.asBoxedSegmentCode();
        Class<?> beanClass = code.getKlass();
        Member member = null;

        if ((!beanClass.isArray()) || (!"length".equals(name))) { // not array.length
            member = resolver.resolveProperty(beanClass, name);
            if (member == null) {
                // reportError
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                StringBuilder err = new StringBuilder(128);
                err.append("The method ");
                err.append("get" + name);
                err.append("() or ");
                err.append("is" + name);
                err.append("() is undefined for the type ");
                err.append(beanClass.getName());
                err.append('.');
                if (Object.class.equals(beanClass)) {
                    err.append("\n advise: ");
                    if (code.getNode() instanceof Expr_identifierContext) {
                        err.append("Please use #define(type ");
                        err.append(code.getNode().getText());
                        err.append(") to define variable type.");
                    } else {
                        err.append("Please use #set(type xxx = ");
                        err.append(code.getNode().getText());
                        err.append(") to define expression type.");
                    }
                }
                throw reportError(err.toString(), ctx.IDENTIFIER());
            }
        }

        boolean isSafeCall = globalSafeCall || "?.".equals(ctx.getChild(1).getText());

        // 生成code
        StringBuilder sb = new StringBuilder(64);
        TypedKlass resultKlass = null;
        String source;
        if (member instanceof Method) {
            Method method = (Method) member;
            if (securityManager != null) {
                securityManager.checkMemberAccess(method);
            }

            // special code for Map.get()
            // https://github.com/subchen/jetbrick-template/issues/100
            TypedKlass typedKlass = code.getTypedKlass();
            if ("get".equals(method.getName()) && member.getDeclaringClass() == Map.class) {
                if (typedKlass.getTypeArgs().length >=2) {
                    resultKlass = typedKlass.getTypeArgs()[1];
                }
            }
            if (resultKlass == null) {
                resultKlass = TypedKlassUtils.getMethodReturnTypedKlass(method);
            }
            if (method.getParameterTypes().length == 0) {
                // getXXX() or isXXX()
                if (isSafeCall) { // 安全调用，防止 NullPointException
                    boolean boxWhenSafeCall = false;
                    if (resultKlass.isPrimitive()) {
                        boxWhenSafeCall = true;
                        resultKlass = resultKlass.asBoxedTypedKlass();
                    }
                    source = resultKlass.getSource();
                    sb.append("((");
                    sb.append(code.toString());
                    sb.append("==null)?(");
                    sb.append(source);
                    sb.append(")null:");
                    if (boxWhenSafeCall) {
                        sb.append(source).append(".valueOf(");
                    }
                    sb.append(code.toString());
                    sb.append('.');
                    sb.append(method.getName());
                    sb.append("()");
                    if (boxWhenSafeCall) {
                        sb.append(')');
                    }
                    sb.append(')');
                } else {
                    sb.append(code.toString());
                    sb.append('.');
                    sb.append(method.getName());
                    sb.append("()");
                }
            } else {
                // get(String)
                if (isSafeCall) { // 安全调用，防止 NullPointException
                    boolean boxWhenSafeCall = false;
                    if (resultKlass.isPrimitive()) {
                        boxWhenSafeCall = true;
                        resultKlass = resultKlass.asBoxedTypedKlass();
                    }
                    source = resultKlass.getSource();
                    sb.append("((");
                    sb.append(code.toString());
                    sb.append("==null)?(");
                    sb.append(source);
                    sb.append(")null:");
                    if (boxWhenSafeCall) {
                        sb.append(source).append(".valueOf(");
                    }
                    sb.append(code.toString());
                    sb.append(".get(\"");
                    sb.append(name);
                    sb.append("\")");
                    if (boxWhenSafeCall) {
                        sb.append(')');
                    }
                    sb.append(')');
                } else {
                    sb.append(code.toString());
                    sb.append(".get(\"");
                    sb.append(name);
                    sb.append("\")");
                }
            }
        } else {
            if (member instanceof Field) {
                if (securityManager != null) {
                    securityManager.checkMemberAccess((Field) member);
                }
                resultKlass = TypedKlassUtils.getFieldTypedKlass((Field) member);
            } else {
                // array.length
                resultKlass = TypedKlass.create(Integer.TYPE);
            }
            if (isSafeCall) { // 安全调用，防止 NullPointException
                boolean boxWhenSafeCall = false;
                if (resultKlass.isPrimitive()) {
                    boxWhenSafeCall = true;
                    resultKlass = resultKlass.asBoxedTypedKlass();
                }
                source = resultKlass.getSource();
                sb.append("((");
                sb.append(code.toString());
                sb.append("==null)?(");
                sb.append(source);
                sb.append(")null:");
                if (boxWhenSafeCall) {
                    sb.append(source).append(".valueOf(");
                }
                sb.append(code.toString());
                sb.append('.');
                sb.append(name);
                if (boxWhenSafeCall) {
                    sb.append(')');
                }
                sb.append(')');
            } else {
                sb.append(code.toString());
                sb.append('.');
                sb.append(name);
            }
        }

        return new SegmentCode(resultKlass, sb.toString(), ctx);
    }

    @Override
    public Code visitExpr_method_invocation(Expr_method_invocationContext ctx) {
        // 处理参数
        Expression_listContext expression_list = ctx.expression_list();
        SegmentListCode segmentListCode = (expression_list == null) ? SegmentListCode.EMPTY : (SegmentListCode) expression_list.accept(this);
        Class<?>[] parameterTypes = segmentListCode.getParameterTypes();

        // 查找方法
        SegmentCode code = (SegmentCode) ctx.expression().accept(this);
        assert_not_null_constantContext(code.getNode());

        code = code.asBoxedSegmentCode();
        Class<?> beanClass = code.getKlass();
        String name = ctx.IDENTIFIER().getText();
        Method bean_method = resolver.resolveMethod(beanClass, name, parameterTypes);
        Method tool_method = (bean_method != null) ? null : resolver.resolveToolMethod(beanClass, name, parameterTypes);
        boolean tool_advanced = false;
        if (bean_method == null && tool_method == null) {
            tool_method = resolver.resolveToolMethod_advanced(beanClass, name, parameterTypes);
            tool_advanced = true;
        }
        if (bean_method == null && tool_method == null) {
            // reportError
            StringBuilder err = new StringBuilder(128);
            err.append("The method ").append(getMethodSignature(name, parameterTypes));
            err.append(" is undefined for the type ");
            err.append(beanClass.getName());
            err.append('.');
            if (Object.class.equals(beanClass)) {
                err.append("\n advise: ");
                if (code.getNode() instanceof Expr_identifierContext) {
                    err.append("Please use #define(type ");
                    err.append(code.getNode().getText());
                    err.append(") to define variable type.");
                } else {
                    err.append("Please use #set(type xxx = ");
                    err.append(code.getNode().getText());
                    err.append(") to define expression type.");
                }
            }
            throw reportError(err.toString(), ctx.IDENTIFIER());
        }

        boolean isSafeCall = globalSafeCall || "?.".equals(ctx.getChild(1).getText());

        // 得到方法的返回类型
        Method method = (bean_method == null) ? tool_method : bean_method;
        if (securityManager != null) {
            securityManager.checkMemberAccess(method);
        }

        TypedKlass resultKlass = TypedKlassUtils.getMethodReturnTypedKlass(method);
        boolean boxWhenSafeCall = resultKlass.isPrimitive();
        if (isSafeCall) {
            resultKlass = resultKlass.asBoxedTypedKlass();
        }

        // 生成code
        StringBuilder sb = new StringBuilder(64);
        String source;
        if (tool_method != null) {
            // tool method
            if (isSafeCall) { // 安全调用，防止 NullPointException
                source = resultKlass.getSource();
                sb.append('(');
                sb.append(code.toString());
                sb.append("==null)?(");
                sb.append(source);
                sb.append(")null:");
                if (boxWhenSafeCall) {
                    sb.append(source).append(".valueOf(");
                }
            }
            sb.append(ClassUtils.getShortClassName(tool_method.getDeclaringClass()));
            sb.append('.');
            sb.append(name);
            sb.append('(');
            sb.append(code.toString());
            if (tool_advanced) {
                sb.append(",$ctx");
            }
            if (segmentListCode.size() > 0) {
                sb.append(',');
            }
        } else {
            if (isSafeCall) { // 安全调用，防止 NullPointException
                source = resultKlass.getSource();
                sb.append('(');
                sb.append(code.toString());
                sb.append("==null)?(");
                sb.append(source);
                sb.append(")null:");
                if (boxWhenSafeCall) {
                    sb.append(source).append(".valueOf(");
                }
            }
            sb.append(code.toString());
            sb.append('.');
            sb.append(name);
            sb.append('(');
        }
        if (segmentListCode.size() > 0) {
            sb.append(segmentListCode.toString());
        }
        sb.append(')');

        if (isSafeCall) { // 为了安全起见，用()包起来
            if (boxWhenSafeCall) {
                sb.append(')');
            }
            sb.insert(0, '(').append(')');
        }

        return new SegmentCode(resultKlass, sb.toString(), ctx);
    }
    
    private String resolveImportedName(String import_ref, String procName, 
            Expr_function_callContext ctx) {
        String name = import_ref.substring(0, import_ref.length() - 2);
        TemplateClassCode.Import imp = tcc.getImport(name);
        
        if (imp == null) {
            reportError("Make sure you reference the correct import: " + name + 
                    " (Replace the dot with an underscore)", ctx);
        }
        
        return name + importedProcSuffix + "." + procName;
        //return imp.fqcn + "." + procName;
    }
    
    private SegmentCode newProcCode(String name, 
            SegmentListCode segmentListCode, 
            Expr_function_callContext ctx)
    {
        final StringBuilder sb = new StringBuilder(64);
        final int indent = currentIndent;
        
        boolean voidType = false;
        if (ctx.getParent() instanceof ValueContext) {
            int underscore = name.indexOf('_');
            voidType = underscore == -1 || !NON_VOID_CALL.containsKey(
                    name.substring(0, underscore));
        }
        
        if (voidType && indent != 0) {
            sb.append("$out.indent(").append(indent).append(");");
        }
        
        sb.append(name);
        
        if (voidType) {
            sb.append("($out");
            
            if (segmentListCode.size() > 0) {
                sb.append(',').append(' ').append(segmentListCode.toString());
            }
        } else {
            if (segmentListCode.size() == 0)
                reportError("Missing arguments for the function call: " + name, ctx);
            
            sb.append('(').append(segmentListCode.toString());
        }
        
        sb.append(')');
        
        if (voidType && indent != 0) {
            sb.append(";$out.indent(-").append(indent).append(')');
        }
        
        return new SegmentCode(voidType ? TypedKlass.VOID : TypedKlass.Object, 
                sb.toString(), ctx, true);
    }

    @Override
    public Code visitExpr_function_call(Expr_function_callContext ctx) {
        // 处理参数
        Expression_listContext expression_list = ctx.expression_list();
        SegmentListCode segmentListCode = (expression_list == null) ? SegmentListCode.EMPTY : (SegmentListCode) expression_list.accept(this);
        Class<?>[] parameterTypes = segmentListCode.getParameterTypes();

        // 查找方法
        String name = ctx.IDENTIFIER().getText();
        
        TerminalNode import_ref = ctx.IMPORT_REF();
        if (import_ref != null) {
            return newProcCode(resolveImportedName(import_ref.getText(), name, ctx), 
                    segmentListCode, ctx);
        }

        // 查找扩展方法
        boolean advanced = false;
        Method method = resolver.resolveFunction(name, parameterTypes);
        if (method == null) {
            method = resolver.resolveFunction_advanced(name, parameterTypes);
            advanced = true;
        }
        if (method == null) {
            // 生成 proc 调用 code
            return newProcCode(name, segmentListCode, ctx);
        }
        
        if (securityManager != null) {
            securityManager.checkMemberAccess(method);
        }

        // 生成code
        StringBuilder sb = new StringBuilder(64);
        sb.append(ClassUtils.getShortClassName(method.getDeclaringClass()));
        sb.append('.');
        sb.append(name);
        sb.append('(');
        if (advanced) {
            sb.append("$ctx");
        }
        if (segmentListCode.size() > 0) {
            if (advanced) sb.append(',');
            sb.append(segmentListCode.toString());
        }
        sb.append(')');

        TypedKlass typedKlass = TypedKlassUtils.getMethodReturnTypedKlass(method);
        return new SegmentCode(typedKlass, sb.toString(), ctx);
    }

    @Override
    public Code visitExpr_static_field_access(Expr_static_field_accessContext ctx) {
        SegmentCode static_type_name_code = (SegmentCode) ctx.static_type_name().accept(this);
        String typeName = static_type_name_code.toString();
        String name = ctx.IDENTIFIER().getText();

        Class<?> beanClass = resolver.resolveClass(typeName);
        if (beanClass == null) {
            throw reportError("java.lang.ClassNotFoundException: " + typeName, static_type_name_code.getNode());
        }
        Field field = resolver.resolveStaticField(beanClass, name);
        if (field == null) {
            throw reportError(name + " is not a static field for type " + beanClass.getName(), ctx.IDENTIFIER());
        }

        if (securityManager != null) {
            securityManager.checkMemberAccess(field);
        }

        String source = ClassUtils.getShortClassName(field.getDeclaringClass()) + '.' + name;
        TypedKlass resultKlass = TypedKlassUtils.getFieldTypedKlass(field);
        return new SegmentCode(resultKlass, source, ctx);
    }

    @Override
    public Code visitExpr_static_method_invocation(Expr_static_method_invocationContext ctx) {
        SegmentCode static_type_name_code = (SegmentCode) ctx.static_type_name().accept(this);
        String typeName = static_type_name_code.toString();
        String name = ctx.IDENTIFIER().getText();

        // 获取静态 Class
        Class<?> beanClass = resolver.resolveClass(typeName);
        if (beanClass == null) {
            throw reportError("java.lang.ClassNotFoundException: " + typeName, static_type_name_code.getNode());
        }

        // 处理参数
        Expression_listContext expression_list = ctx.expression_list();
        SegmentListCode segmentListCode = (expression_list == null) ? SegmentListCode.EMPTY : (SegmentListCode) expression_list.accept(this);
        Class<?>[] parameterTypes = segmentListCode.getParameterTypes();

        Method method = resolver.resolveStaticMethod(beanClass, name, parameterTypes);
        if (method == null) {
            throw reportError("The static method " + getMethodSignature(name, parameterTypes) + " is undefined for the type " + beanClass.getName(), ctx.IDENTIFIER());
        }

        if (securityManager != null) {
            securityManager.checkMemberAccess(method);
        }

        // 生成代码
        StringBuilder sb = new StringBuilder();
        sb.append(ClassUtils.getShortClassName(method.getDeclaringClass()));
        sb.append('.');
        sb.append(name);
        sb.append('(');
        sb.append(segmentListCode.toString());
        sb.append(')');

        TypedKlass resultKlass = TypedKlassUtils.getMethodReturnTypedKlass(method);
        return new SegmentCode(resultKlass, sb.toString(), ctx);
    }

    @Override
    public Code visitStatic_type_name(Static_type_nameContext ctx) {
        List<TerminalNode> name_list = ctx.IDENTIFIER();
        StringBuilder sb = new StringBuilder();
        for (TerminalNode node : name_list) {
            if (sb.length() > 0) {
                sb.append('.');
            }
            sb.append(node.getText());
        }
        return new SegmentCode(TypedKlass.NULL, sb.toString(), ctx);
    }

    @Override
    public Code visitExpr_array_get(Expr_array_getContext ctx) {
        SegmentCode lhs = (SegmentCode) ctx.expression(0).accept(this);
        SegmentCode rhs = (SegmentCode) ctx.expression(1).accept(this);

        assert_not_null_constantContext(lhs.getNode());
        assert_not_null_constantContext(rhs.getNode());

        boolean isSafeCall = globalSafeCall || "?".equals(ctx.getChild(1).getText());

        Class<?> lhsKlass = lhs.getKlass();
        String source;
        if (lhsKlass.isArray()) {
            if (!ClassUtils.isAssignable(Integer.TYPE, rhs.getKlass())) {
                throw reportError("Type mismatch: cannot convert from " + rhs.getKlassName() + " to int.", lhs.getNode());
            }

            TypedKlass resultKlass = TypedKlass.create(lhsKlass.getComponentType(), lhs.getTypeArgs());
            boolean boxWhenSafeCall = resultKlass.isPrimitive();
            if (isSafeCall) {
                resultKlass = resultKlass.asBoxedTypedKlass();
            }

            StringBuilder sb = new StringBuilder();
            if (isSafeCall) {
                source = resultKlass.getSource();
                sb.append('(');
                sb.append(lhs.toString());
                sb.append("==null?(");
                sb.append(source);
                sb.append(")null:");
                if (boxWhenSafeCall) {
                    sb.append(source).append(".valueOf(");
                }
                sb.append(lhs.toString());
                sb.append('[');
                sb.append(rhs.toString());
                sb.append(']');
                if (boxWhenSafeCall) {
                    sb.append(')');
                }
                sb.append(')');
            } else {
                sb.append(lhs.toString());
                sb.append('[');
                sb.append(rhs.toString());
                sb.append(']');
            }
            return new SegmentCode(resultKlass, sb.toString(), ctx);
        } else {
            TypedKlass resultKlass = null;

            // try to List.get(index) or Map.get(name) or JetContext.get(name)
            if (List.class.isAssignableFrom(lhsKlass)) {
                if (!ClassUtils.isAssignable(Integer.TYPE, rhs.getKlass())) {
                    throw reportError("The method get(int) in the type List is not applicable for the arguments (" + rhs.getKlassName() + ")", rhs.getNode());
                }
                // 取出可能的List泛型
                if (lhs.getTypeArgs() != null && lhs.getTypeArgs().length == 1) {
                    resultKlass = lhs.getTypeArgs()[0];
                }
            } else if (Map.class.isAssignableFrom(lhsKlass)) {
                // 取出可能的Map泛型
                if (lhs.getTypeArgs() != null && lhs.getTypeArgs().length == 2) {
                    resultKlass = lhs.getTypeArgs()[1]; // value 对应的泛型
                }
            } else if (JetContext.class.isAssignableFrom(lhsKlass)) {
                if (!String.class.equals(rhs.getKlass())) {
                    throw reportError("The method get(String) in the type JetContext is not applicable for the arguments (" + rhs.getKlassName() + ")", rhs.getNode());
                }
                resultKlass = TypedKlass.Object;
            } else {
                throw reportError("Operator [] is not applicable for the object (" + lhs.getKlassName() + ").", ctx);
            }

            if (resultKlass == null) {
                resultKlass = TypedKlass.Object;
            }
            boolean boxWhenSafeCall = resultKlass.isPrimitive();
            if (isSafeCall) {
                resultKlass = resultKlass.asBoxedTypedKlass();
            }

            StringBuilder sb = new StringBuilder();
            if (isSafeCall) {
                source = resultKlass.getSource();
                sb.append('(');
                sb.append(lhs.toString());
                sb.append("==null?(");
                sb.append(source);
                sb.append(")null:");
                if (boxWhenSafeCall) {
                    sb.append(source).append(".valueOf(");
                }
                sb.append(lhs.toString());
                sb.append(".get(");
                sb.append(rhs.toString());
                sb.append(')');
                if (boxWhenSafeCall) {
                    sb.append(')');
                }
                sb.append(')');
            } else {
                sb.append(lhs.toString());
                sb.append(".get(");
                sb.append(rhs.toString());
                sb.append(')');
            }
            return new SegmentCode(resultKlass, sb.toString(), ctx);
        }
    }

    @Override
    public Code visitExpr_new_object(Expr_new_objectContext ctx) {
        SegmentCode code = (SegmentCode) ctx.type().accept(this);

        Expression_listContext expression_list = ctx.expression_list();
        SegmentListCode segmentListCode = (expression_list == null) ? SegmentListCode.EMPTY : (SegmentListCode) expression_list.accept(this);
        Class<?>[] parameterTypes = segmentListCode.getParameterTypes();

        // 查找对应的构造函数
        Class<?> beanClass = code.getKlass();
        Constructor<?> constructor = resolver.resolveConstructor(beanClass, parameterTypes);
        if (constructor == null) {
            // reportError
            StringBuilder err = new StringBuilder(128);
            err.append("The constructor ");
            err.append(getMethodSignature(beanClass.getSimpleName(), parameterTypes));
            err.append(" is undefined for the type ");
            err.append(beanClass.getName());
            err.append('.');
            throw reportError(err.toString(), ctx.type());
        }

        // 生成代码
        StringBuilder source = new StringBuilder(32);
        source.append("(new ").append(code.toString()).append('(');
        if (segmentListCode.size() > 0) {
            source.append(segmentListCode.toString());
        }
        source.append("))");

        return new SegmentCode(code.getTypedKlass(), source.toString(), ctx);
    }

    @Override
    public Code visitExpr_new_array(Expr_new_arrayContext ctx) {
        SegmentCode code = (SegmentCode) ctx.type().accept(this);
        if (code.getKlass().isArray()) {
            throw reportError("Cannot specify an array dimension after an empty dimension", ctx.type());
        }

        StringBuilder typeSource = new StringBuilder(code.toString());

        // 生成代码
        StringBuilder source = new StringBuilder(32);
        source.append("(new ").append(code.toString());
        for (ExpressionContext expression : ctx.expression()) {
            SegmentCode c = (SegmentCode) expression.accept(this);
            if (!ClassUtils.isAssignable(Integer.TYPE, c.getKlass())) {
                throw reportError("Type mismatch: cannot convert from " + c.getKlassName() + " to int.", expression);
            }
            source.append('[').append(c.toString()).append(']');
            typeSource.append("[]");
        }
        source.append(')');

        TypedKlass resultKlass = resolver.resolveTypedKlass(typeSource.toString());
        return new SegmentCode(resultKlass, source.toString(), ctx);
    }

    @Override
    public Code visitExpr_class_cast(Expr_class_castContext ctx) {
        SegmentCode code = (SegmentCode) ctx.type().accept(this);
        Code expr_code = ctx.expression().accept(this);
        String source = "((" + code.toString() + ")" + expr_code.toString() + ")";
        return new SegmentCode(code.getTypedKlass(), source, ctx);
    }

    @Override
    public Code visitExpr_instanceof(Expr_instanceofContext ctx) {
        SegmentCode lhs = (SegmentCode) ctx.expression().accept(this);
        SegmentCode rhs = (SegmentCode) ctx.type().accept(this);

        if (!ClassUtils.isAssignable(lhs.getKlass(), rhs.getKlass()) && !ClassUtils.isAssignable(lhs.getKlass(), rhs.getKlass())) {
            throw reportError("Incompatible conditional operand types " + lhs.getKlassName() + " and " + rhs.getKlassName(), ctx.getChild(1));
        }
        String source = "(" + lhs.toString() + " instanceof " + rhs.toString() + ")";
        return new SegmentCode(Boolean.TYPE, source, ctx);
    }

    @Override
    public Code visitExpr_math_unary_suffix(Expr_math_unary_suffixContext ctx) {
        ExpressionContext expression = ctx.expression();
        SegmentCode code = (SegmentCode) expression.accept(this);
        String op = ctx.getChild(1).getText();

        assert_not_null_constantContext(expression);

        // ++, --
        if (expression.getChildCount() == 1 && expression.getChild(0) instanceof ConstantContext) {
            throw reportError("Invalid argument to operation " + op + ", required: variable, found Value", expression);
        }

        // 类型检查
        Class<?> resultKlass = PromotionUtils.get_unary_inc_dec(code.getKlass(), op);
        if (resultKlass == null) {
            throw reportError("The UnaryOperator \"" + op + "\" is not applicable for the operand " + code.getKlassName(), ctx.getChild(1));
        }

        String source = "(" + code.toString() + op + ")";
        return new SegmentCode(code.getTypedKlass(), source, ctx);
    }

    @Override
    public Code visitExpr_math_unary_prefix(Expr_math_unary_prefixContext ctx) {
        ExpressionContext expression = ctx.expression();
        SegmentCode code = (SegmentCode) expression.accept(this);
        String op = ctx.getChild(0).getText();

        assert_not_null_constantContext(expression);

        // 类型检查
        Class<?> resultKlass;
        if (op.length() == 1) {
            // +, -, ~
            resultKlass = PromotionUtils.get_unary_basic(code.getKlass(), op);
        } else {
            // ++, --
            if (expression.getChildCount() == 1 && expression.getChild(0) instanceof ConstantContext) {
                throw reportError("Invalid argument to operation " + op + ", required: variable, found Value", expression);
            }
            resultKlass = PromotionUtils.get_unary_inc_dec(code.getKlass(), op);
        }
        if (resultKlass == null) {
            throw reportError("The UnaryOperator \"" + op + "\" is not applicable for the operand " + code.getKlassName(), ctx.getChild(0));
        }

        String source = "(" + op + code.toString() + ")";
        return new SegmentCode(code.getTypedKlass(), source, ctx);
    }

    @Override
    public Code visitExpr_math_binary_basic(Expr_math_binary_basicContext ctx) {
        SegmentCode lhs = (SegmentCode) ctx.expression(0).accept(this);
        SegmentCode rhs = (SegmentCode) ctx.expression(1).accept(this);
        String op = ctx.getChild(1).getText();

        // 类型检查
        Class<?> resultKlass = PromotionUtils.get_binary_basic(lhs.getKlass(), rhs.getKlass(), op);
        if (resultKlass == null) {
            throw reportError("The BinaryOperator \"" + op + "\" is not applicable for the operands " + lhs.getKlassName() + " and " + rhs.getKlassName(), ctx.getChild(1));
        }

        String source = "(" + lhs.toString() + op + rhs.toString() + ")";
        return new SegmentCode(resultKlass, source, ctx);
    }

    @Override
    public Code visitExpr_math_binary_shift(Expr_math_binary_shiftContext ctx) {
        SegmentCode lhs = (SegmentCode) ctx.expression(0).accept(this);
        SegmentCode rhs = (SegmentCode) ctx.expression(1).accept(this);

        // Combined '>' '>' => '>>'
        String op = "";
        for (int i = 1; i < ctx.getChildCount() - 1; i++) {
            ParseTree node = ctx.getChild(i);
            if (node instanceof TerminalNode) {
                op = op + node.getText();
            }
        }

        // 类型检查
        Class<?> resultKlass = PromotionUtils.get_binary_shift(lhs.getKlass(), rhs.getKlass(), op);
        if (resultKlass == null) {
            throw reportError("The BinaryOperator \"" + op + "\" is not applicable for the operands " + lhs.getKlassName() + " and " + rhs.getKlassName(), ctx.getChild(1));
        }

        String source = "(" + lhs.toString() + op + rhs.toString() + ")";
        return new SegmentCode(resultKlass, source, ctx);
    }

    @Override
    public Code visitExpr_math_binary_bitwise(Expr_math_binary_bitwiseContext ctx) {
        SegmentCode lhs = (SegmentCode) ctx.expression(0).accept(this);
        SegmentCode rhs = (SegmentCode) ctx.expression(1).accept(this);
        String op = ctx.getChild(1).getText();

        // 类型检查
        Class<?> resultKlass = PromotionUtils.get_binary_bitwise(lhs.getKlass(), rhs.getKlass(), op);
        if (resultKlass == null) {
            throw reportError("The BinaryOperator \"" + op + "\" is not applicable for the operands " + lhs.getKlassName() + " and " + rhs.getKlassName(), ctx);
        }

        String source = "(" + lhs.toString() + op + rhs.toString() + ")";
        return new SegmentCode(resultKlass, source, ctx);
    }

    @Override
    public Code visitExpr_compare_not(Expr_compare_notContext ctx) {
        SegmentCode code = (SegmentCode) ctx.expression().accept(this);
        String source = "(!" + get_if_expression_source(code) + ")";
        return new SegmentCode(Boolean.TYPE, source, ctx);
    }
    
    private SegmentCode boolExpr(String left, String op, String right, 
            ParserRuleContext ctx) {
        return new SegmentCode(Boolean.TYPE, new StringBuilder()
                .append(left).append(' ').append(op).append(' ').append(right).toString(), 
                ctx);
    }

    @Override
    public Code visitExpr_compare_equality(Expr_compare_equalityContext ctx) {
        SegmentCode lhs = (SegmentCode) ctx.expression(0).accept(this);
        SegmentCode rhs = (SegmentCode) ctx.expression(1).accept(this);
        
        TypedKlass ltk = lhs.getTypedKlass(), 
                rtk = rhs.getTypedKlass();
        
        TerminalNode op = (TerminalNode) ctx.getChild(1);
        
        if (ltk != null && rtk != null)
        {
            Class<?> left = ltk.getKlass(), 
                    right = rtk.getKlass();
            
            if (left == null)
                return boolExpr("null", op.getText(), rhs.toString(), ctx); 
                
            if (right == null)
                return boolExpr(lhs.toString(), op.getText(), "null", ctx); 
            
            if (left != Object.class && right != Object.class && left != String.class && 
                    (left == right 
                        || (left.isPrimitive() && right.isPrimitive())
                        || (left.isPrimitive() && null != PrimitiveClassUtils.asUnboxedClass(right))
                        || (right.isPrimitive() && null != PrimitiveClassUtils.asUnboxedClass(left))))
            {
                // emit the raw op for the classes that are equal 
                // or are primitive/boxed types.
                return boolExpr(lhs.toString(), op.getText(), rhs.toString(), ctx); 
            }
        }
        
        assert_not_void_expression(lhs);
        assert_not_void_expression(rhs);

        StringBuilder source = new StringBuilder(32);
        source.append("==".equals(op.getText()) ? "JetUtils.asEquals(" : "JetUtils.asNotEquals(");
        source.append(lhs.toString());
        source.append(',');
        source.append(rhs.toString());
        source.append(')');
        return new SegmentCode(Boolean.TYPE, source.toString(), ctx);
    }

    @Override
    public Code visitExpr_compare_relational(Expr_compare_relationalContext ctx) {
        SegmentCode lhs = (SegmentCode) ctx.expression(0).accept(this);
        SegmentCode rhs = (SegmentCode) ctx.expression(1).accept(this);
        TerminalNode op = (TerminalNode) ctx.getChild(1);

        assert_not_void_expression(lhs);
        assert_not_void_expression(rhs);
        assert_not_null_constantContext(lhs.getNode());
        assert_not_null_constantContext(rhs.getNode());

        Class<?> c1 = lhs.getKlass();
        Class<?> c2 = rhs.getKlass();

        // 类型校验
        boolean pass = true, bothNumeric = false;
        if (NumberClassUtils.isNumbericClass(c1)) {
            bothNumeric = pass = NumberClassUtils.isNumbericClass(c2);
        } else if (NumberClassUtils.isNumbericClass(c2)) {
            pass = false;
        } else {
            pass = c1.isAssignableFrom(c2) || c2.isAssignableFrom(c1);
        }
        if (pass == false) {
            throw reportError("The operator " + op.getText() + " is undefined for the argument type(s) " + lhs.getKlassName() + ", " + rhs.getKlassName(), op);
        }
        
        if (bothNumeric) {
            // emit raw op.
            return boolExpr(lhs.toString(), op.getText(), rhs.toString(), ctx);
        }

        String suffix = "";
        switch (op.getSymbol().getType()) {
        case JetTemplateParser.OP_RELATIONAL_GT:
            suffix = ">0";
            break;
        case JetTemplateParser.OP_RELATIONAL_LT:
            suffix = "<0";
            break;
        case JetTemplateParser.OP_RELATIONAL_GE:
            suffix = ">=0";
            break;
        case JetTemplateParser.OP_RELATIONAL_LE:
            suffix = "<=0";
            break;
        default:
            throw reportError("Unexpected operator :" + op.getText(), ctx);
        }
        StringBuilder source = new StringBuilder(32);
        source.append("(JetUtils.asCompareWith(");
        source.append(lhs.toString());
        source.append(',');
        source.append(rhs.toString());
        source.append(')');
        source.append(suffix);
        source.append(')');
        return new SegmentCode(Boolean.TYPE, source.toString(), ctx);
    }

    @Override
    public Code visitExpr_compare_condition(Expr_compare_conditionContext ctx) {
        SegmentCode lhs = (SegmentCode) ctx.expression(0).accept(this);
        SegmentCode rhs = (SegmentCode) ctx.expression(1).accept(this);
        String op = ctx.getChild(1).getText();

        assert_not_void_expression(lhs);
        assert_not_void_expression(rhs);

        String source = "(" + get_if_expression_source(lhs) + op + get_if_expression_source(rhs) + ")";
        return new SegmentCode(Boolean.TYPE, source, ctx);
    }

    @Override
    public Code visitExpr_conditional_ternary(Expr_conditional_ternaryContext ctx) {
        SegmentCode condition = (SegmentCode) ctx.expression(0).accept(this);
        SegmentCode lhs = (SegmentCode) ctx.expression(1).accept(this);
        SegmentCode rhs = (SegmentCode) ctx.expression(2).accept(this);
        String source = "(" + get_if_expression_source(condition) + "?" + lhs.toString() + ":" + rhs.toString() + ")";

        TypedKlass klass = PromotionUtils.getResultClassForConditionalOperator(lhs.getTypedKlass(), rhs.getTypedKlass());
        return new SegmentCode(klass, source, ctx);
    }

    @Override
    public Code visitConstant(ConstantContext ctx) {
        Token token = ((TerminalNode) ctx.getChild(0)).getSymbol();
        String text = token.getText();
        switch (token.getType()) {
        case JetTemplateParser.STRING_DOUBLE:
            return new SegmentCode(String.class, text, ctx);
        case JetTemplateParser.STRING_SINGLE:
            text = StringEscapeUtils.asCanonicalJavaString(text);
            return new SegmentCode(String.class, text, ctx);
        case JetTemplateParser.INTEGER:
        case JetTemplateParser.INTEGER_HEX:
        case JetTemplateParser.FLOATING_POINT:
            Class<?> klass;
            if (text.endsWith("l") || text.endsWith("L")) {
                klass = Long.TYPE;
            } else if (text.endsWith("f") || text.endsWith("F")) {
                klass = Float.TYPE;
            } else if (text.endsWith("d") || text.endsWith("D")) {
                klass = Double.TYPE;
            } else if (token.getType() == JetTemplateParser.FLOATING_POINT) {
                klass = Double.TYPE; // 浮点数默认是double
            } else {
                klass = Integer.TYPE;
            }
            return new SegmentCode(klass, text, ctx);
        case JetTemplateParser.KEYWORD_TRUE:
            return new SegmentCode(Boolean.TYPE, text, ctx);
        case JetTemplateParser.KEYWORD_FALSE:
            return new SegmentCode(Boolean.TYPE, text, ctx);
        case JetTemplateParser.KEYWORD_NULL:
            return new SegmentCode(TypedKlass.NULL, text, ctx);
        default:
            throw reportError("Unexpected token type :" + token.getType(), ctx);
        }
    }

    @Override
    public Code visitExpression_list(Expression_listContext ctx) {
        List<ExpressionContext> expression_list = ctx.expression();
        SegmentListCode code = new SegmentListCode(expression_list.size());

        for (ExpressionContext expression : expression_list) {
            SegmentCode c = (SegmentCode) expression.accept(this);
            assert_not_void_expression(c);
            code.addChild(c);
        }
        return code;
    }

    @Override
    public Code visitType(TypeContext ctx) {
        StringBuilder name = new StringBuilder();
        for (TerminalNode node : ctx.IDENTIFIER()) {
            if (name.length() > 0) {
                name.append('.');
            }
            name.append(node.getText());
        }

        // 查找 klass
        Class<?> klass = resolver.resolveClass(name.toString());
        if (klass == null) {
            StringBuilder sb = new StringBuilder(128);
            sb.append("java.lang.ClassNotFoundException: ").append(name);
            sb.append("\n advise: Please define package in 'import.packages' or use full qualified class name.");
            throw reportError(sb.toString(), ctx);
        }

        if (securityManager != null) {
            securityManager.checkMemberAccess(klass);
        }

        // 查找泛型类型 typeArgs
        TypedKlass[] typeArgs = TypedKlass.EMPTY_TYPE_ARGS;
        Type_argumentsContext type_arguments = ctx.type_arguments();
        if (type_arguments != null) {
            SegmentListCode c = (SegmentListCode) type_arguments.accept(this);
            typeArgs = new TypedKlass[c.size()];
            for (int i = 0; i < typeArgs.length; i++) {
                typeArgs[i] = c.getChild(i).getTypedKlass();
            }
        }

        // 如果是数组类型，则把 klass 转成数组
        String array_suffix = "";
        List<Type_array_suffixContext> type_array_suffix = ctx.type_array_suffix();
        for (Type_array_suffixContext c : type_array_suffix) {
            Code code = c.accept(this);
            array_suffix = array_suffix + code.toString();
        }

        if (array_suffix.length() > 0) {
            // 转换成 Array Class, 重新 resolve
            String klassName = name.toString() + array_suffix;
            klass = resolver.resolveClass(klassName);
            if (klass == null) {
                throw reportError("java.lang.ClassNotFoundException: " + klassName, ctx);
            }
        }

        // 返回带有的泛型信息的 Class
        TypedKlass typedKlass = TypedKlass.create(klass, typeArgs);
        return new SegmentCode(typedKlass, typedKlass.toString(), ctx);
    }

    @Override
    public Code visitType_array_suffix(Type_array_suffixContext ctx) {
        return new SegmentCode((TypedKlass) null, "[]", ctx);
    }

    @Override
    public Code visitType_arguments(Type_argumentsContext ctx) {
        return ctx.type_list().accept(this);
    }

    @Override
    public Code visitType_list(Type_listContext ctx) {
        List<Type_nameContext> type_name_list = ctx.type_name();
        SegmentListCode code = new SegmentListCode(type_name_list.size());

        for (Type_nameContext type_name : type_name_list) {
            Code c = type_name.accept(this);
            code.addChild((SegmentCode) c);
        }
        return code;
    }

    @Override
    public Code visitType_name(Type_nameContext ctx) {
        TypeContext type = ctx.type();
        if (type != null) {
            return type.accept(this);
        }

        // List<?>
        return new SegmentCode(TypedKlass.WildcharTypedKlass, "?", ctx);
    }

    // -----------------------------------------------------------
    // 变量必须是合法的 java 变量
    private String assert_java_identifier(ParseTree node, boolean isDefining) {
        String name = node.getText();

        if ("for".equals(name)) {
            if (isDefining) {
                throw reportError("Syntax error on token \"" + name + "\" is not a valid identifier.", node);
            }
            return name;
        }
        if (Code.CONTEXT_NAME.equals(name)) {
            if (isDefining) {
                throw reportError("Duplicate local variable \"" + name + "\" is a reserved identifier.", node);
            }
            return name;
        }

        if (SourceVersion.isKeyword(name)) {
            throw reportError("Syntax error on token \"" + name + "\", It is not a valid identifier in Java.", node);
        }
        if (name.startsWith("$")) {
            throw reportError("Local variable \"" + name + "\" can't start with '$', it is a reserved identifier.", node);
        }

        return name;
    }

    // 检验 ctx 必须在 #for 里面, 但不能在 for-else 里面
    private void assert_inside_of_for_directive(ParserRuleContext ctx, String name) {
        if (!validBreakOrContinue)
            throw reportError(name + " cannot be used outside of a #for directive", ctx);
        /*// 还有一种方法，直接看 forStack 是否为空就可以了
        ParserRuleContext p = ctx.getParent();
        while (p != null) {
            if (p instanceof For_directiveContext || p instanceof Alt_for_directiveContext) {
                return;
            }
            if (p instanceof Else_directiveContext) {
                // 跳过可能的  for-else 的情况, 继续向上查找
                // 当然如果时候 if-else 的情况, 也可以跳过这个 #if，没有问题
                p = p.getParent();
            }
            p = p.getParent();
        }
        throw reportError(name + " cannot be used outside of a #for directive", ctx);*/
    }

    // 检测 void 类型
    private void assert_not_void_expression(SegmentCode code) {
        if (Void.TYPE.equals(code.getKlass()) || Void.class.equals(code.getKlass())) {
            throw reportError("Unexpected void type in here.", code.getNode());
        }
    }

    // 检测非 null 常量
    private void assert_not_null_constantContext(ParserRuleContext node) {
        if (node.getStart().getType() == JetTemplateParser.KEYWORD_NULL) {
            throw reportError("Unexpected token: invalid keyword null in here.", node);
        }
    }

    // 确保返回的代码类型必须是 boolean 类型的
    private String get_if_expression_source(SegmentCode code) {
        if (Boolean.TYPE.equals(code.getKlass())) {
            return code.toString();
        } else {
            assert_not_void_expression(code);
            return "JetUtils.asBoolean(" + code.toString() + ")";
        }
    }

    // -----------------------------------------------------------
    // 创建一个全局唯一的变量名称
    private String getUid(String prefix) {
        return "$" + prefix + "_" + (uuid++);
    }

    private RuntimeException reportError(String message, Object node) {
        if (node instanceof ParserRuleContext) {
            parser.notifyErrorListeners(((ParserRuleContext) node).getStart(), message, null);
        } else if (node instanceof TerminalNode) {
            parser.notifyErrorListeners(((TerminalNode) node).getSymbol(), message, null);
        } else if (node instanceof Token) {
            parser.notifyErrorListeners((Token) node, message, null);
        }
        return new SyntaxErrorException(message);
    }

    // 等到一个方法描述字符串
    private String getMethodSignature(String name, Class<?>[] parameterTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append('(');
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            Class<?> type = parameterTypes[i];
            sb.append(type == null ? "<null>" : type.getSimpleName());
        }
        sb.append(')');
        return sb.toString();
    }
}
