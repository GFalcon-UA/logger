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

/**
 * Constants.
 */
@SuppressWarnings("checkstyle:lineLength")
public interface AnnotationLookupConstants {
    List<Class> PRIMITIVES = ImmutableList.of(int.class, double.class, float.class, long.class, char.class,
            boolean.class, String.class);

    Predicate<Class> IS_ENUM_TYPE = Enum.class::isAssignableFrom;

    Predicate<Class> IS_PRIMITIVE_TYPE = ClassUtils::isPrimitiveOrWrapper;

    Predicate<Class> IS_TO_STRING_APPLICABLE_TO_CLASS = clazz -> IS_ENUM_TYPE.or(IS_PRIMITIVE_TYPE)
            .or(String.class::equals)
            .test(clazz);

    Function<Field, Pair<Class, Class>> TO_PROCESSED_FIELDS_METADATA = field -> Pair.of(field.getDeclaringClass(),
            field.getType());

    Predicate<Pair<Field, AnnotatedObject<LoggableType>>> IS_FIELD_COMPLEX = pair -> pair.getRight()
            .isAnnotated();

    Function<Object, Function<Field, Pair<Field, AnnotatedObject<LoggableType>>>> FIELD_TO_FIELD_OBJ_CURRIED =
            obj -> field -> Pair.of(
            field, AnnotatedObject.createWithAnnotation(getField(field, obj), LoggableType.class));

    Function<RuntimeException, LookupResult> THROW_EX_LOOKUP = ex -> LookupResult.createExceptional(() -> {
        throw ex;
    });

    LookupResult DO_NOTHING_LOOKUP = LookupResult.createResolved(() -> new HashMap<>());

    String FIELD_NON_EXTRACTABLE_EXCEPTION_MESSAGE = "Field %s cannot be extracted: "
            + "No extractor found and toString() is not applicable";
}
