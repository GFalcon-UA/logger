package ua.com.gfalcon.logger.advice.handler;

import static java.util.Collections.singletonMap;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import static ua.com.gfalcon.logger.LoggingConstants.DURATION;
import static ua.com.gfalcon.logger.LoggingConstants.TASK_NAME;
import static ua.com.gfalcon.logger.LoggingConstants.TIME_LOGGING_CONTEXT;
import static ua.com.gfalcon.logger.LoggingConstants.TIME_UNIT;
import ua.com.gfalcon.logger.PrettyLoggable;
import ua.com.gfalcon.logger.advice.handler.base.AbstractLogActionHandler;
import ua.com.gfalcon.logger.annotation.Log;
import ua.com.gfalcon.logger.common.WordUtils;

public class LogExectimeActionHandler extends AbstractLogActionHandler {
    public LogExectimeActionHandler(PrettyLoggable prettyLoggable) {
        super(prettyLoggable);
    }

    @Override
    public void perform(Map<String, Object> params) {
        Method method = (Method) params.get(METHOD_PARAM);
        if (!isApplicable(method, Log.ExecTime.class)) {
            return;
        }

        Long startTime = (Long) params.get(START_MOMENT_PARAM);
        Long endTime = (Long) params.get(FINISH_MOMENT_PARAM);
        TimeUnit timeUnit = method.getAnnotation(Log.ExecTime.class)
                .timeUnit();
        String taskName = getTaskNameIfPresentOrMethodName(method);

        Map<String, Object> durationContextInfo = getDurationAsContextInfo(taskName, timeUnit, startTime, endTime);

        prettyLoggable.logDebug("execution finished", durationContextInfo);
    }

    private long getDesiredDurationFromNanoseconds(long start, long end, TimeUnit timeUnit) {
        return timeUnit.convert(end - start, NANOSECONDS);
    }

    private Map<String, Object> getDurationAsContextInfo(String taskName, TimeUnit timeUnit, long beforeCall,
            long afterCall) {
        Map<String, Object> timeLoggingContext = new HashMap<>();

        timeLoggingContext.put(TIME_UNIT, timeUnit.name());
        timeLoggingContext.put(TASK_NAME, taskName);
        timeLoggingContext.put(DURATION, getDesiredDurationFromNanoseconds(beforeCall, afterCall, timeUnit));

        return singletonMap(TIME_LOGGING_CONTEXT, timeLoggingContext);
    }

    private String getTaskNameIfPresentOrMethodName(Method method) {
        Log.ExecTime annotation = method.getAnnotation(Log.ExecTime.class);

        if (isNotEmpty(annotation.taskName())) {
            return WordUtils.toCamelCase(annotation.taskName(), SPACE);
        }
        return method.getName();
    }
}
