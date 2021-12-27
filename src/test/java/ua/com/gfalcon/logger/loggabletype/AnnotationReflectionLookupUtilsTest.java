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

package ua.com.gfalcon.logger.loggabletype;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.configuration.ContextExtractorFactoryConfiguration;
import ua.com.gfalcon.logger.loggabletype.cases.BasePojo;
import ua.com.gfalcon.logger.loggabletype.cases.accessorinterface.AccessorInterfacePojo;
import ua.com.gfalcon.logger.loggabletype.cases.annotatedmethod.AnnotatedMethodPojo;
import ua.com.gfalcon.logger.loggabletype.cases.annotatedmethodfails.AnnotatedMEthodFailsPojo;
import ua.com.gfalcon.logger.loggabletype.cases.caseinheritance.ChildPojo;
import ua.com.gfalcon.logger.loggabletype.cases.conflictinglookup.ConflictingLookupPojo;
import ua.com.gfalcon.logger.loggabletype.cases.donothinglookup.DoNothingLookupPojo;
import ua.com.gfalcon.logger.loggabletype.cases.emptypojo.Empty;
import ua.com.gfalcon.logger.loggabletype.cases.enumtypefield.PojoWithEnumField;
import ua.com.gfalcon.logger.loggabletype.cases.multipleannotatedmethods.MultipleAnnotatedMethodsPojo;
import ua.com.gfalcon.logger.loggabletype.cases.nestedcollector.Pojo;
import ua.com.gfalcon.logger.loggabletype.cases.nestedextractor.PojoWithNestedPojo;
import ua.com.gfalcon.logger.loggabletype.cases.notannotatedfields.NotAnnotatedFieldsPojo;
import ua.com.gfalcon.logger.loggabletype.cases.recursivefail.RecursiveLoopPojo1;
import ua.com.gfalcon.logger.loggabletype.cases.renamedcomplexfield.ComplexFieldRenamedPojo;
import ua.com.gfalcon.logger.loggabletype.cases.renamedfield.RenamedFieldPojo;
import ua.com.gfalcon.logger.loggabletype.cases.repeatedfieldnames.RepeatedFieldnamesPojo;
import ua.com.gfalcon.logger.loggabletype.cases.simpleextractor.SimpleExtractorPojo;
import ua.com.gfalcon.logger.loggabletype.cases.unextractablefield.PojoWithUnextractableField;
import ua.com.gfalcon.logger.parameters.loggabletype.AnnotatedObject;
import ua.com.gfalcon.logger.parameters.loggabletype.LookupResult;
import ua.com.gfalcon.logger.parameters.loggabletype.exception.LookupConflictException;
import ua.com.gfalcon.logger.parameters.loggabletype.exception.RecursiveLookupException;
import ua.com.gfalcon.logger.parameters.loggabletype.exception.RepeatedFieldsException;
import ua.com.gfalcon.logger.parameters.loggabletype.exception.UnresolvedLookupException;
import ua.com.gfalcon.logger.parameters.loggabletype.util.AnnotationReflectionLookupUtils;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Configuration
@ContextConfiguration(classes = {AnnotationReflectionLookupUtilsTest.class, ContextExtractorFactoryConfiguration.class})
@ComponentScan("ua.com.gfalcon.logger")
@ExtendWith(SpringExtension.class)
public class AnnotationReflectionLookupUtilsTest {
    @Autowired
    private AnnotationReflectionLookupUtils reflectionLookupUtils;

    private ObjectMapper objectMapper = new ObjectMapper().configure(FAIL_ON_EMPTY_BEANS, false);

    private static final SimpleExtractorPojo POJO_A1 = new SimpleExtractorPojo();
    private static final PojoWithNestedPojo POJO_A2 = new PojoWithNestedPojo();
    private static final Pojo POJO_A3 = new Pojo();
    private static final AccessorInterfacePojo POJO_A4 = new AccessorInterfacePojo();
    private static final AnnotatedMethodPojo POJO_A5 = new AnnotatedMethodPojo();
    private static final Empty POJO_A6 = new Empty();
    private static final NotAnnotatedFieldsPojo POJO_A7 = new NotAnnotatedFieldsPojo();
    private static final RenamedFieldPojo POJO_A8 = new RenamedFieldPojo();
    private static final ChildPojo POJO_A9 = new ChildPojo();
    private static final PojoWithEnumField POJO_A14 = new PojoWithEnumField();
    private static final ComplexFieldRenamedPojo POJO_A16 = new ComplexFieldRenamedPojo();
    private static final DoNothingLookupPojo POJO_A17 = new DoNothingLookupPojo();

