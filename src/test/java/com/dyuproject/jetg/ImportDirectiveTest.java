//========================================================================
//Copyright 2015 David Yu
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package com.dyuproject.jetg;

import static com.dyuproject.jetg.TestUtil.createEngine;

import java.util.LinkedHashMap;

import com.dyuproject.jetg.runtime.JetPage;

import org.junit.Assert;
import org.junit.Test;

/**
 * TODO
 * 
 * @author David Yu
 * @created Nov 26, 2015
 */
public class ImportDirectiveTest
{
    
    static void verify(String basePath, String importPath, String expect, 
            String name)
    {
        JetEngine engine = createEngine(basePath);
        JetTemplate template = engine.createTemplate(
                "#import " + importPath + "\n");
        
        JetPage page = template.getJetPage();
        LinkedHashMap<String, String> imports = page.getImports();
        Assert.assertTrue(imports != null && imports.size() == 1);
        Assert.assertEquals(expect, imports.get(name));
    }
    
    @Test
    public void testProcBlock()
    {
        JetEngine engine = createEngine("src/test/resources");
        JetTemplate template = engine.getTemplate("/template/proc.rel.jetx");
        Assert.assertNotNull(template);
        Assert.assertFalse(template.getJetPage().hasProcBlock("foo"));
        Assert.assertTrue(template.getJetPage().hasProcBlock("foo_block"));
    }
    
    @Test
    public void testWithSuffix()
    {
        verify("src/test/resources/", "template/proc.jetx", "/template/proc", "proc");
    }
    
    @Test
    public void testWithoutSuffix()
    {
        verify("src/test/resources/", "template/proc", "/template/proc", "proc");
    }
    
    @Test
    public void testAbsoluteWithSuffix()
    {
        verify("src/test/resources/", "/template/proc.jetx", "/template/proc", "proc");
    }
    
    @Test
    public void testAbsoluteWithoutSuffix()
    {
        verify("src/test/resources/", "/template/proc", "/template/proc", "proc");
    }
    
    @Test
    public void testRelativeWithSuffix()
    {
        verify("src/test/resources/", "template/proc.jetx", "/template/proc", "proc");
    }
    
    @Test
    public void testRelativeWithoutSuffix()
    {
        verify("src/test/resources/", "template/proc", "/template/proc", "proc");
    }
    
    @Test
    public void testDotRelativeWithSuffix()
    {
        verify("src/test/resources/", "./template/proc.jetx", "/template/proc", "proc");
    }
    
    @Test
    public void testDotRelativeWithoutSuffix()
    {
        verify("src/test/resources/", "./template/proc", "/template/proc", "proc");
    }
    
    // TODO compile the imported templates first before compiling the template itself.
    @Test
    public void testHtmlWithSuffix()
    {
        verify("src/test/resources/", "template/proc.html.jetx", "/template/proc.html", "proc_html");
    }
    
    @Test
    public void testHtmlWithoutSuffix()
    {
        verify("src/test/resources/", "template/proc.html", "/template/proc.html", "proc_html");
    }
    
    @Test
    public void testHtmlAbsoluteWithSuffix()
    {
        verify("src/test/resources/", "/template/proc.html.jetx", "/template/proc.html", "proc_html");
    }
    
    @Test
    public void testHtmlAbsoluteWithoutSuffix()
    {
        verify("src/test/resources/", "/template/proc.html", "/template/proc.html", "proc_html");
    }
    
    @Test
    public void testHtmlRelativeWithSuffix()
    {
        verify("src/test/resources/", "template/proc.html.jetx", "/template/proc.html", "proc_html");
    }
    
    @Test
    public void testHtmlRelativeWithoutSuffix()
    {
        verify("src/test/resources/", "template/proc.html", "/template/proc.html", "proc_html");
    }
    
    @Test
    public void testHtmlDotRelativeWithSuffix()
    {
        verify("src/test/resources/", "./template/proc.html.jetx", "/template/proc.html", "proc_html");
    }
    
    @Test
    public void testHtmlDotRelativeWithoutSuffix()
    {
        verify("src/test/resources/", "./template/proc.html", "/template/proc.html", "proc_html");
    }
    
    @Test
    public void testCallImported()
    {
        JetEngine engine = createEngine("src/test/resources/");
        JetTemplate template = engine.createTemplate(
                "\n#import /template/proc\n«proc::test(\"foo\",\"bar\", 1)»");
        
        JetPage page = template.getJetPage();
        LinkedHashMap<String, String> imports = page.getImports();
        Assert.assertTrue(imports != null && imports.size() == 1);
        Assert.assertEquals("/template/proc", imports.get("proc"));
    }
    
    @Test
    public void testHtmlCallImported()
    {
        JetEngine engine = createEngine("src/test/resources/");
        JetTemplate template = engine.createTemplate(
                "\n#import /template/proc.html\n«proc_html::test(\"foo\",\"bar\", 1)»");
        
        JetPage page = template.getJetPage();
        LinkedHashMap<String, String> imports = page.getImports();
        Assert.assertTrue(imports != null && imports.size() == 1);
        Assert.assertEquals("/template/proc.html", imports.get("proc_html"));
    }

}
