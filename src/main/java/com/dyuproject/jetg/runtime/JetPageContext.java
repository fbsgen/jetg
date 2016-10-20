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
import com.dyuproject.jetg.utils.PathUtils;

public class JetPageContext implements JetContextAware {
    private final JetTemplate template;
    private final JetWriter out;
    private final JetContext context;

    public JetPageContext(JetTemplate template, JetContext context, JetWriter out) {
        this.template = template;
        this.context = context;
        this.out = out;
    }

    public JetEngine getEngine() {
        return template.getEngine();
    }

    public JetTemplate getTemplate() {
        return template;
    }

    public JetWriter getWriter() {
        return out;
    }

    @Override
    public JetContext getContext() {
        return context;
    }

    /**
     * 基于当前模板路径，转换子模板相对路径为绝对路径。
     */
    public String getAbsolutionName(String name) {
        return PathUtils.getAbsolutionName(template.getName(), name);
    }
}
