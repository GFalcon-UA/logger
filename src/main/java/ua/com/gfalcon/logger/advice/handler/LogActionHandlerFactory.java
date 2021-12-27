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

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ua.com.gfalcon.logger.LogContext;
import ua.com.gfalcon.logger.ObjectlessPrettyLoggable;
import ua.com.gfalcon.logger.PrettyLoggable;
import ua.com.gfalcon.logger.parameters.extractor.ContextParamExtractorFactory;
import ua.com.gfalcon.logger.parameters.loggabletype.util.AnnotationReflectionLookupUtils;

/**
 * Log action handler factory.
 */
@Component
public class LogActionHandlerFactory {
    private AnnotationReflectionLookupUtils reflectionLookupUtils;
    private ContextParamExtractorFactory contextParamExtractorFactory;
    private LogContext<Long, String> logContext;

    /**
     * Create instance.
     */
    @Autowired
    public LogActionHandlerFactory(AnnotationReflectionLookupUtils reflectionLookupUtils,
            ContextParamExtractorFactory contextParamExtractorFactory, LogContext<Long, String> logContext) {
        this.reflectionLookupUtils = reflectionLookupUtils;
        this.contextParamExtractorFactory = contextParamExtractorFactory;
        this.logContext = logContext;
    }

    public LogActionHandler createEntryHandler(Logger logger) {
        PrettyLoggable prettyLoggable = new ObjectlessPrettyLoggable(logger, logContext);
        return new LogEntryActionHandler(prettyLoggable, reflectionLookupUtils);
    }

    public LogActionHandler createExectimeHandler(Logger logger) {
        PrettyLoggable prettyLoggable = new ObjectlessPrettyLoggable(logger, logContext);
        return new LogExectimeActionHandler(prettyLoggable);
    }

    public LogActionHandler createExitHandler(Logger logger) {
        PrettyLoggable prettyLoggable = new ObjectlessPrettyLoggable(logger, logContext);
        return new LogExitActionHandler(prettyLoggable, reflectionLookupUtils);
    }
}
