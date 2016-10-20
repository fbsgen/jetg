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

import org.junit.Test;

/**
 * TODO
 * 
 * @author David Yu
 * @created Nov 23, 2015
 */
public class ProcDirectiveTest
{
    private JetEngine engine = JetEngine.create();
    
    @Test
    public void testValidBlockDirective()
    {
        TestUtil.assertEquals("1one", 
                "«test(1)»#test(int item)«item»#if(item == 1)one#endif#end", 
                engine);
    }
    
    @Test
    public void testSetDirective()
    {
        TestUtil.assertEquals("01one", 
                "«test(0)»#test(int item)#set(int x = item++)«x»«item»#if(item == 1)one#endif#end", 
                engine);
    }
    
    @Test
    public void testValueIterator()
    {
        TestUtil.assertEquals("  101  102\n", 
                "«test([1, 2])»\n#test(List<Integer> items)\n  «items:Integer:print(100)»\n#end\n#print(int item, int toAdd)\n«item + toAdd»\n#end", 
                engine);
    }
    
    @Test
    public void testValueIteratorWithCommaSeparator()
    {
        TestUtil.assertEquals("  101,  102\n", 
                "#import template/proc#\n«test([1, 2])»\n#test(List<Integer> items)\n  «items:Integer:print(100); separator=\",\"»\n#end\n#print(int item, int toAdd)\n«item + toAdd»\n#end", 
                TestUtil.createEngine("src/test/resources/"));
    }
    
    @Test
    public void testValueIteratorWithCommaLineSeparator()
    {
        TestUtil.assertEquals("  101,\n  102\n", 
                "#import template/proc#\n«test([1, 2])»\n#test(List<Integer> items)\n  «items:Integer:print(100); separator=\",\\n\"»\n#end\n#print(int item, int toAdd)\n«item + toAdd»\n#end", 
                TestUtil.createEngine("src/test/resources/"));
    }
    
    @Test
    public void testValueIteratorWithCommaLineSeparatorAndNextLineText()
    {
        TestUtil.assertEquals("  101,\n  102\nfoo\n", 
                "#import template/proc#\n«test([1, 2], \"foo\")»\n#test(List<Integer> items, String nextLineText)\n  «items:Integer:print(100); separator=\",\\n\"»\n«nextLineText»\n#end\n#print(int item, int toAdd)\n«item + toAdd»\n#end", 
                TestUtil.createEngine("src/test/resources/"));
    }
    
    @Test
    public void testForIndexVar()
    {
        TestUtil.assertEquals("0112&0112", 
                "«test([1, 2])»#test(List<Integer> items)#for(int a : items)«a$$i»«a»#endfor&#for(int a : items)«a$$i»«a»#endfor#end", 
                engine);
    }
    
    @Test
    public void testEqualityString()
    {
        TestUtil.assertEquals("foo", 
                "«test(\"foo\")»#test(String item)#if(item == \"foo\")«item»#endif#end", 
                engine);
    }
    
    @Test
    public void testEqualityStringLhs()
    {
        TestUtil.assertEquals("foo", 
                "«test(\"foo\")»#test(String item)#if(\"foo\" == item)«item»#endif#end", 
                engine);
    }
    
    @Test
    public void testEmitInsideProc()
    {
        TestUtil.assertEquals("00000", 
                "#import /template/proc#\n" +
                "«repeat(\"0\", 5)»", 
                TestUtil.createEngine("src/test/resources/"));
    }
    
    @Test
    public void testOptionalArg()
    {
        TestUtil.assertEquals("1one", 
                "«test(1, 0)»#test(int item, toAdd = 1)«item + toAdd»#if(item == 1)one#endif#end", 
                engine);
    }
    
    @Test
    public void testOptionalArgWithTwoRequired()
    {
        TestUtil.assertEquals("1trueone", 
                "«test(1, true, 0)»#test(int item, boolean bool, toAdd = 1)«item + toAdd»«bool»#if(item == 1)one#endif#end", 
                engine);
    }
    
