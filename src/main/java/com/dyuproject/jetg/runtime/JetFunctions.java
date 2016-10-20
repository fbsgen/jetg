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
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.dyuproject.jetg.utils.ArrayUtils;

public final class JetFunctions {
    private static final Random RANDOM = new Random();

    public static Date now() {
        return new Date();
    }

    public static int random() {
        return RANDOM.nextInt();
    }

    public static UUID uuid() {
        return UUID.randomUUID();
    }

    /**
     * @since 1.0.2
     * @deprecated replaced by {@link #loop(int, int)}
     */
    @Deprecated
    public static Iterator<Integer> iterator(int start, int stop) {
        return ArrayUtils.iterator(start, stop, 1);
    }

    /**
     * @since 1.0.2
     * @deprecated replaced by {@link #loop(int, int, int)}
     */
    @Deprecated
    public static Iterator<Integer> iterator(int start, int stop, int step) {
        return ArrayUtils.iterator(start, stop, step);
    }

    /**
     * @since 1.2.6
     */
    public static Iterator<Integer> loop(int start, int stop) {
        return ArrayUtils.iterator(start, stop, 1);
    }

    /**
     * @since 1.2.6
     */
    public static Iterator<Integer> loop(int start, int stop, int step) {
        return ArrayUtils.iterator(start, stop, step);
    }

    // 读取子模板内容
    public static String include(JetPageContext ctx, String relativeName) throws IOException {
        return JetUtils.asIncludeContent(ctx, relativeName, null);
    }

    // 读取子模板内容
    public static String include(JetPageContext ctx, String relativeName, Map<String, Object> parameters) throws IOException {
        return JetUtils.asIncludeContent(ctx, relativeName, parameters);
    }

    // 读取纯文本内容
    public static String read(JetPageContext ctx, String relativeName) {
        return JetUtils.asReadContent(ctx, relativeName, null);
    }

    // 读取纯文本内容
    public static String read(JetPageContext ctx, String relativeName, String encoding) {
        return JetUtils.asReadContent(ctx, relativeName, encoding);
    }

    public static void debug(String format, Object... args) {
        JetUtils.debug(format, args);
    }

    /**
     * @since 1.2.0
     */
    /*public static String ctxpath(JetPageContext ctx) {
        return ctxpath(ctx, "");
    }*/

    /**
     * @since 1.2.0
     */
    /*public static String ctxpath(JetPageContext ctx, String url) {
        HttpServletRequest request = (HttpServletRequest) ctx.getContext().get(JetWebContext.REQUEST);
        return request.getContextPath() + url;
    }*/

    /**
     * @since 1.2.0
     */
    /*public static String webroot(JetPageContext ctx) {
        return webroot(ctx, "");
    }*/

    /**
     * @since 1.2.0
     */
    /*public static String webroot(JetPageContext ctx, String url) {
        HttpServletRequest request = (HttpServletRequest) ctx.getContext().get(JetWebContext.REQUEST);
        String schema = request.getScheme();
        int port = request.getServerPort();

        StringBuilder sb = new StringBuilder();
        sb.append(schema);
        sb.append("://");
        sb.append(request.getServerName());
        if (!(port == 80 && "http".equals(schema)) && !(port == 443 && "https".equals(schema))) {
            sb.append(':').append(request.getServerPort());
        }
        sb.append(request.getContextPath());
        sb.append(url);
        return sb.toString();
    }*/
}
