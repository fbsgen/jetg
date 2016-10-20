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
package com.dyuproject.jetg;

import java.util.*;

/**
 * 存放模板的 context 数据, 支持 chain
 */
public class JetContext {
    private final JetContext parent;
    private final Map<String, Object> context;
    private JetGlobalVariables globalVariables;

    public JetContext() {
        this(null, null);
    }

    public JetContext(Map<String, Object> context) {
        this(null, context);
    }

    public JetContext(JetContext parent, Map<String, Object> context) {
        this.parent = parent;

        if (context == null) {
            this.context = new HashMap<String, Object>();
        } else {
            this.context = new HashMap<String, Object>(context);
        }
    }

    protected boolean isSimpleModel() {
        return parent == null && JetContext.class.equals(getClass());
    }

    protected Map<String, Object> getContext() {
        return context;
    }

    public JetContext getParent() {
        return parent;
    }

    public Set<String> keySet() {
        return context.keySet();
    }

    public Object get(String name) {
        Object value = context.get(name);
        if (value == null && parent != null) {
            value = parent.get(name);
        }
        if (value == null && globalVariables != null) {
            value = globalVariables.get(this, name);
            if (value != null) {
                put(name, value); // resolved
            }
        }
        return value;
    }

    protected void setGlobalVariables(JetGlobalVariables globalVariables) {
        this.globalVariables = globalVariables;
    }

    /**
     * 只修改自己 Context，不影响 Parent
     * #set 指令用
     */
    public void put(String name, Object value) {
        context.put(name, value);
    }

    /**
     * 同 put(name, value)
     */
    public void putAll(Map<String, Object> context) {
        this.context.putAll(context);
    }

    /**
     * 修改所有的父 Context （支持子模板返回变量到父模板）
     * #put 指令用
     */
    public void putAsParents(String name, Object value) {
        context.put(name, value);
        if (parent != null) {
            parent.putAsParents(name, value);
        }
    }
}
