//========================================================================
//Copyright 2015 David Yu
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package com.dyuproject.jetg;

import java.util.Arrays;

/**
 * Jet utils.
 * 
 * @author David Yu
 * @created Nov 27, 2015
 */
public final class JetUtil
{
    private JetUtil() {}
    
    public static byte[] repeat(byte value, int count)
    {
        byte[] ret = new byte[count];
        Arrays.fill(ret, value);
        return ret;
    }
    
    public static char[] repeat(char value, int count)
    {
        char[] ret = new char[count];
        Arrays.fill(ret, value);
        return ret;
    }
}
