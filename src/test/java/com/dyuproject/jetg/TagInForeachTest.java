package com.dyuproject.jetg;

import java.util.Properties;
import com.dyuproject.jetg.runtime.JetTagContext;
import com.dyuproject.jetg.utils.UnsafeCharArrayWriter;
import org.junit.Assert;
import org.junit.Test;

public class TagInForeachTest {
    private JetEngine engine = getJetEngine();

    public JetEngine getJetEngine() {
        Properties config = new Properties();
        config.put(JetConfig.IMPORT_TAGS, myTag.class.getName());
        config.put(JetConfig.COMPILE_DEBUG, "true");
        return JetEngine.create(config);
    }

    @Test
    public void tagInFor() {
        String source = "#for(int i: iterator(1,9))#tag testTag()«i»#end#endfor";
        JetTemplate template = engine.createTemplate(source);
        UnsafeCharArrayWriter out = new UnsafeCharArrayWriter();
        JetContext context = new JetContext();
        template.render(context, out);
        Assert.assertEquals(out.toString(), "123456789");
    }

    public static class myTag {
        public static void testTag(JetTagContext ctx) {
            ctx.writeBodyContent();
        }
    }
}
