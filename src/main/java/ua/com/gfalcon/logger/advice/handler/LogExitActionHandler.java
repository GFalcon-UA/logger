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

import static java.util.Collections.EMPTY_MAP;
import static java.util.Collections.singletonMap;
import static java.util.Objects.nonNull;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import static ua.com.gfalcon.logger.LoggingConstants.RETURNED_RESULT;
import ua.com.gfalcon.logger.PrettyLoggable;
import ua.com.gfalcon.logger.advice.handler.base.LogFlowActionHandler;
import ua.com.gfalcon.logger.annotation.Log;
import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.parameters.loggabletype.AnnotatedObject;
import ua.com.gfalcon.logger.parameters.loggabletype.util.AnnotationReflectionLookupUtils;

/**
 * Log exit action handler.
 */
public class LogExitActionHandler extends LogFlowActionHandler {

    private AnnotationReflectionLookupUtils reflectionLookupUtils;

    public LogExitActionHandler(PrettyLoggable prettyLoggable, AnnotationReflectionLookupUtils reflectionLookupUtils) {
        super(prettyLoggable, reflectionLookupUtils);
    }

    @Override
    public void perform(Map<String, Object> params) {
        Method method = (Method) params.get(METHOD_PARAM);
        if (!isApplicable(method, Log.Exit.class)) {
            return;
        }

        Exception exception = (Exception) params.get(EXCEPTION_PARAM);
        if (nonNull(exception)) {
            prettyLoggable.logError(exception.getMessage(), EMPTY_MAP, exception);
            return;
        }

        Map<String, Object> contextParams = getContextParams(method, params.get(INVOCATION_RESULT_PARAM));
        prettyLoggable.logDebug("< -- " + method.getName() + "()", contextParams);
    }

    private Map<String, Object> getContextParams(Method method, Object invocationResult) {
        if (isVoidReturn(method.getReturnType())) {
            return Collections.singletonMap(RETURNED_RESULT, "void");
        }

        return getLoggableTypesContextInfo(singletonMap(RETURNED_RESULT,
                AnnotatedObject.createWithAnnotation(invocationResult, LoggableType.class)));
    }

    private boolean isVoidReturn(Class<?> returnType) {
        return returnType.equals(void.class);
    }
}
