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

import com.dyuproject.jetg.*;
import com.dyuproject.jetg.utils.ExceptionUtils;
import com.dyuproject.jetg.utils.UnsafeCharArrayWriter;

public abstract class JetTagContext implements JetContextAware {
    public static final Class<?>[] CLASS_ARRAY = { JetTagContext.class };
    private final JetPageContext ctx;

    public JetTagContext(JetPageContext ctx) {
        this.ctx = ctx;
    }

    public String getBodyContent() {
        UnsafeCharArrayWriter out = new UnsafeCharArrayWriter();
        String encoding = ctx.getEngine().getConfig().getOutputEncoding();
        try {
            render(ctx.getContext(), JetWriter.create(out, encoding));
        } catch (Throwable e) {
            handleException(e);
        }
        return out.toString();
    }

    public void writeBodyContent() {
        try {
            render(ctx.getContext(), ctx.getWriter());
        } catch (Throwable e) {
            handleException(e);
        }
    }

    private void handleException(Throwable e) {
        if ("org.apache.catalina.connector.ClientAbortException".equals(e.getClass().getName())) {
            // log.warn(e.toString());
        } else {
            throw ExceptionUtils.uncheck(e);
        }
    }

    public JetPageContext getPageContext() {
        return ctx;
    }

    public JetEngine getEngine() {
        return ctx.getEngine();
    }

    public JetWriter getWriter() {
        return ctx.getWriter();
    }

    @Override
    public JetContext getContext() {
        return ctx.getContext();
    }

    // 专门给 tag 内部用的 ctx
    protected JetPageContext getPageContext(JetWriter out) {
        if (out == ctx.getWriter()) {
            return ctx;
        } else {
            return new JetPageContext(ctx.getTemplate(), ctx.getContext(), out);
        }
    }

    protected abstract void render(final JetContext context, final JetWriter out) throws Throwable;
}
