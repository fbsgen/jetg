package com.dyuproject.jetg;

import com.dyuproject.jetg.utils.UnsafeCharArrayWriter;

import org.junit.Assert;
import org.junit.Test;

public class ForDirectiveTest {
    private JetEngine engine = JetEngine.create();

    public enum MyEnum {
        aa, bb, cc, dd
    }

    @Test
    public void forEnum() {
        JetTemplate template = engine.createTemplate("#for(item: items)«item»#endfor");
        UnsafeCharArrayWriter out = new UnsafeCharArrayWriter();
        JetContext context = new JetContext();
        context.put("items", MyEnum.class);
        template.render(context, out);
        Assert.assertEquals(out.toString(), "aabbccdd");
    }
    
    @Test
    public void testInvalidBreak()
    {
        TestUtil.assertFail(
                "«test(1)»#test(int item)#break(item == 1)#endif#end",
                engine);
    }
    
    @Test
    public void testInvalidContinue()
    {
        TestUtil.assertFail(
                "«test(1)»#test(int item)#continue(item == 1)#endif#end",
                engine);
    }
}
