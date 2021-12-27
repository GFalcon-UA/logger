/*
 * MIT License
 *
 * Copyright (c) 2018 NIX Solutions Ltd.
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

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import static ua.com.gfalcon.logger.LoggingConstants.SINGLE_PROPERTY;
import static ua.com.gfalcon.logger.parameters.loggabletype.util.AnnotatedTypeReflectionUtils.getClassesToExtract;
import static ua.com.gfalcon.logger.parameters.loggabletype.util.AnnotatedTypeReflectionUtils.getRenamedFieldNameOrDefault;
import static ua.com.gfalcon.logger.parameters.loggabletype.util.AnnotatedTypeReflectionUtils.getSupplierMethod;
import static ua.com.gfalcon.logger.parameters.loggabletype.util.AnnotatedTypeReflectionUtils.isRecursiveLoop;
import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.common.MapUtils;
import ua.com.gfalcon.logger.parameters.extractor.ContextParamExtractor;
import ua.com.gfalcon.logger.parameters.extractor.ContextParamExtractorFactory;
import ua.com.gfalcon.logger.parameters.loggabletype.AnnotatedObject;
import ua.com.gfalcon.logger.parameters.loggabletype.ContextParamsAccessor;
import ua.com.gfalcon.logger.parameters.loggabletype.ExtractionResolutionStrategy;
import ua.com.gfalcon.logger.parameters.loggabletype.LookupResult;
import ua.com.gfalcon.logger.parameters.loggabletype.exception.LookupConflictException;
import ua.com.gfalcon.logger.parameters.loggabletype.exception.RecursiveLookupException;
import ua.com.gfalcon.logger.parameters.loggabletype.exception.RepeatedFieldsException;
import ua.com.gfalcon.logger.parameters.loggabletype.exception.UnresolvedLookupException;

/**
 * Annotation reflection lookup utils.
 */
@Component
@SuppressWarnings("unchecked")
public class AnnotationReflectionLookupUtils implements AnnotationLookupConstants {

    @Autowired
    private ContextParamExtractorFactory contextParamExtractorFactory;

    private LookupResult accessorMethodLookup(AnnotatedObject<LoggableType> annotatedObject) {
        Object object = annotatedObject.getObject();
        if (object instanceof ContextParamsAccessor) {
            return LookupResult.createResolved(((ContextParamsAccessor) object)::extractParams);
        }
        return LookupResult.createUnresolved();
    }

    private LookupResult annotatedMethodLookup(AnnotatedObject<LoggableType> annotatedObject) {
        try {
            Object object = annotatedObject.getObject();
            Optional<Method> supplierMethod = getSupplierMethod(object);
            if (supplierMethod.isPresent()) {
                Map<String, Object> invocationResult = (Map<String, Object>) supplierMethod.get()
                        .invoke(object);
                return LookupResult.createResolved(() -> invocationResult);
            }
        } catch (Exception ex) {
            return LookupResult.createExceptional(() -> ex);
        }
        return LookupResult.createUnresolved();
    }

    private Map<String, Object> collectContextParamsForPlainFields(
            List<Pair<Field, AnnotatedObject<LoggableType>>> allFields) {
        List<Pair<Field, AnnotatedObject<LoggableType>>> plainFields = allFields.stream()
                .filter(IS_FIELD_COMPLEX.negate())
                .collect(toList());

        rejectErrorOnDuplicatingFields(plainFields);

        return plainFields.stream()
                .flatMap(this::toFieldNameValuePair)
                .collect(toMap(Entry::getKey, Entry::getValue));
    }

    private LookupResult collectorLookup(Map<Class, List<Class>> fieldsProcessedBefore,
            Pair<Field, AnnotatedObject<LoggableType>> fieldObjPair) {
        List<Pair<Field, AnnotatedObject<LoggableType>>> allFields = getAnnotatedFieldObjPairs(fieldObjPair.getRight());
        LookupResult eligibleFieldsContextParamLookup = getCompositeFieldsContextParamLookup(fieldsProcessedBefore,
                allFields);
        LookupResult notEligibleFieldsContextParamLookup = plainFieldsContextParamLookup(allFields);

        LookupResult errorLookup = LookupUtils.errorLookup(notEligibleFieldsContextParamLookup,
                eligibleFieldsContextParamLookup);

        if (errorLookup != null) {
            return errorLookup;
        }

        Map<String, Object> simpleContextParams = notEligibleFieldsContextParamLookup.executeForResult();
        Map<String, Object> complexContextParams = eligibleFieldsContextParamLookup.executeForResult();

        return LookupResult.createResolved(() -> MapUtils.mergeMaps(simpleContextParams, complexContextParams));
    }

