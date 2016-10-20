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
package com.dyuproject.jetg.compiler;

public class CompileErrorException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CompileErrorException() {
        super();
    }

    public CompileErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompileErrorException(String message) {
        super(message);
    }

    public CompileErrorException(Throwable cause) {
        super(cause);
    }
}
