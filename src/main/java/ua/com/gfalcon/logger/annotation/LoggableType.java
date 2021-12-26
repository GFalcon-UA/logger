package ua.com.gfalcon.logger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ua.com.gfalcon.logger.parameters.loggabletype.ExtractionResolutionStrategy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LoggableType {
    boolean ignoreParents() default true;

    ExtractionResolutionStrategy resolutionStrategy() default ExtractionResolutionStrategy.COLLECTOR_FIRST;

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD})
    @interface Property {
        String name() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface ExtractionMethod {

    }
}
