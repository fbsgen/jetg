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
package com.dyuproject.jetg.parser.code;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dyuproject.jetg.JetEngine;
import com.dyuproject.jetg.resource.Resource;
import com.dyuproject.jetg.utils.PathUtils;
import com.dyuproject.jetg.utils.StringEscapeUtils;

/**
 * 模板 Class
 */
public class TemplateClassCode extends Code {
    
    static final String BASE_CLASS = "JetPage";
    
    public static final class Import {
        public final String name, path, fqcn;
        public final boolean wildcard;
        public Import(String name, String path, String fqcn, boolean wildcard) {
            this.name = name;
            this.path = path;
            this.fqcn = fqcn;
            this.wildcard = wildcard;
        }
    }
    
    private final String templateSuffix; // for imports
    private List<String[]> fields = new ArrayList<String[]>(32); // 全局文本字段
    private MethodCode methodCode = new MethodCode(null, "    ", false); // 方法体
    // TODO use this
    private LinkedHashMap<String,Import> imports = null;
    private List<MacroCode> macroCodeList; // 宏定义
    private List<ProcCode> procCodeList;
    private LinkedHashMap<String,Boolean> procBlockMap;
    private final JetEngine engine;
    private final Resource resource;
    public String baseClass = BASE_CLASS;
    
    public TemplateClassCode(JetEngine engine, Resource resource) {
        this.engine = engine;
        this.templateSuffix = engine.getConfig().getTemplateSuffix();
        this.resource = resource;
    }

    public void addField(String id, String text) {
        fields.add(new String[] { id, text });
    }
    
    public Import getImport(String name) {
        return imports == null ? null : imports.get(name);
    }
    
    public void addImport(String path, boolean wildcard) {
        int sl = path.lastIndexOf('/');
        // check relative
        if (sl == -1) {
            if ((sl = resource.name.lastIndexOf('/')) != -1) {
                path = resource.name.substring(0, sl + 1) + path;
            }
        } else if (sl == 1 && path.charAt(0) == '.') {
            if ((sl = resource.name.lastIndexOf('/')) != -1) {
                path = resource.name.substring(0, sl + 1) + path.substring(2);
            }
        }
        
        path = PathUtils.getStandardizedName(path);
        
        String fqcn;
        
        if (path.endsWith(templateSuffix)) {
            fqcn = Resource.resolveQualifiedClassName(path);
            path = path.substring(0, path.length() - templateSuffix.length());
        } else {
            fqcn = Resource.resolveQualifiedClassName(path + templateSuffix);
        }
        
        int slash = path.lastIndexOf('/');
        String name = slash == -1 ? path : path.substring(slash + 1);
        
        if (imports == null)
            imports = new LinkedHashMap<String, Import>();
        
        Import imp = new Import(name.replace('.', '_'), path, fqcn, wildcard);
        imports.put(imp.name, imp);
    }

    public void addMacro(MacroCode macroCode) {
        if (macroCodeList == null) {
            macroCodeList = new ArrayList<MacroCode>(8);
        }
        macroCodeList.add(macroCode);
    }
    
    public void addProc(ProcCode procCode) {
        if (procCodeList == null) {
            procCodeList = new ArrayList<ProcCode>(8);
        }
        procCodeList.add(procCode);
        if (!procCode.name.endsWith("_block"))
            return;
        
        if (procBlockMap == null)
            procBlockMap = new LinkedHashMap<String, Boolean>();
        
        procBlockMap.put(procCode.name, Boolean.TRUE);
    }

    public MethodCode getMethodCode() {
        return methodCode;
    }
    
    private void addInitImportsTo(StringBuilder sb)
    {
        for (Import i : imports.values()) {
            sb.append("    imports.put(\"").append(i.name).append("\", \"")
                .append(i.path).append("\");\n");
        }
    }
    
    private void addInitProcBlocksTo(StringBuilder sb)
    {
        for (Map.Entry<String, Boolean> entry : procBlockMap.entrySet()) {
            sb.append("    proc_blocks.put(\"")
                .append(entry.getKey()).append("\", Boolean.TRUE);\n");
        }
    }

    @Override
    public String toString() {
        final String packageName = resource.getPackageName(),
                className = resource.getClassName(),
                templateName = resource.getName(),
                encoding = resource.getEncoding();
        
        final StringBuilder sb = new StringBuilder(2048);
        
        int flags = 0;
        
        if (packageName != null)
            sb.append("package ").append(packageName).append(";\n\n");
        
        sb.append("import java.util.*;\n")
            .append("import com.dyuproject.jetg.JetContext;\n")
            .append("import com.dyuproject.jetg.runtime.*;\n");
        
        if (imports != null) {
            for (Import i : imports.values()) {
                engine.getTemplate(i.path + templateSuffix);
                sb.append("import ");
                
                if (i.wildcard)
                    sb.append("static ");
                
                sb.append(i.fqcn);
                
                if (i.wildcard)
                    sb.append(".*");
                
                sb.append(";\n");
            }
        }
        
        sb.append("\n@SuppressWarnings({\"all\", \"warnings\", \"unchecked\", \"unused\", \"cast\"})\n")
            .append("public final class ").append(className).append(" extends ")
            .append(baseClass).append(" {\n\n");
        
        if (imports != null) {
            sb.append("  static final LinkedHashMap<String,String> imports = new LinkedHashMap<String,String>();\n");
            flags |= 1;
        }
        
        if (procBlockMap != null) {
            sb.append("  static final HashMap<String,Boolean> proc_blocks = new HashMap<String,Boolean>();\n");
            flags |= 2;
        }
        
        if (flags != 0) {
            sb.append("  static {\n");
            
            if (imports != null)
                addInitImportsTo(sb);
            if (procBlockMap != null)
                addInitProcBlocksTo(sb);
            
            sb.append("  }\n\n");
            
            if (imports != null) {
                sb.append("  @Override\n")
                    .append("  public LinkedHashMap<String,String> getImports() {\n")
                    .append("    return imports;\n")
                    .append("  }\n\n");
            }
            
            if (procBlockMap != null) {
                sb.append("  @Override\n")
                    .append("  public boolean hasProcBlock(String name) {\n")
                    .append("    return Boolean.TRUE == proc_blocks.get(name);\n")
                    .append("  }\n\n");
            }
        }
        
        sb.append("  @Override\n")
            .append("  public void render(final JetPageContext $ctx) throws Throwable {\n")
            .append(methodCode.toString())
            .append("  }\n\n");
        
        if (macroCodeList != null) {
            for (MacroCode c : macroCodeList)
                sb.append(c.toString()).append('\n');
        }
        if (procCodeList != null) {
            for (ProcCode c : procCodeList)
                sb.append(c.toString()).append('\n');
        }
        
        sb.append("  @Override\n")
            .append("  public String getName() {\n")
            .append("    return \"").append(StringEscapeUtils.escapeJava(templateName)).append("\";\n")
            .append("  }\n\n");
        
        sb.append("  public static final String $ENC = \"").append(encoding).append("\";\n");
        for (String[] field : fields) {
            sb.append("  private static final String ").append(field[0])
                .append(" = \"").append(StringEscapeUtils.escapeJava(field[1])).append("\";\n");
            sb.append("  private static final byte[] ").append(field[0])
                .append("_bytes = JetUtils.asBytes(").append(field[0]).append(", $ENC);\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

}