    @Test
    public void testOptionalArg2()
    {
        TestUtil.assertEquals("1truebazone", 
                "«test(1, true, \"baz\", 0)»#test(int item, boolean bool, str = \"bar\", toAdd = 1)«item + toAdd»«bool»«str»#if(item == 1)one#endif#end", 
                engine);
    }
    
    @Test
    public void testOptionalArg2Overload()
    {
        TestUtil.assertEquals("2truebarone", 
                "«test(1, true)»#test(int item, boolean bool, str = \"bar\", toAdd = 1)«item + toAdd»«bool»«str»#if(item == 1)one#endif#end", 
                engine);
    }
    
    @Test
    public void testOptionalArg2Overload1()
    {
        TestUtil.assertEquals("2true#one", 
                "«test(1, true, \"#\")»#test(int item, boolean bool, str = \"bar\", toAdd = 1)«item + toAdd»«bool»«str»#if(item == 1)one#endif#end", 
                engine);
    }
    
    @Test
    public void testOptionalArgWithType()
    {
        TestUtil.assertEquals("1one", 
                "«test(1, 0)»#test(int item, long toAdd = 1)«item + toAdd»#if(item == 1)one#endif#end", 
                engine);
    }
    
    @Test
    public void testOptionalArgString()
    {
        TestUtil.assertEquals("10one", 
                "«test(1, \"0\")»#test(int item, toAdd = \"1\")«item + toAdd»#if(item == 1)one#endif#end", 
                engine);
    }
    
    @Test
    public void testOptionalArgStringOverload()
    {
        TestUtil.assertEquals("11one", 
                "«test(1)»#test(int item, toAdd = \"1\")«item + toAdd»#if(item == 1)one#endif#end", 
                engine);
    }
    
    @Test
    public void testOptionalArgBool()
    {
        TestUtil.assertEquals("1trueone", 
                "«test(1, true)»#test(int item, toAdd = false)«item»«toAdd»#if(item == 1)one#endif#end", 
                engine);
    }
    
    @Test
    public void testOptionalArgBoolOverload()
    {
        TestUtil.assertEquals("1falseone", 
                "«test(1)»#test(int item, toAdd = false)«item»«toAdd»#if(item == 1)one#endif#end", 
                engine);
    }
    
    @Test
    public void testOptionalArgTypeMismatch()
    {
        TestUtil.assertFail(
                "«test(1, 0)»#test(int item, int toAdd = \"foo\")«item + toAdd»#if(item == 1)one#endif#end", 
                engine);
    }
    
    @Test
    public void testOptionalArgInvalid()
    {
        TestUtil.assertFail(
                "«test(1, 0)»#test(toAdd = 1)«item + toAdd»#if(item == 1)one#endif#end", 
                engine);
    }
    
    @Test
    public void testOptionalArgInvalid2()
    {
        TestUtil.assertFail(
                "«test(1, 0)»#test(int item, toAdd = 1, int another)«item + toAdd»#if(item == 1)one#endif#end", 
                engine);
    }
    
    @Test
    public void testFunctionOptionalArgInvalid()
    {
        TestUtil.assertFail(
                "«test(1, 0)»#test(toAdd = 1)::int\nreturn toAdd;\n#end", 
                engine);
    }
    
    @Test
    public void testFunctionOptionalArgInvalid2()
    {
        TestUtil.assertFail(
                "«test(1, 0)»#test(int item, toAdd = 1, int another)\nreturn item + toAdd + another;\n#end",  
                engine);
    }
    
    /* ================================================== */
    
    @Test
    public void testOptionalArgUseOverload()
    {
        TestUtil.assertEquals("2one", 
                "«test(1)»#test(int item, toAdd = 1)«item + toAdd»#if(item == 1)one#endif#end", 
                engine);
    }
    
    @Test
    public void testOptionalArgUseOverload2a()
    {
        TestUtil.assertEquals("4one", 
                "«test(1)»#test(int item, toAdd = 1, another = 2)«item + toAdd + another»#if(item == 1)one#endif#end", 
                engine);
    }
    
    @Test
    public void testOptionalArgUseOverload2b()
    {
        TestUtil.assertEquals("5one", 
                "«test(1, 2)»#test(int item, toAdd = 1, another = 2)«item + toAdd + another»#if(item == 1)one#endif#end", 
                engine);
    }
    