    private static final AnnotatedMEthodFailsPojo POJO_A10 = new AnnotatedMEthodFailsPojo();
    private static final RepeatedFieldnamesPojo POJO_A11 = new RepeatedFieldnamesPojo();
    private static final ConflictingLookupPojo POJO_A12 = new ConflictingLookupPojo();
    private static final PojoWithUnextractableField POJO_A13 = new PojoWithUnextractableField();
    private static final RecursiveLoopPojo1 POJO_A15 = new RecursiveLoopPojo1();
    private static final MultipleAnnotatedMethodsPojo POJO_A18 = new MultipleAnnotatedMethodsPojo();

    @ParameterizedTest(name = "Should {0}")
    @MethodSource("paramsForShouldProduceCorrectResult")
    public void shouldProduceCorrectResult(String name, BasePojo initial, JsonNode expected) throws Exception {
        //given
        AnnotatedObject<LoggableType> annotatedObject = AnnotatedObject.createWithAnnotation(initial,
                LoggableType.class);

        //when
        LookupResult lookupResult = reflectionLookupUtils.strategyLookupForRootObj(annotatedObject);
        JsonNode result = objectMapper.readTree(objectMapper.writeValueAsString(lookupResult.executeForResult()));
        //then
        Assertions.assertEquals(expected, result);
    }

    private Stream<Arguments> paramsForShouldProduceCorrectResult() throws Exception {
        return Stream.of(Arguments.of("perform extractor lookup", POJO_A1, prepareResult(POJO_A1)),
                Arguments.of("perform nested extractor lookup", POJO_A2, prepareResult(POJO_A2)),
                Arguments.of("perform even nested collector lookup", POJO_A3, prepareResult(POJO_A3)),
                Arguments.of("perform accessor method lookup", POJO_A4, prepareResult(POJO_A4)),
                Arguments.of("perform annotated method lookup", POJO_A5, prepareResult(POJO_A5)),
                Arguments.of("perform lookup on empty pojo", POJO_A6, prepareResult(POJO_A6)),
                Arguments.of("perform lookup on pojo without annotated fields", POJO_A7, prepareResult(POJO_A7)),
                Arguments.of("perform lookup on pojo with renamed field(s)", POJO_A8, prepareResult(POJO_A8)),
                Arguments.of("perform lookup in parent classes", POJO_A9, prepareResult(POJO_A9)),
                Arguments.of("should serialize enums", POJO_A14, prepareResult(POJO_A14)),
                Arguments.of("rename complex field", POJO_A16, prepareResult(POJO_A16)),
                Arguments.of("perform do nothing lookup", POJO_A17, prepareResult(POJO_A17))

        );
    }

    private JsonNode prepareResult(BasePojo basePojo) throws IOException {
        return objectMapper.readTree(objectMapper.writeValueAsString(basePojo));
    }

    @ParameterizedTest(name = "Should {0}")
    @MethodSource("paramsForShouldThrowException")
    public void shouldThrowException(String name, BasePojo initial, Class<? extends Exception> exceptionClass) {
        //given
        AnnotatedObject<LoggableType> annotatedObject = AnnotatedObject.createWithAnnotation(initial,
                LoggableType.class);

        //when
        LookupResult lookupResult = reflectionLookupUtils.strategyLookupForRootObj(annotatedObject);

        //then
        Assertions.assertTrue(lookupResult.isExceptional());
        try {
            lookupResult.executeForResult();
        } catch (RuntimeException e) {
            Assertions.assertEquals(exceptionClass, Optional.ofNullable(e.getCause())
                    .orElse(e)
                    .getClass());
            return;
        }
        Assertions.fail();
    }

    private Stream<Arguments> paramsForShouldThrowException() throws IOException {
        return Stream.of(
                Arguments.of("throw exception if annotated method fails", POJO_A10, InvocationTargetException.class),
                Arguments.of("throw exception if field name is repeated in parent class and child", POJO_A11,
                        RepeatedFieldsException.class),
                Arguments.of("throw exception on conflicting lookup", POJO_A12, LookupConflictException.class),
                Arguments.of("throw exception for unextractable field", POJO_A13, UnresolvedLookupException.class),
                Arguments.of("throw exception on recursive loop", POJO_A15, RecursiveLookupException.class),
                Arguments.of("throw exception if multiple annotated methods are present", POJO_A18,
                        IllegalStateException.class));
    }
}