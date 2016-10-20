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

import static com.dyuproject.jetg.JetUtil.repeat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public abstract class JetWriter {

    protected int indent = 0, conditionalIndent = 0;
    protected boolean activeCondition, skipPrintln;
    
    public static JetWriter create(Writer os, String encoding) {
        return new JetWriterImpl(os, encoding);
    }

    public static JetWriter create(OutputStream os, String encoding) {
        return new JetOutputStreamImpl(os, encoding);
    }
    
    public abstract boolean isStreaming();
    
    public void $push(int indent)
    {
        activeCondition = true;
        skipPrintln = false;
        conditionalIndent = indent;
    }
    
    /**
     * Pass false to print a line if it hasn't been printed yet.
     */
    public void $pop(boolean skipNewline) throws IOException {
        if (skipNewline) {
            if (activeCondition) {
                skipPrintln = true;
                activeCondition = false;
            }
        } else if (activeCondition) {
            // nothing printed
            activeCondition = false;
        } else if (!skipPrintln) {
            // add a println
            println();
        }
    }
    
    public void $print(String text, byte[] bytes) throws IOException
    {
        if (activeCondition) {
            // first item
            activeCondition = false;
            printIndent(conditionalIndent);
        }
        
        print(text, bytes);
    }

    public abstract void print(String text, byte[] bytes) throws IOException;
    
    public abstract void print(int leadingSpaces, String text, byte[] bytes) throws IOException;
    
    public void $printSpace(int count) throws IOException
    {
        if (activeCondition) {
            // first item
            activeCondition = false;
            printIndent(conditionalIndent);
        }
        
        printSpace(count);
    }
    
    public abstract void printSpace(int count) throws IOException;
    
    public abstract void printLine(int count) throws IOException;
    
    public void printIndent(int count) throws IOException {
        count += indent;
        if (count != 0)
            printSpace(count);
    }
    
    public void indent(int count) {
        indent += count;
    }
    
    public void $print(boolean x) throws IOException {
        if (activeCondition) {
            // first item
            activeCondition = false;
            printIndent(conditionalIndent);
        }
        
        print(x);
    }
    
    public void print(boolean x) throws IOException {
        print(x ? "true" : "false");
    }
    
    public void $print(byte x) throws IOException {
        if (activeCondition) {
            // first item
            activeCondition = false;
            printIndent(conditionalIndent);
        }
        
        print(x);
    }
    
    public void $print(short x) throws IOException {
        if (activeCondition) {
            // first item
            activeCondition = false;
            printIndent(conditionalIndent);
        }
        
        print(x);
    }
    
    public void $print(int x) throws IOException {
        if (activeCondition) {
            // first item
            activeCondition = false;
            printIndent(conditionalIndent);
        }
        
        print(x);
    }
    
    public void $print(long x) throws IOException {
        if (activeCondition) {
            // first item
            activeCondition = false;
            printIndent(conditionalIndent);
        }
        
        print(x);
    }
    
    public void $print(float x) throws IOException {
        if (activeCondition) {
            // first item
            activeCondition = false;
            printIndent(conditionalIndent);
        }
        
        print(x);
    }
    
    public void $print(double x) throws IOException {
        if (activeCondition) {
            // first item
            activeCondition = false;
            printIndent(conditionalIndent);
        }
        
        print(x);
    }
    
    public void $print(Boolean x) throws IOException {
        if (activeCondition) {
            // first item
            activeCondition = false;
            printIndent(conditionalIndent);
        }
        
        print(x);
    }
    
    public void $print(Number x) throws IOException {
        if (activeCondition) {
            // first item
            activeCondition = false;
            printIndent(conditionalIndent);
        }
        
        print(x);
    }
    
    public void $print(Character x) throws IOException {
        if (activeCondition) {
            // first item
            activeCondition = false;
            printIndent(conditionalIndent);
        }
        
        print(x);
    }

    public void print(byte x) throws IOException {
        print(String.valueOf(x));
    }

    public void print(short x) throws IOException {
        print(String.valueOf(x));
    }

    public void print(int x) throws IOException {
        print(String.valueOf(x));
    }

    public void print(long x) throws IOException {
        print(String.valueOf(x));
    }

    public void print(float x) throws IOException {
        print(String.valueOf(x));
    }

    public void print(double x) throws IOException {
        print(String.valueOf(x));
    }

    public void print(Boolean x) throws IOException {
        if (x != null) {
            print(x.booleanValue() ? "true" : "false");
        }
    }

    public void print(Number x) throws IOException {
        if (x != null) {
            print(x.toString());
        }
    }

    public void print(Character x) throws IOException {
        if (x != null) {
            print(x.charValue());
        }
    }
    
    public void $print(char x) throws IOException {
        if (activeCondition) {
            // first item
            activeCondition = false;
            printIndent(conditionalIndent);
        }
        
        print(x);
    }
    
    public void $print(byte[] x) throws IOException {
        if (x != null)
            $print(x, 0, x.length);
    }
    
    public void $print(char[] x) throws IOException {
        if (x != null)
            $print(x, 0, x.length);
    }
    
    public void $print(byte[] x, int offset, int len) throws IOException {
        if (activeCondition) {
            // first item
            activeCondition = false;
            printIndent(conditionalIndent);
        }
        
        print(x, offset, len);
    }
    
    public void $print(char[] x, int offset, int len) throws IOException {
        if (activeCondition) {
            // first item
            activeCondition = false;
            printIndent(conditionalIndent);
        }
        
        print(x, offset, len);
    }
    
    public void $print(CharSequence x) throws IOException {
        if (activeCondition) {
            // first item
            activeCondition = false;
            printIndent(conditionalIndent);
        }
        
        print(x);
    }
    
    public void $print(Object x) throws IOException {
        if (activeCondition) {
            // first item
            activeCondition = false;
            printIndent(conditionalIndent);
        }
        
        print(x);
    }

    public abstract void print(char x) throws IOException;
    
    public void print(byte[] x) throws IOException {
        if (x != null)
            print(x, 0, x.length);
    }
    
    public void print(char[] x) throws IOException {
        if (x != null)
            print(x, 0, x.length);
    }

    public abstract void print(byte[] x, int offset, int len) throws IOException;

    public abstract void print(char[] x, int offset, int len) throws IOException;

    public abstract void print(CharSequence x) throws IOException;

    public void print(Object x) throws IOException {
        if (x != null) {
            if (x instanceof byte[]) {
                print((byte[]) x);
            } else if (x instanceof char[]) {
                print((char[]) x);
            } else {
                print(x.toString());
            }
        }
    }

    public abstract void println() throws IOException;

    /*public void println(boolean x) throws IOException {
        print(x);
        println();
    }

    public void println(byte x) throws IOException {
        print(x);
        println();
    }

    public void println(char x) throws IOException {
        print(x);
        println();
    }

    public void println(short x) throws IOException {
        print(x);
        println();
    }

    public void println(int x) throws IOException {
        print(x);
        println();
    }

    public void println(long x) throws IOException {
        print(x);
        println();
    }

    public void println(float x) throws IOException {
        print(x);
        println();
    }

    public void println(double x) throws IOException {
        print(x);
        println();
    }

    public void println(byte x[]) throws IOException {
        if (x != null) {
            print(x);
            println();
        }
    }

    public void println(char x[]) throws IOException {
        if (x != null) {
            print(x);
            println();
        }
    }

    public void println(CharSequence x) throws IOException {
        if (x != null) {
            print(x);
            println();
        }
    }

    public void println(Object x) throws IOException {
        if (x != null) {
            print(x);
            println();
        }
    }*/

    public abstract void flush() throws IOException;

    public abstract void close() throws IOException;
    
    static class JetWriterImpl extends JetWriter {
        private static final char[] INDENT = repeat(' ', 512),
                NEWLINE = repeat('\n', 64);
        
        private final Writer os;
        private final String encoding;

        public JetWriterImpl(Writer os, String encoding) {
            this.os = os;
            this.encoding = encoding;
        }

        @Override
        public boolean isStreaming() {
            return false;
        }

        @Override
        public void print(String text, byte[] bytes) throws IOException {
            os.write(text);
        }

        @Override
        public void print(char x) throws IOException {
            os.write(x);
        }

        @Override
        public void print(byte x[], int offset, int len) throws IOException {
            os.write(new String(x, offset, len, encoding));
        }

        @Override
        public void print(char x[], int offset, int len) throws IOException {
            os.write(x, offset, len);
        }

        @Override
        public void print(CharSequence x) throws IOException {
            if (x == null)
                return;
            
            os.write(x.toString());
        }

        @Override
        public void println() throws IOException {
            os.write('\n');
        }
        
        @Override
        public void printLine(int count) throws IOException {
            os.write(NEWLINE, 0, count);
        }
        
        @Override
        public void printSpace(int count) throws IOException {
            os.write(INDENT, 0, count);
        }

        @Override
        public void print(int leadingSpaces, String text, byte[] bytes) throws IOException {
            int count = indent + leadingSpaces;
            if (count != 0)
                os.write(INDENT, 0, count);
            
            print(text, bytes);
        }
        
        @Override
        public void flush() throws IOException {
            os.flush();
        }

        @Override
        public void close() throws IOException {
            os.close();
        }
    }

    static class JetOutputStreamImpl extends JetWriter {
        private static final byte[] INDENT = repeat((byte)' ', 512),
                NEWLINE = repeat((byte)'\n', 64);
        
        private final OutputStream os;
        private final String encoding;

        public JetOutputStreamImpl(OutputStream os, String encoding) {
            this.os = os;
            this.encoding = encoding;
        }

        @Override
        public boolean isStreaming() {
            return true;
        }

        @Override
        public void print(String text, byte[] bytes) throws IOException {
            os.write(bytes);
        }

        @Override
        public void print(char x) throws IOException {
            os.write(x);
        }

        @Override
        public void print(byte x[], int offset, int len) throws IOException {
            os.write(x, offset, len);
        }

        @Override
        public void print(char x[], int offset, int len) throws IOException {
            os.write(new String(x, offset, len).getBytes(encoding));
        }

        @Override
        public void print(CharSequence x) throws IOException {
            if (x == null)
                return;
            
            os.write(x.toString().getBytes(encoding));
        }

        @Override
        public void println() throws IOException {
            os.write((byte)'\n');
        }
        
        @Override
        public void printLine(int count) throws IOException {
            os.write(NEWLINE, 0, count);
        }
        
        @Override
        public void printSpace(int count) throws IOException {
            os.write(INDENT, 0, count);
        }
        
        @Override
        public void print(int leadingSpaces, String text, byte[] bytes) throws IOException {
            int count = indent + leadingSpaces;
            if (count != 0)
                os.write(INDENT, 0, count);
            
            print(text, bytes);
        }
        
        @Override
        public void flush() throws IOException {
            os.flush();
        }

        @Override
        public void close() throws IOException {
            os.close();
        }
    }
}