    @Test
    public void testOptionalArgUseOverload3a()
    {
        TestUtil.assertEquals("7one", 
                "«test(1)»#test(int item, toAdd = 1, another = 2, last = 3)«item + toAdd + another + last»#if(item == 1)one#endif#end", 
                engine);
    }
    
    @Test
    public void testOptionalArgUseOverload3b()
    {
        TestUtil.assertEquals("8one", 
                "«test(1, 2)»#test(int item, toAdd = 1, another = 2, last = 3)«item + toAdd + another + last»#if(item == 1)one#endif#end", 
                engine);
    }
    
    @Test
    public void testOptionalArgUseOverload3c()
    {
        TestUtil.assertEquals("9one", 
                "«test(1, 2, 3)»#test(int item, toAdd = 1, another = 2, last = 3)«item + toAdd + another + last»#if(item == 1)one#endif#end", 
                engine);
    }
    
    /* ================================================== */
    
    @Test
    public void testFunctionOptionalArgUseOverload()
    {
        TestUtil.assertEquals("2", 
                "«get_num(1)»#get_num(int item, toAdd = 1)::int\nreturn item + toAdd;\n#end", 
                engine);
    }
    
    @Test
    public void testFunctionOptionalArgUseOverload2a()
    {
        TestUtil.assertEquals("4", 
                "«get_num(1)»#get_num(int item, toAdd = 1, another = 2)::int\nreturn item + toAdd + another;\n#end", 
                engine);
    }
    
    @Test
    public void testFunctionOptionalArgUseOverload2b()
    {
        TestUtil.assertEquals("5", 
                "«get_num(1, 2)»#get_num(int item, toAdd = 1, another = 2)::int\nreturn item + toAdd + another;\n#end", 
                engine);
    }
    
    @Test
    public void testFunctionOptionalArgUseOverload3a()
    {
        TestUtil.assertEquals("7", 
                "«get_num(1)»#get_num(int item, toAdd = 1, another = 2, last = 3)::int\nreturn item + toAdd + another + last;\n#end", 
                engine);
    }
    
    @Test
    public void testFunctionOptionalArgUseOverload3b()
    {
        TestUtil.assertEquals("8", 
                "«get_num(1, 2)»#get_num(int item, toAdd = 1, another = 2, last = 3)::int\nreturn item + toAdd + another + last;\n#end", 
                engine);
    }
    
    @Test
    public void testFunctionOptionalArgUseOverload3c()
    {
        TestUtil.assertEquals("9", 
                "«get_num(1, 2, 3)»#get_num(int item, toAdd = 1, another = 2, last = 3)::int\nreturn item + toAdd + another + last;\n#end", 
                engine);
    }
    
    /* ================================================== */
    
    @Test
    public void testNonVoid()
    {
        TestUtil.assertEquals("1false22\n2true44", 
                "«test(1)»\n«test(2)»#test(int item)«item»«is_even(item)»«even(item) ? plus2(item) : plus1(item)»«item % 2 == 0 ? item + 2 : ++item»#end#even(int num)::boolean\nreturn num % 2 == 0;\n#end#is_even(int num)::boolean\nreturn num % 2 == 0;\n#end#plus1(int num)::int\nreturn num + 1;\n#end#plus2(int num)::int\nreturn num + 2;\n#end", 
                engine);
    }
    
