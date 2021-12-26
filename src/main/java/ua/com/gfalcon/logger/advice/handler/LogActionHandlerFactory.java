package ua.com.gfalcon.logger.advice.handler;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ua.com.gfalcon.logger.LogContext;
import ua.com.gfalcon.logger.ObjectlessPrettyLoggable;
import ua.com.gfalcon.logger.PrettyLoggable;
import ua.com.gfalcon.logger.parameters.extractor.ContextParamExtractorFactory;
import ua.com.gfalcon.logger.parameters.loggabletype.util.AnnotationReflectionLookupUtils;

@Component
public class LogActionHandlerFactory {
    private AnnotationReflectionLookupUtils reflectionLookupUtils;
    private ContextParamExtractorFactory contextParamExtractorFactory;
    private LogContext<Long, String> logContext;

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
