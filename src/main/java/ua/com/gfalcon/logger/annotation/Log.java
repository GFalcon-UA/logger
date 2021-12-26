package ua.com.gfalcon.logger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Annotation intended to be a generic one for logging advice:
 * 1) Log entry into the method
 * 2) Log exit from the method(with exec time logging or not)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Log {
    @Retention(RetentionPolicy.RUNTIME)
    @interface Entry {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Exit {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface ExecTime {
        String taskName() default "";

        TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
    }
}
