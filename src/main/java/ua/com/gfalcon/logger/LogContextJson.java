/*
 * MIT License
 *
 * Copyright (c) 2018 NIX Solutions Ltd.
 * Copyright (c) 2021 Oleksii V. KHALIKOV, PE.
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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import ua.com.gfalcon.logger.common.MapUtils;
import ua.com.gfalcon.logger.parameters.loggabletype.exception.LoggerException;

/**
 * JSON log context.
 */
public class LogContextJson implements LogContext<Long, String> {
    private static final String CONTEXT_ID_KEY = "key";
    private static final String CONTEXT_KEY = "ctx";
    private static final String MESSAGE_KEY = "message";

    @Override
    public String get(Long context) {
        return get(StringUtils.EMPTY, context);
    }

    @Override
    public String get(String message, Long context) {
        return get(message, context, Collections.emptyMap());
    }

    @Override
    public String get(String message, Long context, Map<String, Object> params) {
        Map<String, Object> logInfo = asMap(message);
        Map<String, Object> contextInfo = getContextInfo(params, context);
        logInfo.put(CONTEXT_KEY, contextInfo);

        return createJsonLogMessage(logInfo);
    }

    @Override
    public String get(String message, Map<String, Object> params) {
        return get(message, null, params);
    }

    @Override
    public Map<String, Object> shrinkParamsAsField(Map<String, Object> contextParams, String fieldName) {
        String fieldValue;
        try {
            fieldValue = MapUtils.convertMapToJson(contextParams);
        } catch (JsonProcessingException e) {
            throw new LoggerException("Can't convert context map to json.");
        }

        Map<String, String> nestedMap = new LinkedHashMap<>();
        nestedMap.put(fieldName, fieldValue);

        return Collections.unmodifiableMap(nestedMap);
    }

    private Map<String, Object> addInfo(Map<String, Object> quoteInfo, Long context) {
        Map<String, Object> infoMap = asMap(context);
        Map<String, Object> newInfo = Optional.ofNullable(quoteInfo)
                .map(LinkedHashMap::new)
                .orElseGet(LinkedHashMap::new);

        infoMap.putAll(newInfo);
        return infoMap;
    }

    private Map<String, Object> asMap(String message) {
        Map<String, Object> messageMap = new LinkedHashMap<>();

        if (StringUtils.isBlank(message)) {
            return messageMap;
        }

        messageMap.put(MESSAGE_KEY, message);
        return messageMap;
    }

    private Map<String, Object> asMap(Long context) {
        Map<String, Object> info = new LinkedHashMap<>();

        if (context == null || context == 0L) {
            return info;
        }

        info.put(CONTEXT_ID_KEY, String.valueOf(context));
        return info;
    }

    private String createJsonLogMessage(Map<String, Object> logMap) {
        try {
            return MapUtils.convertMapToJson(logMap);
        } catch (JsonProcessingException e) {
            throw new LoggerException("Can't convert context map to json.");
        }
    }

    private Map<String, Object> getContextInfo(Map<String, Object> params, Long context) {
        return addInfo(params, context);
    }
}
