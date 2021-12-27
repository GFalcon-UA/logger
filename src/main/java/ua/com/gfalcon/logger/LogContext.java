/*
 * MIT License
 *
 * Copyright (c) 2018 NIX Solutions Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ua.com.gfalcon.logger;

import java.util.Map;

/**
 * Log context.
 *
 * @param <T> defines a type of a primary context(f.e. for QuoteLogContext it is probably a quoteId: {@link Long})
 * @param <E> defines a result of a context info processed(filtered,added, etc). Example: {@link java.lang.String} to
 *            print in log
 */
public interface LogContext<T, E> {
    E get(T context);

    E get(String message, T context);

    E get(String message, T context, Map<String, Object> params);

    E get(String message, Map<String, Object> params);

    /**
     * Compresses params into a single field.
     * Example: {quoteId: 123, groupId: 122}, "groupQtContext" - {groupQtContext: [quoteId: 123, groupId: 122]}
     *
     * @param contextParams - map of params to shrink
     * @param fieldName     - resulting field name
     * @return resulting map
     */
    Map<String, Object> shrinkParamsAsField(Map<String, Object> contextParams, String fieldName);
}
