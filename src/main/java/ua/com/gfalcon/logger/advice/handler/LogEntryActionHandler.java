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
