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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.springframework.util.ReflectionUtils.getField;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;

import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.parameters.loggabletype.AnnotatedObject;
import ua.com.gfalcon.logger.parameters.loggabletype.LookupResult;
import ua.com.gfalcon.logger.parameters.loggabletype.exception.LoggerException;

/**
 * Constants.
 */
@SuppressWarnings("checkstyle:lineLength")
public final class AnnotationLookupConstants {
    public static final List<Class<?>> PRIMITIVES = ImmutableList.of(int.class, double.class, float.class, long.class,
            char.class, boolean.class, String.class);

    public static final Predicate<Class<?>> IS_ENUM_TYPE = Enum.class::isAssignableFrom;

    public static final Predicate<Class<?>> IS_PRIMITIVE_TYPE = ClassUtils::isPrimitiveOrWrapper;

    public static final Predicate<Class<?>> IS_TO_STRING_APPLICABLE_TO_CLASS = clazz -> IS_ENUM_TYPE.or(
                    IS_PRIMITIVE_TYPE)
            .or(String.class::equals)
            .test(clazz);

    public static final Function<Field, Pair<Class<?>, Class<?>>> TO_PROCESSED_FIELDS_METADATA = field -> Pair.of(
            field.getDeclaringClass(), field.getType());

    public static final Predicate<Pair<Field, AnnotatedObject<LoggableType>>> IS_FIELD_COMPLEX = pair -> pair.getRight()
            .isAnnotated();

    public static final Function<Object, Function<Field, Pair<Field, AnnotatedObject<LoggableType>>>> FIELD_TO_FIELD_OBJ_CURRIED = obj -> field -> Pair.of(
            field, AnnotatedObject.createWithAnnotation(getField(field, obj), LoggableType.class));

    public static final Function<LoggerException, LookupResult> THROW_EX_LOOKUP = ex -> LookupResult.createExceptional(
            () -> {
                throw ex;
            });

    public static final LookupResult DO_NOTHING_LOOKUP = LookupResult.createResolved(HashMap::new);

    public static final String FIELD_NON_EXTRACTABLE_EXCEPTION_MESSAGE =
            "Field %s cannot be extracted: " + "No extractor found and toString() is not applicable";

    private AnnotationLookupConstants() {
    }
}
