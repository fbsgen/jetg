package com.dyuproject.jetg.utils.finder;

import java.io.InputStream;
import com.dyuproject.jetg.JetAnnotations;
import com.dyuproject.jetg.utils.ClassLoaderUtils;
import org.junit.Assert;
import org.junit.Test;

public class AnnotationClassReaderTestCase {
    @Test
    public void test() {
        AnnotationClassReader f = new AnnotationClassReader();
        f.addAnnotation(JetAnnotations.Tags.class);
        InputStream is = ClassLoaderUtils.getContextClassLoader().getResourceAsStream("testcase/TagUtils.class");
        Assert.assertEquals(true, f.isAnnotationed(is));
    }
}
