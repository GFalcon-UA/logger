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

import org.slf4j.Logger;

/**
 * Pretty loggable.
 */
public interface PrettyLoggable<T> {
    Logger getCurrentLogger();

    LogContext<T, String> getLogContext();

    default void logDebug(String message, T context) {
        getCurrentLogger().debug(getLogContext().get(message, context));
    }

    default void logDebug(String message, Map<String, Object> customContext) {
        getCurrentLogger().debug(getLogContext().get(message, customContext));
    }

    default void logDebug(String message, T context, Map<String, Object> customContext) {
        getCurrentLogger().debug(getLogContext().get(message, context, customContext));
    }

    default void logError(String message, T context, Exception e) {
        getCurrentLogger().error(getLogContext().get(message, context), e);
    }

    default void logError(String message, Map<String, Object> customContext, Exception e) {
        getCurrentLogger().error(getLogContext().get(message, customContext), e);
    }

    default void logError(String message, T context, Map<String, Object> customContext, Exception e) {
        getCurrentLogger().error(getLogContext().get(message, context, customContext), e);
    }
}
