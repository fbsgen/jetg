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
 * @created Dec 7, 2015
 */
public class HeaderDirectiveTest
{
    
    public static abstract class Base extends JetPage
    {
        public static boolean is_power_of_two(int num)
        {
            return num > 0 && 0 == (num & (num-1));
        }
    }
    
    @Test
    public void testExtendsAndImport()
    {
        JetEngine engine = createEngine("src/test/resources/");
        JetTemplate template = engine.createTemplate("\n#extends " + 
                HeaderDirectiveTest.class.getName() + "." + Base.class.getSimpleName() +  
                "\n#import /template/proc\n«proc::test(\"foo\",\"bar\", is_power_of_two(512) ? 1 : 0)»\n«is_power_of_two(512)»");
        
        JetPage page = template.getJetPage();
        Assert.assertTrue(page instanceof Base);
        LinkedHashMap<String, String> imports = page.getImports();
        Assert.assertTrue(imports != null && imports.size() == 1);
        Assert.assertEquals("/template/proc", imports.get("proc"));
        
        TestUtil.assertEquals("    name: foo, bar\n    age: 1\ntrue", 
                template);
    }

}
