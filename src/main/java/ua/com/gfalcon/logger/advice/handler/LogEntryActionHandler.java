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

package ua.com.gfalcon.logger.advice.handler;

import java.lang.reflect.Method;
import java.util.Map;

import ua.com.gfalcon.logger.PrettyLoggable;
import ua.com.gfalcon.logger.advice.handler.base.LogFlowActionHandler;
import ua.com.gfalcon.logger.annotation.Log;
import ua.com.gfalcon.logger.parameters.loggabletype.util.AnnotationReflectionLookupUtils;

public class LogEntryActionHandler extends LogFlowActionHandler {
    public LogEntryActionHandler(PrettyLoggable prettyLoggable, AnnotationReflectionLookupUtils reflectionLookupUtils) {
        super(prettyLoggable, reflectionLookupUtils);
    }

    @Override
    public void perform(Map<String, Object> params) {
        Method method = (Method) params.get(METHOD_PARAM);
        if (!isApplicable(method, Log.Entry.class)) {
            return;
        }

        Object[] args = (Object[]) params.get(METHOD_ARGS_PARAM);

        prettyLoggable.logDebug(method.getName() + "() -- >", getAdditionalContextInfo(method.getParameters(), args));
    }
}
