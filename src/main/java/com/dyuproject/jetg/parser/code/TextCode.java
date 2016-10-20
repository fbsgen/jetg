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

/**
 * 用于存储文本内容，方便后面进行 trim 分析
 */
public class TextCode extends Code {
    
    public static final String PRINT = "$out.print(",
            $PRINT = "$out.$print(",
            PRINT_SPACE = "$out.printSpace(",
            $PRINT_SPACE = "$out.$printSpace(",
            PRINT_INDENT = "$out.printIndent(";
    
    public static final TextCode NEWLINE = new TextCode(
            null, null, false, false, 0, false, PRINT, PRINT_SPACE);
    
    static int countLeadingSpaces(String text)
    {
        int i = 0, 
                len = text.length();
        
        while (i < len && text.charAt(i) == ' ') i++;
        
        return i;
    }
    
    private final String id;
    private String text;
    public final boolean countForTrimComments, countLeadingSpaces, allSpaces, addNewline;
    public final int leadingSpaces, indent;
    public final String print, printSpace;

    public TextCode(String id, String text, boolean countLeadingSpaces, 
            boolean countForTrimComments, int indent, boolean addNewline,
            String print, String printSpace) {
        this.id = id;
        this.text = text;
        this.countForTrimComments = countForTrimComments;
        this.countLeadingSpaces = countLeadingSpaces;
        boolean count = countLeadingSpaces || countForTrimComments;
        this.leadingSpaces = count ? countLeadingSpaces(text) : 0;
        this.allSpaces = count && leadingSpaces == text.length();
        this.indent = indent;
        this.addNewline = addNewline;
        this.print = print;
        this.printSpace = printSpace;
    }
    
    /*public int countIndentFromBack()
    {
        if (text == null)
            return 0;
        
        int i = text.length(),
                end;
        
        if (i == 0 || text.charAt(--i) != '\n')
            return 0;
        
        while (i > 0 && text.charAt(--i) != ' ');
        
        end = i;
        
        while (i > 0 && text.charAt(--i) == ' ');
        
        if (end == i || text.charAt(i) != '\n')
            return 0;
        
        // the spaces between \n   \n
        
        return end - i;
    }*/
    
    public void trimEmptyLine(boolean trimLeft, boolean trimRight, boolean keepLeftNewLine) {
        if (text == null || text.length() == 0) {
            return;
        }

        int lpos = 0;
        if (trimLeft) {
            int len = text.length();
            for (int i = 0; i < len; i++) {
                char c = text.charAt(i);
                if (c == ' ' || c == '\t') {
                    continue;
                } else if (c == '\r') {
                    if (keepLeftNewLine) {
                        lpos = i;
                        break;
                    } else {
                        continue;
                    }
                } else if (c == '\n') {
                    if (keepLeftNewLine) {
                        lpos = i;
                    } else {
                        lpos = i + 1;
                    }
                    break;
                } else {
                    break;
                }
            }
        }

        int rpos = text.length();
        if (trimRight) {
            for (int i = text.length() - 1; i >= 0; i--) {
                char c = text.charAt(i);
                if (c == ' ' || c == '\t' || c == '\r') {
                    continue;
                } else if (c == '\n') {
                    rpos = i + 1;
                    break;
                } else {
                    break;
                }
            }
        }

        if (lpos < rpos) {
            text = text.substring(lpos, rpos);
        } else {
            text = null;
        }
    }

    /**
     * Return true if the right comment was trimmed and only a single line break is remaining.
     */
    public boolean trimComments(boolean trimLeft, boolean trimRight, String prefix, String suffix) {
        if (text == null || text.length() == 0) {
            return false;
        }

        int lpos = 0;
        int text_len = text.length();
        int suffix_len = suffix.length();
        int prefix_len = prefix.length();
        if (trimLeft) {
            for (int i = 0; i < text_len; i++) {
                char c = text.charAt(i);
                if (c == ' ' || c == '\t') {
                    continue;
                } else {
                    boolean matched = true;
                    int j = 0;
                    while (i < text_len && j < suffix_len) {
                        if (text.charAt(i) != suffix.charAt(j)) {
                            matched = false;
                            break;
                        }
                        i++;
                        j++;
                    }
                    if (matched) {
                        lpos = i;
                    }
                    break;
                }
            }
        }

        int rpos = text_len;
        if (trimRight) {
            for (int i = text_len - 1; i >= 0; i--) {
                char c = text.charAt(i);
                if (c == ' ' || c == '\t') {
                    continue;
                } else {
                    boolean matched = true;
                    int j = prefix_len - 1;
                    while (i >= 0 && j >= 0) {
                        if (text.charAt(i) != prefix.charAt(j)) {
                            matched = false;
                            break;
                        }
                        i--;
                        j--;
                    }
                    if (matched) {
                        rpos = i + 1;
                    }
                    break;
                }
            }
        }

        if (lpos < rpos) {
            text = text.substring(lpos, rpos);
            return !trimRight && text.length() == 1 && text.charAt(0) == '\r';
        }
        
        text = null;
        return false;
    }

    public void trimLastNewLine() {
        if (text == null || text.length() == 0) {
            return;
        }

        int length = text.length();
        if (text.charAt(length - 1) == '\n') {
            length--;
            if (length > 0 && text.charAt(length - 1) == '\r') {
                length--;
            }
        }
        text = text.substring(0, length);
    }
    
    public String cacheText() {
        return leadingSpaces == 0 ? text : text.substring(leadingSpaces);
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public boolean isEmpty() {
        return text == null || text.length() == 0;
    }
    
    @Override
    public String toString() {
        return text == null ? null : toString(
                countLeadingSpaces, leadingSpaces, indent, addNewline, 
                print, printSpace);
    }

    public String toString(boolean countLeadingSpaces, int leadingSpaces, int indent, 
            boolean addNewline, String print, String printSpace) {
        StringBuilder builder = new StringBuilder();
        
        if (countLeadingSpaces || indent != 0)
            builder.append("$out.print(").append(leadingSpaces + indent).append(", ");
        else if (leadingSpaces != 0)
            builder.append(printSpace).append(leadingSpaces).append(");").append(print);
        else
            builder.append(print);
        
        builder.append(id).append(", ").append(id).append("_bytes);");
        
        if (addNewline)
            builder.append("$out.println();");
        
        return builder.toString();
        //return "$out.print(" + id + ", " + id + "_bytes);";
    }

}
