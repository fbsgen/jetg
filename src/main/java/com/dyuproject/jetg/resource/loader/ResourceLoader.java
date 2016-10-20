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
package com.dyuproject.jetg.resource.loader;

import java.util.List;
import com.dyuproject.jetg.JetEngine;
import com.dyuproject.jetg.resource.Resource;

public interface ResourceLoader {

    /**
     * 初始化 ResourceLoader.
     *
     * @param engine    模板引擎
     * @param basepath  模板根路径
     * @param encoding  模板默认输入编码
     */
    public void initialize(JetEngine engine, String basepath, String encoding);

    /**
     * 获取一个代表模板的 Resource.
     *
     * @param name  模板路径名
     * @return 如果模板不存在，那么返回 {@code null}
     */
    public Resource load(String name);

    /**
     * 获取所有的模板，主要用来实现预编译
     */
    public List<String> loadAll();

}
