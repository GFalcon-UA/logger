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

package ua.com.gfalcon.logger.advice;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static ua.com.gfalcon.logger.advice.LoggingResultHelper.PARAM_LONG;
import static ua.com.gfalcon.logger.advice.LoggingResultHelper.PARAM_STR;
import static ua.com.gfalcon.logger.advice.LoggingResultHelper.supposeThat;
import ua.com.gfalcon.logger.LogContext;
import ua.com.gfalcon.logger.LogContextJson;
import ua.com.gfalcon.logger.advice.pojo.Pojo;
import ua.com.gfalcon.logger.configuration.LoggingConfiguration;
import ua.com.gfalcon.logger.integration.SampleService;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Configuration
@ContextConfiguration(classes = {LoggingAdviceTest.class, LoggingConfiguration.class})
@ExtendWith(SpringExtension.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class LoggingAdviceTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final PrintStream stdout = System.out;
    private final PrintStream stderr = System.err;

    private ByteArrayOutputStream outStream;

    @Autowired
    SampleService sampleService;

    @BeforeAll
    public static void beforeAll() {
        LoggerFactory.getLogger(LoggingAdviceTest.class)
                .debug("{\"status\": \"TEST STARTED\"}");
    }

    @BeforeEach
    public void beforeTest() {
        outStream = new ByteArrayOutputStream();

        System.setOut(new PrintStream(outStream));
    }

    @AfterEach
    public void afterTest() {
        System.setOut(stdout);
        System.setErr(stderr);
    }

    @ParameterizedTest(name = "Should {0}")
    @MethodSource("argsForTestEntryLogging")
    void testEntryLogging(String name, Runnable methodRun, String fileName) throws Exception {
        //when
        methodRun.run();

        //then
        JsonNode expected = objectMapper.readTree(LoggingAdviceTest.class.getResourceAsStream("entry/" + fileName));

        supposeThat().givenSource(outStream)
                .fromCtx()
                .shouldBeEqualTo(expected);
    }

    private Stream<Arguments> argsForTestEntryLogging() {
        return Stream.of(Arguments.of("not log entry params if not specified", (Runnable) () -> sampleService.method(),
                        "emptyCtx.json"),
                Arguments.of("log single string param", (Runnable) () -> sampleService.method(PARAM_STR),
                        "strParam.json"), Arguments.of("log renamed string parameter",
                        (Runnable) () -> sampleService.methodWithRenamedParam(PARAM_STR), "renamedParam.json"),
                Arguments.of("log multiple parameters",
                        (Runnable) () -> sampleService.methodWithMultipleParams(PARAM_STR, PARAM_LONG),
                        "multipleParams.json"),
                Arguments.of("log complex parameter", (Runnable) () -> sampleService.methodWithComplexParam(new Pojo()),
                        "complexParam.json"));
    }

    @ParameterizedTest(name = "Should {0}")
    @MethodSource("argsForTestExectimeLogging")
    void testExectimeLogging(String name, Runnable methodRun, String fileName) throws Exception {
        //when
        methodRun.run();

        //then
        JsonNode expected = objectMapper.readTree(LoggingAdviceTest.class.getResourceAsStream("exectime/" + fileName));

        supposeThat().givenSource(outStream)
                .fromCtx()
                .propertyValueIgnored("timeLoggingContext.duration")
                .shouldBeEqualTo(expected);
    }

    private Stream<Arguments> argsForTestExectimeLogging() {
        return Stream.of(
                Arguments.of("log exec time if specified", (Runnable) () -> sampleService.methodWihExecTimeLogging(),
                        "simpleTimeLogging.json"), Arguments.of("log exec time with adjusted time unit",
                        (Runnable) () -> sampleService.methodWithExectimeLoggingAndOtherTimeUnit(),
                        "changedTimeUnit.json"), Arguments.of("log exec time with adjusted task name",
                        (Runnable) () -> sampleService.methodWithExectimeLoggingAndOtherTaskName(),
                        "adjustedTaskName.json"), Arguments.of("log exec time with human readable task name",
                        (Runnable) () -> sampleService.methodWithExectimeLoggingAndHumanReadableTaskName(),
                        "humanReadableTaskName.json"));
    }

    @Test
    void shouldLogTime() throws Exception {
        //when
        sampleService.methodWihExecTimeLogging();

        //then
        long duration = new ObjectMapper().readTree(outStream.toString())
                .get("context")
                .get("ctx")
                .get("timeLoggingContext")
                .get("duration")
                .longValue();


        Assertions.assertTrue(duration >= 300L);
    }

    @ParameterizedTest(name = "Should {0}")
    @MethodSource("argsForTestExitLogging")
    void testExitLogging(String name, Runnable methodRun, String fileName) throws Exception {
        //when
        methodRun.run();

        //then
        JsonNode expected = objectMapper.readTree(LoggingAdviceTest.class.getResourceAsStream("exit/" + fileName));

        supposeThat().givenSource(outStream)
                .fromCtx()
                .shouldBeEqualTo(expected);
    }

    private Stream<Arguments> argsForTestExitLogging() {
        return Stream.of(Arguments.of("log string return param", (Runnable) () -> sampleService.methodWithStrReturn(),
                        "strReturnParam.json"),
                Arguments.of("log pojo return param", (Runnable) () -> sampleService.methodWithPojoReturn(),
                        "pojoReturnParam.json"),
                Arguments.of("log void return param", (Runnable) () -> sampleService.methodWithVoidReturn(),
                        "voidReturn.json"));
    }

    @Test
    void shouldLogExceptionTerminatedMethod() throws Exception {
        //when
        try {
            sampleService.methodTerminatedWithException();
        } catch (Exception e) {

        }

        //then
        JsonNode actual = objectMapper.readTree(outStream.toString());

        Assertions.assertTrue(actual.get("exception")
                .asText()
                .length() > 0);
    }

    @Primary
    @Bean
    public LogContext<Long, String> logContextJson() {
        return new LogContextJson();
    }
}