    private LookupResult extractorLookup(AnnotatedObject<LoggableType> annotatedObject) {
        ContextParamExtractor extractor = contextParamExtractorFactory.getExtractorByClass(
                annotatedObject.getObjectClass());
        if (isNull(extractor)) {
            return LookupResult.createUnresolved();
        }

        return LookupResult.createResolved(extractor::extractParams, annotatedObject.getObject());
    }

    private List<Pair<Field, AnnotatedObject<LoggableType>>> getAnnotatedFieldObjPairs(
            AnnotatedObject<LoggableType> annotatedObject) {
        Function<Field, Pair<Field, AnnotatedObject<LoggableType>>> transformFn = FIELD_TO_FIELD_OBJ_CURRIED.apply(
                annotatedObject.getObject());
        List<Class> classes = getClassesToExtract(annotatedObject);
        return classes.stream()
                .map(Class::getDeclaredFields)
                .flatMap(Arrays::stream)
                .filter(field -> field.isAnnotationPresent(LoggableType.Property.class))
                .map(transformFn)
                .collect(toList());
    }

    private Map<Class, List<Class>> getClassFieldRelationMetadata(
            List<Pair<Field, AnnotatedObject<LoggableType>>> compositeFields) {
        return compositeFields.stream()
                .map(Pair::getLeft)
                .map(TO_PROCESSED_FIELDS_METADATA)
                .collect(groupingBy(Pair::getLeft, mapping(Pair::getRight, toList())));
    }

    //TODO CLEANUP/REFACTOR
    private LookupResult getCompositeFieldsContextParamLookup(Map<Class, List<Class>> fieldsProcessedBefore,
            List<Pair<Field, AnnotatedObject<LoggableType>>> allFields) {
        List<Pair<Field, AnnotatedObject<LoggableType>>> compositeFields = allFields.stream()
                .filter(IS_FIELD_COMPLEX)
                .collect(toList());

        rejectErrorOnDuplicatingFields(compositeFields);

        if (compositeFields.isEmpty()) {
            return DO_NOTHING_LOOKUP;
        }

        fieldsProcessedBefore.putAll(getClassFieldRelationMetadata(compositeFields));

        List<LookupResult> compositeFieldsLookups = new ArrayList<>();

        for (Pair<Field, AnnotatedObject<LoggableType>> fieldObjPair : compositeFields) {
            LookupResult lookupResultToCheck = strategyLookupForField(fieldsProcessedBefore, fieldObjPair);

            if (lookupResultToCheck.isExceptional()) {
                return lookupResultToCheck;
            }

            LookupResult adjustedToFieldResult = LookupResult.createResolved(
                    () -> ImmutableMap.of(getRenamedFieldNameOrDefault(fieldObjPair.getLeft()),
                            lookupResultToCheck.executeForResult()));
            compositeFieldsLookups.add(adjustedToFieldResult);
        }

        return LookupResult.lazy(() -> getMergedLookupResult(compositeFieldsLookups));
    }

    private LookupResult getMergedLookupResult(List<LookupResult> compositeFields) {
        return LookupResult.createResolved(() -> compositeFields.stream()
                .map(LookupResult::executeForResult)
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .collect(toMap(Entry::getKey, Entry::getValue)));
    }

    private Map<String, Object> mergeContextParamMaps(Map<String, Object> simpleContextParams,
            Map<String, Object> complexContextParams, Field complexField) {
        if (isNull(complexField)) {
            return MapUtils.mergeMaps(simpleContextParams, complexContextParams);
        }

        Map<String, Object> finalResult = ImmutableMap.<String, Object>builder()
                .putAll(simpleContextParams)
                .put(complexField.getName(), complexContextParams)
                .build();

        return finalResult;
    }

    private LookupResult objectCollectorLookup(Map<Class, List<Class>> fieldsProcessedBefore,
            Pair<Field, AnnotatedObject<LoggableType>> fieldObjPair) {
        AnnotatedObject<LoggableType> annotatedObject = fieldObjPair.getRight();

        return LookupUtils.resultingLookup(accessorMethodLookup(annotatedObject),
                annotatedMethodLookup(annotatedObject), collectorLookup(fieldsProcessedBefore, fieldObjPair));
    }

    private LookupResult plainFieldsContextParamLookup(List<Pair<Field, AnnotatedObject<LoggableType>>> allFields) {
        try {
            Map<String, Object> contextParamsForNotEligibleFields = collectContextParamsForPlainFields(allFields);
            return LookupResult.createResolved(() -> contextParamsForNotEligibleFields);
        } catch (Exception e) {
            return LookupResult.createExceptional(() -> e);
        }
    }

