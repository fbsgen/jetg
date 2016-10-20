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

import java.io.File;
import java.util.Properties;

import com.dyuproject.jetg.parser.SyntaxErrorException;
import com.dyuproject.jetg.resource.loader.FileSystemResourceLoader;
import com.dyuproject.jetg.utils.UnsafeCharArrayWriter;

import org.junit.Assert;

/**
 * TODO
 * 
 * @author David Yu
 * @created Nov 23, 2015
 */
public final class TestUtil
{
    private TestUtil() {}
    
    public static JetEngine createEngine(String basePath) {
        Properties config = new Properties();
        config.put(JetConfig.TEMPLATE_LOADER, FileSystemResourceLoader.class.getName());
        config.put(JetConfig.TEMPLATE_PATH, new File(basePath).getAbsolutePath());
        config.put(JetConfig.TEMPLATE_SUFFIX, ".jetx");
        //config.put(JetConfig.COMPILE_ALWAYS, "false");
        //config.put(JetConfig.COMPILE_TOOL, JdtCompiler.class.getName());
        //config.put(JetConfig.COMPILE_STRATEGY, "auto");
        //config.put(JetConfig.COMPILE_DEBUG, "true");
        //config.put(JetConfig.TEMPLATE_RELOADABLE, "true");
        //config.put(JetConfig.TRIM_DIRECTIVE_COMMENTS, "true");
        return JetEngine.create(config);
    }
    
    static void assertEquals(String expected, String template, 
            JetEngine engine)
    {
        assertEquals(expected, engine.createTemplate(template));
    }
    
    static void assertEquals(String expected, JetTemplate jt)
    {
        UnsafeCharArrayWriter out = new UnsafeCharArrayWriter();
        JetContext context = new JetContext();
        jt.render(context, out);
        Assert.assertEquals(expected, out.toString());
    }
    
    static void assertFail(String template, JetEngine engine)
    {
        try
        {
            engine.createTemplate(template);
            Assert.fail();
        }
        catch (SyntaxErrorException e)
        {
            // expected
        }
    }

}
