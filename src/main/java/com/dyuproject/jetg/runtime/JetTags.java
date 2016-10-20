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

import java.io.IOException;
import java.util.Map;

import com.dyuproject.jetg.JetContext;
import com.dyuproject.jetg.JetTemplate;

/**
 * 系统自带的 Tag
 */
public final class JetTags {

    /**
     * 实现 layout 的 tag, 将内容插入到 ${bodyContent}
     *
     * @param ctx Tag 上下文对象
     * @param file layout 模板路径
     *
     * @since 1.1.0
     */
    public static void layout(JetTagContext ctx, String file) {
        layout(ctx, file, null);
    }

    /**
     * 实现 layout 的 tag, 将内容插入到 ${bodyContent}，支持传递参数
     *
     * @param ctx Tag 上下文对象
     * @param file layout 模板路径
     *
     * @since 1.1.1
     */
    public static void layout(JetTagContext ctx, String file, Map<String, Object> parameters) {
        JetContext context;
        if (parameters == null || parameters.size() == 0) {
            context = ctx.getContext();
        } else {
            context = new JetContext(ctx.getContext(), parameters);
        }
        String bodyContent = ctx.getBodyContent();
        ctx.getContext().put("bodyContent", bodyContent);

        file = ctx.getPageContext().getAbsolutionName(file);
        JetTemplate template = ctx.getEngine().getTemplate(file);
        template.render(context, ctx.getWriter());
    }

    /**
     * 将一个 Block 的内容保存到一个 JetContext 变量中。
     *
     * @param ctx Tag 上下文对象
     * @param name 保存到 JetContext 的变量名
     *
     * @since 1.1.1
     */
    public static void block(JetTagContext ctx, String name) {
        String bodyContent = ctx.getBodyContent();
        ctx.getContext().put(name, bodyContent);
    }

    /**
     * 如果不存在指定的 JetContext 变量，那么输出 default_block 块内容，否则输出指定的 JetContext 变量。
     *
     * @param ctx Tag 上下文对象
     * @param name JetContext 的变量名
     *
     * @since 1.1.2
     */
    public static void default_block(JetTagContext ctx, String name) throws IOException {
        Object value = ctx.getContext().get(name);
        if (value == null) {
            ctx.writeBodyContent();
        } else {
            ctx.getWriter().print(value);
        }
    }

    /**
     * ServletContext 级别的 cache.
     *
     * @param ctx Tag 上下文对象
     * @param name cache 名称
     * @param timeout 超时时间，单位秒
     *
     * @since 1.2.0
     */
    /*public static void application_cache(JetTagContext ctx, String name, long timeout) throws IOException {
        ServletContext sc = (ServletContext) ctx.getContext().get(JetWebContext.SERVLET_CONTEXT);
        TimedSizeCache cache = (TimedSizeCache) sc.getAttribute(TimedSizeCache.CACHE_KEY);
        if (cache == null) {
            cache = new TimedSizeCache(128);
            sc.setAttribute(TimedSizeCache.CACHE_KEY, cache);
        }
        Object value = cache.get(name);
        if (value == null) {
            value = ctx.getBodyContent();
            cache.put(name, value, timeout * 1000);
        }
        ctx.getWriter().print(value);
    }*/

    /**
     * HttpSession 级别的 cache.
     *
     * @param ctx Tag 上下文对象
     * @param name cache 名称
     * @param timeout 超时时间，单位秒
     *
     * @since 1.2.0
     */
    /*public static void session_cache(JetTagContext ctx, String name, long timeout) throws IOException {
        HttpSession session = (HttpSession) ctx.getContext().get(JetWebContext.SESSION);
        TimedSizeCache cache = (TimedSizeCache) session.getAttribute(TimedSizeCache.CACHE_KEY);
        if (cache == null) {
            cache = new TimedSizeCache(128);
            session.setAttribute(TimedSizeCache.CACHE_KEY, cache);
        }
        Object value = cache.get(name);
        if (value == null) {
            value = ctx.getBodyContent();
            cache.put(name, value, timeout * 1000);
        }
        ctx.getWriter().print(value);
    }*/
}
