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
package com.dyuproject.jetg.runtime;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.*;
import com.dyuproject.jetg.JetContext;
import com.dyuproject.jetg.JetTemplate;
import com.dyuproject.jetg.resource.Resource;
import com.dyuproject.jetg.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JetUtils {
    private static final Logger log = LoggerFactory.getLogger(JetUtils.class);
    
    /**
     * Overridable.
     */
    public static String STR_TRUE = "true";
    
    public static boolean asBoolean(boolean value) {
        return value;
    }

    public static boolean asBoolean(String value) {
        return value != null && (STR_TRUE.equals(value) || !value.isEmpty());
    }
    
    public static boolean asBoolean(Object value) {
        if (value == null) return false;
        if (value instanceof String)
            return STR_TRUE.equals(value) || !value.toString().isEmpty();

        Class<?> klass = value.getClass();
        if (Boolean.class.equals(klass)) return (Boolean) value;
        if (value instanceof Collection) return !((Collection<?>) value).isEmpty();
        if (value instanceof Map) return !((Map<?, ?>) value).isEmpty();
        if (value instanceof CharSequence) return ((CharSequence) value).length() > 0;
        if (value instanceof Number) return ((Number) value).intValue() != 0;
        if (klass.isArray()) return Array.getLength(value) > 0;
        if (value instanceof Character) return ((Character) value) != '\0';
        if (value instanceof Enumeration) return ((Enumeration<?>) value).hasMoreElements();
        if (value instanceof Iterator) return ((Iterator<?>) value).hasNext();
        if (value instanceof Iterable) return ((Iterable<?>) value).iterator().hasNext();
        return true;
    }
    
    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }
    
    public static boolean isEmpty(Map<?,?> m) {
        return m == null || m.isEmpty();
    }
    
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }
    
    public static boolean isEmpty(boolean[] array) {
        return array == null || array.length == 0;
    }
    
    public static boolean isEmpty(int[] array) {
        return array == null || array.length == 0;
    }
    
    public static boolean isEmpty(long[] array) {
        return array == null || array.length == 0;
    }
    
    public static boolean isEmpty(float[] array) {
        return array == null || array.length == 0;
    }
    
    public static boolean isEmpty(double[] array) {
        return array == null || array.length == 0;
    }
    
    public static boolean isEmpty(String[] array) {
        return array == null || array.length == 0;
    }

    /**
     * @deprecated From 1.1.3 改为 JetForIterator 内部实现.
     */
    @SuppressWarnings("rawtypes")
    public static Iterator<?> asIterator(Object value) {
        return new JetForIterator(value);
    }

    /**
     * 返回一个 Map。类似于 Arrays.asList(...)。
     * 
     * <p>注意：这里必须去掉泛型类型，使用 rawtypes, 防止生成的模板出现编译错误。</p>
     */
    @SuppressWarnings("rawtypes")
    public static Map asMap(Object... values) {
        if (values == null || values.length == 0) {
            return Collections.emptyMap();
        }
        if (values.length % 2 == 1) {
            throw new IllegalArgumentException("Mismatched arguments count.");
        }
        Map<Object, Object> map = new HashMap<Object, Object>(values.length);
        for (int i = 0; i < values.length; i += 2) {
            map.put(values[i], values[i + 1]);
        }
        return map;
    }

    public static byte[] asBytes(String value, String encoding) {
        try {
            return value.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            throw ExceptionUtils.uncheck(e);
        }
    }
    
    public static boolean asEquals(boolean a, boolean b) {
        return a == b;
    }
    
    public static boolean asEquals(int a, int b) {
        return a == b;
    }
    
    public static boolean asEquals(long a, long b) {
        return a == b;
    }
    
    public static boolean asEquals(float a, float b) {
        return a == b;
    }
    
    public static boolean asEquals(double a, double b) {
        return a == b;
    }
    
    public static boolean asEquals(Object a, Object b) {
        return a != null ? a.equals(b) : (b == null);
    }
    
    public static boolean asNotEquals(boolean a, boolean b) {
        return a != b;
    }
    
    public static boolean asNotEquals(int a, int b) {
        return a != b;
    }
    
    public static boolean asNotEquals(long a, long b) {
        return a != b;
    }
    
    public static boolean asNotEquals(float a, float b) {
        return a != b;
    }
    
    public static boolean asNotEquals(double a, double b) {
        return a != b;
    }
    
    public static boolean asNotEquals(Object obj1, Object obj2) {
        return !asEquals(obj1, obj2);
    }
    
    public static int asCompareWith(int a, int b) {
        return Integer.compare(a, b);
    }
    
    public static int asCompareWith(int a, Number b) {
        return b == null ? 1 : Integer.compare(a, b.intValue());
    }
    
    public static int asCompareWith(long a, long b) {
        return Long.compare(a, b);
    }
    
    public static int asCompareWith(long a, Number b) {
        return b == null ? 1 : Long.compare(a, b.longValue());
    }
    
    public static int asCompareWith(float a, float b) {
        return Float.compare(a, b);
    }
    
    public static int asCompareWith(float a, Number b) {
        return b == null ? 1 : Float.compare(a, b.floatValue());
    }
    
    public static int asCompareWith(double a, double b) {
        return Double.compare(a, b);
    }
    
    public static int asCompareWith(double a, Number b) {
        return b == null ? 1 : Double.compare(a, b.doubleValue());
    }
    
    // upcast
    
    public static int asCompareWith(Number a, long b) {
        return a == null ? -1 : Long.compare(a.longValue(), b);
    }
    
    public static int asCompareWith(Number a, double b) {
        return a == null ? -1 : Double.compare(a.doubleValue(), b);
    }

    @SuppressWarnings("unchecked")
    public static int asCompareWith(Object lhs, Object rhs) {
        if (lhs == rhs) return 0;

        // null compare
        if (lhs == null) return -1;
        if (rhs == null) return 1;

        // numeric compare
        if (Character.class.equals(lhs)) {
            lhs = Integer.valueOf(((Character) lhs).charValue());
        }
        if (Character.class.equals(rhs)) {
            rhs = Integer.valueOf(((Character) rhs).charValue());
        }
        if (lhs instanceof Number && rhs instanceof Number) {
            return (int) (((Number) lhs).doubleValue() - ((Number) rhs).doubleValue());
        }

        // object compare
        if (lhs instanceof Comparable) {
            return ((Comparable<Object>) lhs).compareTo(rhs);
        }

        throw new ClassCastException(lhs.getClass().getName() + " cannot be cast to java.util.Comparable");
    }

    public static String asEscapeHtml(String value) {
        return StringEscapeUtils.escapeXml(value);
    }

    // render 子模板，并直接输出
    public static void asInclude(JetPageContext ctx, String relativeName, Map<String, Object> parameters) {
        if (relativeName == null || relativeName.length() == 0) {
            throw new IllegalArgumentException("argument relativeName is null or empty.");
        }
        String file = ctx.getAbsolutionName(relativeName);
        JetTemplate template = ctx.getEngine().getTemplate(file);
        JetContext context = new JetContext(ctx.getContext(), parameters);
        JetWriter writer = ctx.getWriter();
        template.render(context, writer);
    }

    // render 子模板，并返回生成的内容
    public static String asIncludeContent(JetPageContext ctx, String relativeName, Map<String, Object> parameters) {
        if (relativeName == null || relativeName.length() == 0) {
            throw new IllegalArgumentException("argument relativeName is null or empty.");
        }
        String file = ctx.getAbsolutionName(relativeName);
        JetTemplate template = ctx.getEngine().getTemplate(file);
        JetContext context = new JetContext(ctx.getContext(), parameters);

        UnsafeCharArrayWriter os = new UnsafeCharArrayWriter();
        template.render(context, os);
        return os.toString();
    }

    // 读取纯文本内容
    public static String asReadContent(JetPageContext ctx, String relativeName, String encoding) {
        if (relativeName == null || relativeName.length() == 0) {
            throw new IllegalArgumentException("argument relativeName is null or empty.");
        }
        String file = ctx.getAbsolutionName(relativeName);
        Resource resource = ctx.getEngine().getResource(file);
        if (encoding == null) {
            encoding = ctx.getEngine().getConfig().getOutputEncoding();
        }
        return new String(resource.getSource(encoding));
    }

    public static void debug(String format, Object... args) {
        if (log.isDebugEnabled()) {
            format = "template debug: " + format;
            log.debug(format, args);
        }
    }
}
