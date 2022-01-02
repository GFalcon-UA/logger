/*
 * MIT License
 *
 * Copyright (c) 2018 NIX Solutions Ltd.
 * Copyright (c) 2021-2022 Oleksii V. KHALIKOV, PE.
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

package ua.com.gfalcon.logger.advice;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ua.com.gfalcon.logger.advice.handler.LogActionHandlerFactory;
import ua.com.gfalcon.logger.advice.handler.base.AbstractLogActionHandler;

/**
 * Logging advice.
 */
@Aspect
@Component
public class LoggingAdvice {

    private final LogActionHandlerFactory logActionHandlerFactory;

    @Autowired
    public LoggingAdvice(LogActionHandlerFactory logActionHandlerFactory) {
        this.logActionHandlerFactory = logActionHandlerFactory;
    }

    /**
     * Logging Advice.
     */
    @Around("@annotation(ua.com.gfalcon.logger.annotation.DoLog)")
    public Object loggingAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        final Object invocationResult;
        final Method method = getMethod(proceedingJoinPoint);
        final Object originalObject = proceedingJoinPoint.getTarget();
        final Object[] args = proceedingJoinPoint.getArgs();
        final Logger logger = getLoggerForObject(originalObject);
        final MethodSignature signature = getSignature(proceedingJoinPoint);

        long beforeCall = System.nanoTime();
        try {
            logActionHandlerFactory.createEntryHandler(logger)
                    .perform(createParamsForEntryLogging(signature, args));
            invocationResult = proceedingJoinPoint.proceed(args);
            logActionHandlerFactory.createExectimeHandler(logger)
                    .perform(createParamsForExectimeLogging(method, beforeCall, System.nanoTime()));
            logActionHandlerFactory.createExitHandler(logger)
                    .perform(createParamsForExitLogging(method, null, invocationResult));
        } catch (Exception exception) {
            logActionHandlerFactory.createExectimeHandler(logger)
                    .perform(createParamsForExectimeLogging(method, beforeCall, System.nanoTime()));
            logActionHandlerFactory.createExitHandler(logger)
                    .perform(createParamsForExitLogging(method, exception, null));
            throw exception;
        }

        return invocationResult;
    }

    private Map<String, Object> createParamsForEntryLogging(MethodSignature signature, Object[] methodArgs) {
        HashMap<String, Object> parameters = new HashMap<>();

        parameters.put(AbstractLogActionHandler.METHOD_SIGNATURE, signature);
        parameters.put(AbstractLogActionHandler.METHOD_PARAM, signature.getMethod());
        parameters.put(AbstractLogActionHandler.METHOD_ARGS_PARAM, methodArgs);

        return parameters;
    }

    private Map<String, Object> createParamsForExectimeLogging(Method method, long beforeCall, long afterCall) {
        HashMap<String, Object> parameters = new HashMap<>();

        parameters.put(AbstractLogActionHandler.METHOD_PARAM, method);
        parameters.put(AbstractLogActionHandler.START_MOMENT_PARAM, beforeCall);
        parameters.put(AbstractLogActionHandler.FINISH_MOMENT_PARAM, afterCall);

        return parameters;
    }

    private Map<String, Object> createParamsForExitLogging(Method method, Exception exception,
            Object invocationResult) {
        HashMap<String, Object> parameters = new HashMap<>();

        parameters.put(AbstractLogActionHandler.METHOD_PARAM, method);
        parameters.putIfAbsent(AbstractLogActionHandler.EXCEPTION_PARAM, exception);
        parameters.put(AbstractLogActionHandler.INVOCATION_RESULT_PARAM, invocationResult);

        return parameters;
    }

    private Logger getLoggerForObject(Object originalObject) {
        return LoggerFactory.getLogger(originalObject.getClass());
    }

    private Method getMethod(ProceedingJoinPoint joinPoint) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod();
    }

    private MethodSignature getSignature(ProceedingJoinPoint joinPoint) {
        return (MethodSignature) joinPoint.getSignature();
    }
}
