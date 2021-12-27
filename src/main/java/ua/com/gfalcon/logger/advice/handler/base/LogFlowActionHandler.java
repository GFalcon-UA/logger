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

package ua.com.gfalcon.logger.advice.handler.base;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Map.Entry;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.tuple.Pair;

import static ua.com.gfalcon.logger.LoggingConstants.SINGLE_PROPERTY;
import ua.com.gfalcon.logger.PrettyLoggable;
import ua.com.gfalcon.logger.annotation.ContextParam;
import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.common.MapUtils;
import ua.com.gfalcon.logger.parameters.loggabletype.AnnotatedObject;
import ua.com.gfalcon.logger.parameters.loggabletype.LookupResult;
import ua.com.gfalcon.logger.parameters.loggabletype.util.AnnotationReflectionLookupUtils;

public abstract class LogFlowActionHandler extends AbstractLogActionHandler {
    protected AnnotationReflectionLookupUtils reflectionLookupUtils;

    public LogFlowActionHandler(PrettyLoggable prettyLoggable, AnnotationReflectionLookupUtils reflectionLookupUtils) {
        super(prettyLoggable);
        this.reflectionLookupUtils = reflectionLookupUtils;
    }

    protected Map<String, Object> getAdditionalContextInfo(Parameter[] methodParameters, Object[] methodArguments) {
        Map<String, AnnotatedObject<LoggableType>> nameAnnotatedObjectMap = MapUtils.toImmutableMap(methodParameters,
                        methodArguments)
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey()
                        .isAnnotationPresent(ContextParam.class))
                .map(this::toNameAnnotatedObject)
                .collect(toMap(Pair::getKey, Pair::getValue));

        return getLoggableTypesContextInfo(nameAnnotatedObjectMap);
    }

    protected Map<String, Object> getLoggableTypesContextInfo(
            Map<String, AnnotatedObject<LoggableType>> contextParamAnnotatedObjectMap) {
        return contextParamAnnotatedObjectMap.entrySet()
                .stream()
                .map(this::toNamedLookup)
                .filter(this::isResolved)
                .map(this::toNameResult)
                .collect(toMap(Pair::getKey, Pair::getValue));
    }

    private boolean isResolved(Pair<String, LookupResult> entry) {
        return entry.getValue()
                .isResolved();
    }

    private boolean isSinglePropertyContextParams(Map<String, Object> contextParams) {
        return contextParams.size() == 1 && nonNull(contextParams.get(SINGLE_PROPERTY));
    }

    private Pair<String, AnnotatedObject<LoggableType>> toNameAnnotatedObject(
            Entry<Parameter, Object> parameterObjectEntry) {
        ContextParam annotation = parameterObjectEntry.getKey()
                .getAnnotation(ContextParam.class);
        String paramName = parameterObjectEntry.getKey()
                .getName();
        if (isNotBlank(annotation.value())) {
            paramName = annotation.value();
        }

        return Pair.of(paramName,
                AnnotatedObject.createWithAnnotation(parameterObjectEntry.getValue(), LoggableType.class));
    }

    private Pair<String, Object> toNameResult(Pair<String, LookupResult> entry) {
        Map<String, Object> contextParams = entry.getRight()
                .executeForResult();
        if (isSinglePropertyContextParams(contextParams)) {
            return Pair.of(entry.getLeft(), contextParams.get(SINGLE_PROPERTY));
        }

        return Pair.of(entry.getLeft(), contextParams);
    }

    private Pair<String, LookupResult> toNamedLookup(Entry<String, AnnotatedObject<LoggableType>> entry) {
        return Pair.of(entry.getKey(), reflectionLookupUtils.strategyLookupForRootObj(entry.getValue()));
    }
}
