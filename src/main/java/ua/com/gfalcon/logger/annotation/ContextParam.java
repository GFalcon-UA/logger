package ua.com.gfalcon.logger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ua.com.gfalcon.logger.parameters.extractor.ContextParamExtractor;

/**
 * Use this annotation to mark the parameter you want to add into log message as context parameter.
 * For this an appropriate {@link ContextParamExtractor} will be used.
 * <br>
 * {@code value} is used as name for name of the field primarily. Otherwise, method
 * parameter name is used.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.TYPE_USE})
public @interface ContextParam {

    /**
     * Overriden name of argument.
     */
    String value() default "";
}
