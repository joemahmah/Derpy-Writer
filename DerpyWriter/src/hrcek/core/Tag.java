/*
 * The MIT License
 *
 * Copyright 2015 Michael Hrcek <hrcekmj@clarkson.edu>.
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
import java.util.List;

/**
 *
 * @author Michael Hrcek <hrcekmj@clarkson.edu>
 */
public class Tag extends Word {

    List<String> params;

    public Tag(String name, String[] params) {
        super(name);
        if (this.params.equals(null)) {
            this.params = new ArrayList();
        }

        String param = "";
        for (int i = 0; i < params.length; i++) {
            param += params[i] + " ";
        }
        this.params.add(param);

    }

    public Tag(String name, String[] params, int size) {
        super(name, size);
        if (this.params.equals(null)) {
            this.params = new ArrayList();
        }

        String param = "";
        for (int i = 0; i < params.length; i++) {
            param += params[i] + " ";
        }
        this.params.add(param);
    }

    public String getParam() {
        return params.get(LogicFactory.getRandomInt(params.size() - 1));
    }

}
