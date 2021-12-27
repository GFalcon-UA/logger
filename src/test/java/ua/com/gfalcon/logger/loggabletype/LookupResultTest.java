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

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.collect.ImmutableMap;

import ua.com.gfalcon.logger.parameters.loggabletype.LookupResult;
import ua.com.gfalcon.logger.parameters.loggabletype.exception.UnresolvedLookupException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LookupResultTest {
    private static final Object OBJECT = new Object();

    private static final Map<String, Object> RESULT = ImmutableMap.of("key1", ImmutableMap.of("key1_1", "key1_val1"),
            "key2", ImmutableMap.of("key2_1", "key2_val1"));

    @ParameterizedTest(name = "Should {0}")
    @MethodSource("argsForShouldBeCorrectType")
    public void shouldBeCorrectType(String caseName, LookupResult lookupResult,
            LookupResult.LookupType lookupTypeExpected) {
        Assertions.assertEquals(lookupTypeExpected, lookupResult.getLookupType());

    }

    private Stream<Arguments> argsForShouldBeCorrectType() {
        return Stream.of(Arguments.of("type be RESOLVED", createLookupResult(LookupResult.LookupType.RESOLVED),
                        LookupResult.LookupType.RESOLVED),
                Arguments.of("type be UNRESOLVED", createLookupResult(LookupResult.LookupType.UNRESOLVED),
                        LookupResult.LookupType.UNRESOLVED),
                Arguments.of("type be EXCEPTIONAL", createLookupResult(LookupResult.LookupType.EXCEPTIONAL),
                        LookupResult.LookupType.EXCEPTIONAL), Arguments.of("type be LAZY",
                        createLazyLookupWrapping(createLookupResult(LookupResult.LookupType.RESOLVED)),
                        LookupResult.LookupType.LAZY));
    }

    @ParameterizedTest(name = "Should be {0} after unwrapping lazy")
    @MethodSource("argsForShouldBeCorrectTypeOfLazyAfterUnwrap")
    public void shouldBeCorrectTypeOfLazyAfterUnwrap(String caseName, LookupResult lookupResult,
            LookupResult.LookupType lookupTypeExpected) {
        Assertions.assertEquals(lookupResult.getLookupType(), LookupResult.LookupType.LAZY);
        Assertions.assertTrue(lookupResult.isCertainLookupType(lookupTypeExpected));
        Assertions.assertNotEquals(lookupResult.getLookupType(), LookupResult.LookupType.LAZY);
    }

    private Stream<Arguments> argsForShouldBeCorrectTypeOfLazyAfterUnwrap() {
        return Stream.of(Arguments.of("type be RESOLVED",
                createLazyLookupWrapping(createLookupResult(LookupResult.LookupType.RESOLVED)),
                LookupResult.LookupType.RESOLVED), Arguments.of("type be UNRESOLVED",
                createLazyLookupWrapping(createLookupResult(LookupResult.LookupType.UNRESOLVED)),
                LookupResult.LookupType.UNRESOLVED), Arguments.of("type be EXCEPTIONAL",
                createLazyLookupWrapping(createLookupResult(LookupResult.LookupType.EXCEPTIONAL)),
                LookupResult.LookupType.EXCEPTIONAL));
    }

    @ParameterizedTest(name = "Should throw {0}")
    @MethodSource("argsForShouldThrowExExceptionalAndUnresolvedLookup")
    public void shouldThrowExExceptionalAndUnresolvedLookup(Class<? extends Exception> expectedException,
            LookupResult lookupResult) {
        Executable executable = lookupResult::executeForResult;
        Assertions.assertThrows(expectedException, executable);
    }

    private Stream<Arguments> argsForShouldThrowExExceptionalAndUnresolvedLookup() {
        return Stream.of(Arguments.of(RuntimeException.class, createLookupResult(LookupResult.LookupType.EXCEPTIONAL)),
                Arguments.of(UnresolvedLookupException.class, createLookupResult(LookupResult.LookupType.UNRESOLVED)));
    }

    @ParameterizedTest(name = "Should {0}")
    @MethodSource("argsForShouldProduceCorrectResult")
    public void shouldProduceCorrectResult(String name, Map<String, Object> expectedResult, LookupResult lookupResult) {
        Assertions.assertEquals(expectedResult, lookupResult.executeForResult());
    }

    private Stream<Arguments> argsForShouldProduceCorrectResult() {
        return Stream.of(Arguments.of("return correct result for resolved lookup", RESULT,
                        createLookupResult(LookupResult.LookupType.RESOLVED)),
                Arguments.of("return correct result for lazy resolved lookup", RESULT,
                        createLazyLookupWrapping(createLookupResult(LookupResult.LookupType.RESOLVED))));
    }

    private LookupResult createLazyLookupWrapping(LookupResult lookupResultWrapped) {
        return LookupResult.lazy(() -> lookupResultWrapped);
    }

    private LookupResult createLookupResult(LookupResult.LookupType lookupType) {
        if (lookupType.equals(LookupResult.LookupType.RESOLVED)) {
            return LookupResult.createResolved(() -> RESULT);
        } else if (lookupType.equals(LookupResult.LookupType.UNRESOLVED)) {
            return LookupResult.createUnresolved();
        } else {
            return LookupResult.createExceptional(RuntimeException::new);
        }
    }
}