    @Test
    public void testIndent()
    {
        TestUtil.assertEquals("<div>\n  11\n</div>\n", 
                "«test(1)»\n#test(int item)\n<div>\n  «item»«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentConditional()
    {
        TestUtil.assertEquals("<div>\n  one11\n</div>\n", 
                "«test(1)»\n#test(int item)\n<div>\n  #if(item == 1)one«item»#else()not#endif«item»\n</div>\n#end", 
                engine);
    }
    
    /* ================================================== */
    
    @Test
    public void testIndentConditionalNewlineText()
    {
        TestUtil.assertEquals("<div>\n  one\n1\n</div>\n", 
                "«test(1)»\n#test(int item)\n<div>\n  #if(item == 1)one#else()not#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentConditionalNewlineTextElse()
    {
        TestUtil.assertEquals("<div>\n  zero\n0\n</div>\n", 
                "«test(0)»\n#test(int item)\n<div>\n  #if(item == 1)not#else()zero#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentConditionalNewlineTextElseIf()
    {
        TestUtil.assertEquals("<div>\n  zero\n0\n</div>\n", 
                "«test(0)»\n#test(int item)\n<div>\n  #if(item == 1)not#elseif(item == 0)zero#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentConditionalNewlineValue()
    {
        TestUtil.assertEquals("<div>\n  1\n1\n</div>\n", 
                "«test(1)»\n#test(int item)\n<div>\n  #if(item == 1)«item»#else()not#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentConditionalNewlineValueElse()
    {
        TestUtil.assertEquals("<div>\n  0\n0\n</div>\n", 
                "«test(0)»\n#test(int item)\n<div>\n  #if(item == 1)not#else()«item»#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentConditionalNewlineValueElseIf()
    {
        TestUtil.assertEquals("<div>\n  0\n0\n</div>\n", 
                "«test(0)»\n#test(int item)\n<div>\n  #if(item == 1)not#elseif(item == 0)«item»#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentConditionalNewlineTextValue()
    {
        TestUtil.assertEquals("<div>\n  one1\n1\n</div>\n", 
                "«test(1)»\n#test(int item)\n<div>\n  #if(item == 1)one«item»#else()not#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentConditionalNewlineTextValueElse()
    {
        TestUtil.assertEquals("<div>\n  zero0\n0\n</div>\n", 
                "«test(0)»\n#test(int item)\n<div>\n  #if(item == 1)not#else()zero«item»#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentConditionalNewlineTextValueElseIf()
    {
        TestUtil.assertEquals("<div>\n  zero0\n0\n</div>\n", 
                "«test(0)»\n#test(int item)\n<div>\n  #if(item == 1)not#elseif(item == 0)zero«item»#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentConditionalNewlineValueText()
    {
        TestUtil.assertEquals("<div>\n  1one\n1\n</div>\n", 
                "«test(1)»\n#test(int item)\n<div>\n  #if(item == 1)«item»one#else()not#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentConditionalNewlineValueTextElse()
    {
        TestUtil.assertEquals("<div>\n  0zero\n0\n</div>\n", 
                "«test(0)»\n#test(int item)\n<div>\n  #if(item == 1)not#else()«item»zero#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    //@Test
    public void testIndentConditionalNewlineValueTextElseIf()
    {
        TestUtil.assertEquals("<div>\n  0zero\n0\n</div>\n", 
                "«test(0)»\n#test(int item)\n<div>\n  #if(item == 1)not#elseif(item == 0)«item»zero#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    /* ================================================== */
    
    @Test
    public void testIndentIgnoreNewlineText()
    {
        TestUtil.assertEquals("<div>  one1</div>\n", 
                "«test(1)»\n#test(int item)%%\n<div>\n  #if(item == 1)one#else()not#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentIgnoreNewlineTextElse()
    {
        TestUtil.assertEquals("<div>  zero0</div>\n", 
                "«test(0)»\n#test(int item)%%\n<div>\n  #if(item == 1)not#else()zero#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentIgnoreNewlineTextElseIf()
    {
        TestUtil.assertEquals("<div>  zero0</div>\n", 
                "«test(0)»\n#test(int item)%%\n<div>\n  #if(item == 1)not#elseif(item == 0)zero#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentIgnoreNewlineValue()
    {
        TestUtil.assertEquals("<div>  11</div>\n", 
                "«test(1)»\n#test(int item)%%\n<div>\n  #if(item == 1)«item»#else()not#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentIgnoreNewlineValueElse()
    {
        TestUtil.assertEquals("<div>  00</div>\n", 
                "«test(0)»\n#test(int item)%%\n<div>\n  #if(item == 1)not#else()«item»#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentIgnoreNewlineValueElseIf()
    {
        TestUtil.assertEquals("<div>  00</div>\n", 
                "«test(0)»\n#test(int item)%%\n<div>\n  #if(item == 1)not#elseif(item == 0)«item»#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentIgnoreNewlineTextValue()
    {
        TestUtil.assertEquals("<div>  one11</div>\n", 
                "«test(1)»\n#test(int item)%%\n<div>\n  #if(item == 1)one«item»#else()not#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentIgnoreNewlineTextValueElse()
    {
        TestUtil.assertEquals("<div>  zero00</div>\n", 
                "«test(0)»\n#test(int item)%%\n<div>\n  #if(item == 1)not#else()zero«item»#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentIgnoreNewlineTextValueElseIf()
    {
        TestUtil.assertEquals("<div>  zero00</div>\n", 
                "«test(0)»\n#test(int item)%%\n<div>\n  #if(item == 1)not#elseif(item == 0)zero«item»#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentIgnoreNewlineValueText()
    {
        TestUtil.assertEquals("<div>  1one1</div>\n", 
                "«test(1)»\n#test(int item)%%\n<div>\n  #if(item == 1)«item»one#else()not#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testIndentIgnoreNewlineValueTextElse()
    {
        TestUtil.assertEquals("<div>  0zero0</div>\n", 
                "«test(0)»\n#test(int item)%%\n<div>\n  #if(item == 1)not#else()«item»zero#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    //@Test
    public void testIndentIgnoreNewlineValueTextElseIf()
    {
        TestUtil.assertEquals("<div>  0zero0</div>\n", 
                "«test(0)»\n#test(int item)%%\n<div>\n  #if(item == 1)not#elseif(item == 0)«item»zero#endif\n«item»\n</div>\n#end", 
                engine);
    }
    
    /* ================================================== */
    
    @Test
    public void testIndentConditional2()
    {
        TestUtil.assertEquals("<div>\n  1one1\n</div>\n", 
                "«test(1)»\n#test(int item)\n<div>\n  #if(item == 1)«item»one#else()not#endif«item»\n</div>\n#end", 
                engine);
    }
    
    @Test
    public void testProcIgnoreLine()
    {
        TestUtil.assertEquals("one", 
                "«test(1)»#test(int item)%%\n#if(item == 1)\none\n#endif\n#end", 
                engine);
    }
    
    @Test
    public void testDelimClose()
    {
        TestUtil.assertEquals("»true»»«»»", 
                "»#if(true)true»#endif»«»»", 
                engine);
    }
    
    @Test
    public void testDelimCloseAndEmpy()
    {
        TestUtil.assertEquals("»true»true»«»«»»«»«»", 
                "»#if(true)true»#endif#if(true)true»«»«»#endif»«»«»", 
                engine);
    }
    
    @Test
    public void testDelimEmpty()
    {
        TestUtil.assertEquals("«»«»«»«»«»«»", 
                "«»«»#if(true)«»«»#endif«»«»", 
                engine);
    }
    
    @Test
    public void testDelim2x()
    {
        TestUtil.assertEquals("««", 
                "««", 
                engine);
    }
    
    @Test
    public void testDelimLast()
    {
        TestUtil.assertEquals("»«»true«", 
                "»«»«true»«", 
                engine);
    }
    
    @Test
    public void testDelimLast2x()
    {
        TestUtil.assertEquals("»«»true««", 
                "»«»«true»««", 
                engine);
    }
    
    // Currently fails
    //@Test
    public void testDelimInsideIf()
    {
        TestUtil.assertEquals("»«»1one«", 
                "»«»«test(1)»#test(int item)«item»#if(item == 1)one«#endif#end", 
                engine);
    }
    
    // Currently fails
    //@Test
    public void testDelimInsideAltIf()
    {
        TestUtil.assertEquals("»«»1one«", 
                "»«»«test(1)»#test(int item)«item»«if(item == 1)»one««endif»#end", 
                engine);
    }
    
    @Test
    public void testAltIf()
    {
        TestUtil.assertEquals("1one", 
                "«test(1)»#test(int item)«item»«if(item == 1)»one«endif»#end", 
                engine);
    }
    
    @Test
    public void testAltIfElse()
    {
        TestUtil.assertEquals("1one", 
                "«test(1)»#test(int item)«item»«if(item == 0)»zero«else»one«endif»#end", 
                engine);
    }
    
    @Test
    public void testAltIfElseIf()
    {
        TestUtil.assertEquals("1one", 
                "«test(1)»#test(int item)«item»«if(item == 0)»zero«elseif(item == 1)»one«endif»#end", 
                engine);
    }
    
    @Test
    public void testAltForOutsideProc()
    {
        TestUtil.assertEquals("1020", 
                "«for(int i : [1,2])»«i»«test(0)»«endfor»#test(int item)«item»#end", 
                engine);
    }
    
    @Test
    public void testAltForElseOutSideProc()
    {
        TestUtil.assertEquals("one0", 
                "«for(i : [])»«i»«else»one«test(0)»«endfor»#test(int item)«item»#end", 
                engine);
    }
    
    @Test
    public void testAltFor()
    {
        TestUtil.assertEquals("123", 
                "«test([1, 2, 3])»#test(List<Integer> items)«for(item : items)»«item»«endfor»#end", 
                engine);
    }
    
    @Test
    public void testAltForElse()
    {
        TestUtil.assertEquals("none", 
                "«test([])»#test(List<Integer> items)«for(item : items)»«item»«else»none«endfor»#end", 
                engine);
    }
    
    @Test
    public void testAltForTyped()
    {
        TestUtil.assertEquals("123", 
                "«test([1, 2, 3])»#test(List<Integer> items)«for(Integer item : items)»«item»«endfor»#end", 
                engine);
    }
    
    @Test
    public void testAltForElseTyped()
    {
        TestUtil.assertEquals("none", 
                "«test([])»#test(List<Integer> items)«for(Integer item : items)»«item»«else»none«endfor»#end", 
                engine);
    }
    
    @Test
    public void testAltForTypedPrimitive()
    {
        TestUtil.assertEquals("123", 
                "«test([1, 2, 3])»#test(List<Integer> items)«for(int item : items)»«item»«endfor»#end", 
                engine);
    }
    
    @Test
    public void testAltForElseTypedPrimitive()
    {
        TestUtil.assertEquals("none", 
                "«test([])»#test(List<Integer> items)«for(int item : items)»«item»«else»none«endfor»#end", 
                engine);
    }
    
    // TODO
    //@Test
    public void testAltForTypedPrimitiveArg()
    {
        TestUtil.assertEquals("123", 
                "«test([1, 2, 3])»#test(int[] items)«for(int item : items)»«item»«endfor»#end", 
                engine);
    }
    
    // TODO
    //@Test
    public void testAltForElseTypedPrimitiveArg()
    {
        TestUtil.assertEquals("none", 
                "«test([])»#test(int[] items)«for(int item : items)»«item»«else»none«endfor»#end", 
                engine);
    }
    
    @Test
    public void testAltForTypedManualSeparator()
    {
        TestUtil.assertEquals("1,2,3", 
                "«test([1, 2, 3])»#test(List<Integer> items)«for(Integer item : items)»«if(item$$i != 0)»,«endif»«item»«endfor»#end", 
                engine);
    }
    
    @Test
    public void testAltForTypedManualSeparatorNested()
    {
        TestUtil.assertEquals("11/0__21&2/1__31&2&3/2", 
                "«test([1, 2, 3])»#test(List<Integer> items)«for(Integer item : items)»«if(item$$i != 0)»__«endif»«item»«for(Integer inner : slice(items, 0, item$$i + 1))»«if(inner$$i != 0)»&«endif»«inner»«endfor»/«inner$$i - 1»«endfor»#end" + 
                "\n#slice(List<Integer> items, int start, int end)::List<Integer>\nreturn items.subList(start, end);\n#end", 
                engine);
    }
    
    @Test
    public void testAltForTypedManualSeparatorNestedInsideIf()
    {
        TestUtil.assertEquals("11/0__21&2/1__31&2&3/2", 
                "«test([1, 2, 3])»#test(List<Integer> items)«if(items != null)»«for(Integer item : items)»«if(item$$i != 0)»__«endif»«item»«for(Integer inner : slice(items, 0, item$$i + 1))»«if(inner$$i != 0)»&«endif»«inner»«endfor»/«inner$$i - 1»«endfor»«endif»#end" + 
                "\n#slice(List<Integer> items, int start, int end)::List<Integer>\nreturn items.subList(start, end);\n#end", 
                engine);
    }
    
    @Test
    public void testAltForTypedManualSeparatorWithStopBreakContinue()
    {
        TestUtil.assertEquals("11/0__21/0", 
                "«test([1, 2, 3, null, 4])»#test(List<Integer> items)#stop(items == null || items.empty)«for(Integer item : items)»#continue(item == null)#break(item$$i == 2)«if(item$$i != 0)»__«endif»«item»«for(Integer inner : slice(items, 0, item$$i + 1))»#continue(inner == null)#break(inner > 1)«if(inner$$i != 0)»&«endif»«inner»«endfor»/«inner$$i - 1»«endfor»#end" + 
                "\n#slice(List<Integer> items, int start, int end)::List<Integer>\nreturn items.subList(start, end);\n#end", 
                engine);
    }
    
    @Test
    public void testAltForTypedManualSeparatorCheckNotNull()
    {
        TestUtil.assertEquals("1,2,3", 
                "«test([1, 2, 3])»#test(List<Integer> items)«for(Integer item : items)»«if(item$$i > 0)»,«endif»«if(item != null)»«item»«endif»«endfor»#end", 
                engine);
    }
    
    @Test
    public void testAltForTypedManualSeparatorNewLine()
    {
        TestUtil.assertEquals("1\n2\n3", 
                "«test([1, 2, 3])»#test(List<Integer> items)«for(Integer item : items)»«if(item$$i != 0)»\\n«endif»«item»«endfor»#end", 
                engine);
    }
    
    @Test
    public void testValidStop()
    {
        TestUtil.assertEquals("1", 
                "«test(1)»#test(int item)«item»#stop(item == 1)one#end", 
                engine);
    }
    
    @Test
    public void testInvalidContextDirective()
    {
        TestUtil.assertFail(
                "«test(1)»#test(int item)«item»#if(item==1)one#put(\"2\", 2)#endif#end", 
                engine);
    }
    
    @Test
    public void testFunction()
    {
        TestUtil.assertEquals("2one", 
                "«test(incr(1))»#test(int item)«item»#stop(item == 1)one#end#incr(int x)::int\nreturn x + 1;\n#end", 
                engine);
    }
    
    @Test
    public void testEmit()
    {
        TestUtil.assertEquals("3one", 
                "«test(incr(1))»#test(int item)«#emit»\nitem++;\n«#»«item»#stop(item == 1)one#end#incr(int x)::int\nreturn x + 1;\n#end", 
                engine);
    }
    
    @Test
    public void testTrailingSpaceAfterNewLine()
    {
        TestUtil.assertEquals("3one\n ", 
                "«test(incr(1))»\n #test(int item)«#emit»\nitem++;\n«#»«item»#stop(item == 1)one#end#incr(int x)::int\nreturn x + 1;\n#end", 
                engine);
    }
    
    @Test
    public void testOptions()
    {
        TestUtil.assertEquals("1bar", 
                "«test(1)»#test(int item)«item; foo=\"bar\"»#end\n#foo(Object it, String param)::String\nreturn it == null ? \"\" : String.valueOf(it) + param;\n#end", 
                engine);
    }
    
    @Test
    public void testInlineExprWithSeparator()
    {
        TestUtil.assertEquals("1!,2!,3!", 
                "«[1,2,3]:Integer:item_detail(\"!\"); separator=\",\"»#item_detail(int item, String suffix)«item»«suffix»#end\n#separator(Object it, String param, int i)«if(i != 0)»«param»«endif»#end", 
                engine);
    }
    
    @Test
    public void testInlineExprPrimitiveWithSeparator()
    {
        TestUtil.assertEquals("1!,2!,3!", 
                "«[1,2,3]:int:item_detail(\"!\"); separator=\",\"»#item_detail(int item, String suffix)«item»«suffix»#end\n#separator(Object it, String param, int i)«if(i != 0)»«param»«endif»#end", 
                engine);
    }
    
    @Test
    public void testSeparator()
    {
        TestUtil.assertEquals("1!,2!,3!", 
                "«test([1,2,3])»#test(List<Integer> items)«items:Integer:item_detail(\"!\"); separator=\",\"»#end\n#item_detail(int item, String suffix)«item»«suffix»#end\n#separator(Object it, String param, int i)«if(i != 0)»«param»«endif»#end", 
                engine);
    }
    
    @Test
    public void testSeparatorLine()
    {
        TestUtil.assertEquals("1!\n2!\n3!\n", 
                "«test([1,2,3])»\n#test(List<Integer> items)«items:Integer:item_detail(\"!\"); separator=\"\\n\"»#end\n#item_detail(int item, String suffix)«item»«suffix»#end\n#separator(Object it, String param, int i)\n«if(i != 0)»«param»«endif»#end", 
                engine);
    }
    
    @Test
    public void testSeparatorLineIndent()
    {
        TestUtil.assertEquals("  @1!\n  @2!\n  @3!\n", 
                "«test([1,2,3])»\n#test(List<Integer> items)\n  «items:Integer:item_detail(\"!\"); separator=\"\\n\"»#end\n#item_detail(int item, String suffix)\n@«item»«suffix»\n#end\n#separator(Object it, String param, int i)\n«#emit»if (i != 0) $out.print(param);«#»#end", 
                engine);
    }
    
    @Test
    public void testSeparatorLineIndentImportedFunction()
    {
        TestUtil.assertEquals("  1!\n  2!\n  3!\n", 
                "#import /template/proc.rel\n" +
                "«test([1,2,3])»\n#test(List<Integer> items)\n  «items:Integer:proc_rel::foo(\"!\"); separator=\"\\n\"»#end\n#item_detail(int item, String suffix)\n@«item»«suffix»\n#end\n#separator(Object it, String param, int i)\n«#emit»if (i != 0) $out.print(param);«#»#end", 
                TestUtil.createEngine("src/test/resources/"));
    }
    
    @Test
    public void testImportedSeparator()
    {
        TestUtil.assertEquals("  1!\n  2!\n  3!\n", 
                "#import /template/proc#\n" +
                "#import /template/proc.rel\n" +
                "«test([1,2,3])»\n#test(List<Integer> items)\n  «items:Integer:proc_rel::foo(\"!\"); separator=\"\\n\"»#end", 
                TestUtil.createEngine("src/test/resources/"));
    }
    
    @Test
    public void testValueIterIndent()
    {
        TestUtil.assertEquals("  1!\n  2!", 
                "#import /template/proc#\n" +
                "#import /template/proc.rel\n" +
                "«test(slice([1,2,3], 0, 2))»#test(List<Integer> items)\n  «items:Integer:proc_rel::foo(\"!\"); separator=\"\\n\"»\n#end\n#slice(List<Integer> items, int start, int end)::List<Integer>\nreturn items.subList(start, end);\n#end", 
                TestUtil.createEngine("src/test/resources/"));
    }
    
    @Test
    public void testValueIterIndentSingle()
    {
        TestUtil.assertEquals("  1!", 
                "#import /template/proc#\n" +
                "#import /template/proc.rel\n" +
                "«test(slice([1,2,3], 0, 1))»#test(List<Integer> items)\n  «items:Integer:proc_rel::foo(\"!\"); separator=\"\\n\"»\n#end\n#slice(List<Integer> items, int start, int end)::List<Integer>\nreturn items.subList(start, end);\n#end", 
                TestUtil.createEngine("src/test/resources/"));
    }
    
    @Test
    public void testValueIterIndentEmpty()
    {
        TestUtil.assertEquals("", 
                "#import /template/proc#\n" +
                "#import /template/proc.rel\n" +
                "«test(slice([1,2,3], 0, 0))»#test(List<Integer> items)\n  «items:Integer:proc_rel::foo(\"!\"); separator=\"\\n\"»\n#end\n#slice(List<Integer> items, int start, int end)::List<Integer>\nreturn items.subList(start, end);\n#end", 
                TestUtil.createEngine("src/test/resources/"));
    }
}
