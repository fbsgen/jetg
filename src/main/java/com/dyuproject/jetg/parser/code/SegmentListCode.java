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
package com.dyuproject.jetg.parser.code;

import java.util.*;
import com.dyuproject.jetg.utils.ArrayUtils;

/**
 * 主要用于存储 expression_list 和 type_list
 */
public class SegmentListCode extends Code {
    public static final SegmentListCode EMPTY = new SegmentListCode(0);
    
    public int optionalCount;

    public final List<SegmentCode> children;

    public SegmentListCode(int initialCapacity) {
        if (initialCapacity == 0) {
            this.children = Collections.emptyList();
        } else {
            this.children = new ArrayList<SegmentCode>(initialCapacity);
        }
    }

    public void addChild(SegmentCode code) {
        children.add(code);
    }

    public List<SegmentCode> getChildren() {
        return children;
    }

    public SegmentCode getChild(int i) {
        return children.get(i);
    }

    public int size() {
        return children.size();
    }

    public Class<?>[] getParameterTypes() {
        return getParameterTypes(null);
    }

    public Class<?>[] getParameterTypes(Class<?> suffix) {
        int size = children.size();
        if (size == 0 && suffix == null) {
            return ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        int padding = (suffix == null) ? 0 : 1;
        Class<?>[] parameterTypes = new Class[size + padding];
        parameterTypes[0] = suffix;
        for (int i = 0; i < size; i++) {
            parameterTypes[i + padding] = getChild(i).getKlass();
        }
        return parameterTypes;
    }

    @Override
    public String toString() {
        return toString(children, 0, children.size());
    }
    
    public String toString(List<SegmentCode> children, int offset, int limit) {
        if (offset == limit) return "";

        StringBuilder sb = new StringBuilder(32);
        
        while (offset != limit) {
            SegmentCode code = children.get(offset++);
            if (sb.length() > 0) {
                sb.append(',').append(' ');
            }
            sb.append(code.toString());
        }
        return sb.toString();
    }
}
