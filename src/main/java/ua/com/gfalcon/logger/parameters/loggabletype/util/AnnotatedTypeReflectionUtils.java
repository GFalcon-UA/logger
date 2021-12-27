/*
 * MIT License
 *
 * Copyright (c) 2018 NIX Solutions Ltd.
 * Copyright (c) 2021 Oleksii V. KHALIKOV, PE.
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

    private AnnotatedTypeReflectionUtils() {
    }

    /**
     * Get classes hierarchy.
     */
    public static List<Class<?>> getClassesHierarchy(Class<?> clazz) {
        List<Class<?>> classList = new ArrayList<>();
        classList.add(clazz);
        Class<?> superclass = clazz.getSuperclass();
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

    /**
     * Get classes to extract.
     */
    public static List<Class<?>> getClassesToExtract(AnnotatedObject<LoggableType> annotatedObject) {
        LoggableType annotation = annotatedObject.getAnnotation();
        if (isNull(annotation)) {
            return Collections.emptyList();
        }


        Class<?> objectClass = annotatedObject.getObjectClass();
        return (annotation.ignoreParents()) ? Collections.singletonList(objectClass) : getClassesHierarchy(objectClass);
    }

    /**
     * Get renamed field name or default.
     */
    public static String getRenamedFieldNameOrDefault(Field field) {
        LoggableType.Property annotation = field.getAnnotation(LoggableType.Property.class);
        if (nonNull(annotation) && isNotEmpty(annotation.name())) {
            return annotation.name();
        }
        return field.getName();
    }

    /**
     * Get supplier method.
     */
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

    /**
     * Is recursive loop.
     */
    public static boolean isRecursiveLoop(Map<Class<?>, List<Class<?>>> fieldsProcessedBefore, Field currentField) {
        return Optional.ofNullable(currentField)
                .map(f -> Optional.ofNullable(fieldsProcessedBefore.get(f.getType()))
                        .map(list -> list.contains(currentField.getDeclaringClass()))
                        .orElse(false))
                .orElse(false);
    }
}