    private void rejectErrorOnDuplicatingFields(List<Pair<Field, AnnotatedObject<LoggableType>>> fields) {
        Multimap<String, String> fieldClassesCollision = MultimapBuilder.hashKeys()
                .arrayListValues()
                .build();

        fields.stream()
                .map(pair -> pair.getKey())
                .forEach(field -> fieldClassesCollision.put(getRenamedFieldNameOrDefault(field),
                        field.getDeclaringClass()
                                .getName()));

        Map<String, Collection<String>> repeatedFields = fieldClassesCollision.asMap()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue()
                        .size() > 1)
                .collect(toMap(Entry::getKey, Entry::getValue));

        if (repeatedFields.size() >= 1) {
            throw new RepeatedFieldsException(repeatedFields.toString());
        }

    }

    private LookupResult strategyLookupForField(Map<Class, List<Class>> fieldsProcessedBefore,
            Pair<Field, AnnotatedObject<LoggableType>> fieldObjPair) {
        if (isRecursiveLoop(fieldsProcessedBefore, fieldObjPair.getLeft())) {
            return THROW_EX_LOOKUP.apply(new RecursiveLookupException());
        }

        AnnotatedObject<LoggableType> annotatedObject = fieldObjPair.getRight();
        ExtractionResolutionStrategy strategy = Optional.ofNullable(annotatedObject)
                .map(AnnotatedObject::getAnnotation)
                .map(LoggableType::resolutionStrategy)
                .orElse(ExtractionResolutionStrategy.COLLECTOR_FIRST);

        LookupResult collectorLookup = LookupResult.lazy(
                () -> objectCollectorLookup(fieldsProcessedBefore, fieldObjPair));
        LookupResult extractorLookup = LookupResult.lazy(() -> extractorLookup(annotatedObject));

        if (strategy == ExtractionResolutionStrategy.COLLECTOR_FIRST) {
            return LookupUtils.resultingLookup(collectorLookup, extractorLookup, DO_NOTHING_LOOKUP);
        } else if (strategy == ExtractionResolutionStrategy.EXTRACTOR_FIRST) {
            return LookupUtils.resultingLookup(extractorLookup, collectorLookup, DO_NOTHING_LOOKUP);
        } else if (strategy == ExtractionResolutionStrategy.RAISE_EX_ON_CONFLICT) {
            return LookupUtils.resultingLookup(
                    LookupUtils.conflictingLookup(THROW_EX_LOOKUP.apply(new LookupConflictException()), collectorLookup,
                            extractorLookup));
        } else {
            return LookupUtils.resultingLookup(
                    LookupUtils.conflictingLookup(DO_NOTHING_LOOKUP, collectorLookup, extractorLookup));
        }
    }

    /**
     * Create lookup result.
     *
     * @param annotatedObject object for logging.
     * @return result.
     */
    public LookupResult strategyLookupForRootObj(AnnotatedObject<LoggableType> annotatedObject) {
        if (IS_TO_STRING_APPLICABLE_TO_CLASS.test(annotatedObject.getObjectClass())) {
            return LookupResult.createResolved(
                    () -> Collections.singletonMap(SINGLE_PROPERTY, annotatedObject.getObject()));
        }

        return LookupResult.lazy(() -> strategyLookupForField(new HashMap<>(), Pair.of(null, annotatedObject)));
    }

    private Stream<Entry<String, Object>> toFieldNameValuePair(
            Pair<Field, AnnotatedObject<LoggableType>> fieldObjectPair) {
        Field field = fieldObjectPair.getLeft();
        Object value = fieldObjectPair.getRight()
                .getObject();
        String fieldName = getRenamedFieldNameOrDefault(field);

        if (IS_TO_STRING_APPLICABLE_TO_CLASS.test(field.getType())) {
            return Stream.of(Pair.of(fieldName, value));
        }

        LookupResult lookupResult = extractorLookup(fieldObjectPair.getRight());
        if (lookupResult.isResolved()) {
            return lookupResult.executeForResult()
                    .entrySet()
                    .stream();
        }

        throw new UnresolvedLookupException(format(FIELD_NON_EXTRACTABLE_EXCEPTION_MESSAGE, fieldName));
    }

    private Pair<LookupResult, AnnotatedObject<LoggableType>> toLookupObjFromCompositeFieldObjPair(
            Map<Class, List<Class>> fieldsProcessedBefore, Pair<Field, AnnotatedObject<LoggableType>> fieldObjPair) {
        return Pair.of(strategyLookupForField(fieldsProcessedBefore, fieldObjPair), fieldObjPair.getRight());
    }

}