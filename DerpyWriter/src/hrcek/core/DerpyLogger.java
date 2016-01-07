/*
 * The MIT License
 *
 * Copyright 2016 Michael Hrcek <hrcekmj@clarkson.edu>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hrcek.core;

import java.util.ArrayList;

/**
 *
 * @author Michael Hrcek <hrcekmj@clarkson.edu>
 */
public class DerpyLogger {

    private static volatile boolean isDebug = false;

    public static synchronized boolean isInDebugMode() {
        return isDebug;
    }

    public static synchronized void setDebugMode(boolean mode) {
        isDebug = mode;
    }

    public static synchronized void warning(String msg) {
        warning(null, msg);
    }

    public static synchronized void warning(Exception e) {
        warning(e, "");
    }

    public static synchronized void warning(Exception e, String msg) {
        if (isInDebugMode()) {
            if (msg == null || msg.equals("")) {
                System.err.println("[WARNING] " + e.getLocalizedMessage());
            } else {
                System.err.println("[WARNING] " + msg);
            }

            if (e != null) {
                e.printStackTrace();
            }
        }
    }

    public static void error(String msg) {
        error(null, msg);
    }

    public static void error(Exception e) {
        error(e, "");
    }

    public static void error(Exception e, String msg) {
        if (msg == null || msg.equals("")) {
            System.err.println("[ERROR] " + e.getLocalizedMessage());
        } else {
            System.err.println("[ERROR] " + msg);
        }

        if (e != null) {
            e.printStackTrace();
        }

        System.exit(-1);

    }

}
