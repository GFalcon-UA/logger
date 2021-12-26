package ua.com.gfalcon.logger.parameters.loggabletype.util;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.parameters.loggabletype.AnnotatedObject;

/**
 * Util class for processing annotated objects.
 */
public class AnnotatedTypeReflectionUtils {
    public static List<Class> getClassesHierarchy(Class clazz) {
        List<Class> classList = new ArrayList<>();
        classList.add(clazz);
        Class superclass = clazz.getSuperclass();
        classList.add(superclass);
        while (superclass != Object.class) {
            clazz = superclass;
            superclass = clazz.getSuperclass();
            if (!superclass.isInterface() && superclass.isAnnotationPresent(LoggableType.class)) {
                classList.add(superclass);
            }
        }
        Collections.reverse(classList);
        return classList;
    }

    public static List<Class> getClassesToExtract(AnnotatedObject<LoggableType> annotatedObject) {
        LoggableType annotation = annotatedObject.getAnnotation();
        if (isNull(annotation)) {
            return Collections.emptyList();
        }


        Class objectClass = annotatedObject.getObjectClass();
        return (annotation.ignoreParents()) ? Collections.singletonList(objectClass) : getClassesHierarchy(objectClass);
    }

    public static String getRenamedFieldNameOrDefault(Field field) {
        LoggableType.Property annotation = field.getAnnotation(LoggableType.Property.class);
        if (nonNull(annotation) && isNotEmpty(annotation.name())) {
            return annotation.name();
        }
        return field.getName();
    }

    public static Optional<Method> getSupplierMethod(Object object) {
        List<Method> methods = Stream.of(object.getClass()
                        .getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(LoggableType.ExtractionMethod.class))
                .collect(toList());
        if (methods.size() > 1) {
            throw new IllegalStateException(
                    "There can't be more than one method annotated @LoggableType.extractionMethod");
        }


        return methods.stream()
                .findFirst();
    }

    public static boolean isRecursiveLoop(Map<Class, List<Class>> fieldsProcessedBefore, Field currentField) {
        if (isNull(currentField)) {
            return false;
        }

        Optional<List<Class>> classes = Optional.ofNullable(fieldsProcessedBefore.get(currentField.getType()));
        if (classes.isPresent()) {
            return classes.map(clazzes -> clazzes.contains(currentField.getDeclaringClass()))
                    .get();
        }

        return false;
    }
}
