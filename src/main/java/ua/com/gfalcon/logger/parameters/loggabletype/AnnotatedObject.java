package ua.com.gfalcon.logger.parameters.loggabletype;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

/**
 * Annotated object.
 *
 * @param <A> annotation
 */
public class AnnotatedObject<A extends Annotation> {

    private Object object;
    private A annotation;

    private AnnotatedObject(Object object, A annotation) {
        this.object = object;
        this.annotation = annotation;
    }

    public A getAnnotation() {
        return annotation;
    }

    public Object getObject() {
        return object;
    }

    public Class getObjectClass() {
        return object.getClass();
    }

    public boolean isAnnotated() {
        return annotation != null;
    }

    public static <A extends Annotation> AnnotatedObject<A> createWithAnnotation(Object object,
            Class<A> annotationClass) {
        return new AnnotatedObject<>(object, object.getClass()
                .getAnnotation(annotationClass));
    }

    public static <A extends Annotation> AnnotatedObject<A> createWithAnnotationMethod(Object object,
            Supplier<A> getAnnotationMethod) {
        return new AnnotatedObject<>(object, getAnnotationMethod.get());
    }
}
