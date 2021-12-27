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

package ua.com.gfalcon.logger.parameters.loggabletype;

import static java.util.Objects.isNull;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static ua.com.gfalcon.logger.parameters.loggabletype.LookupResult.LookupType.EXCEPTIONAL;
import static ua.com.gfalcon.logger.parameters.loggabletype.LookupResult.LookupType.LAZY;
import static ua.com.gfalcon.logger.parameters.loggabletype.LookupResult.LookupType.RESOLVED;
import static ua.com.gfalcon.logger.parameters.loggabletype.LookupResult.LookupType.UNRESOLVED;
import ua.com.gfalcon.logger.parameters.loggabletype.exception.UnresolvedLookupException;

public class LookupResult {

    private ResultAccessor resultAccessor;
    private LookupType lookupType;
    private Supplier<LookupResult> wrappedLookupResultSupplier;

    private LookupResult(Supplier<LookupResult> wrappedLookupResultSupplier) {
        this.wrappedLookupResultSupplier = wrappedLookupResultSupplier;
        this.lookupType = LAZY;
    }

    private LookupResult(ResultAccessor resultAccessor) {
        this(resultAccessor, LAZY);
    }

    private LookupResult(ResultAccessor resultAccessor, LookupType lookupType) {
        this.resultAccessor = resultAccessor;
        this.lookupType = lookupType;
    }

    public LookupType getLookupType() {
        return lookupType;
    }

    public boolean isExceptional() {
        return isCertainLookupType(EXCEPTIONAL);
    }

    public boolean isLazy() {
        return this.getLookupType()
                .equals(LAZY);
    }

    public boolean isResolved() {
        return isCertainLookupType(RESOLVED);
    }

    public boolean isUnresolved() {
        return isCertainLookupType(UNRESOLVED);
    }

    public static LookupResult createExceptional(Supplier<Exception> exceptionSupplier) {

        return new LookupResult(ResultAccessor.from(() -> {
            throw new RuntimeException(exceptionSupplier.get());
        }), EXCEPTIONAL);
    }

    public static LookupResult createResolved(Function<Object, Map<String, Object>> extractionFunction, Object object) {
        return new LookupResult(ResultAccessor.from(extractionFunction, object), RESOLVED);
    }

    public static LookupResult createResolved(Supplier<Map<String, Object>> extractionSupplier) {
        return new LookupResult(ResultAccessor.from(extractionSupplier), RESOLVED);
    }

    public static LookupResult createUnresolved() {
        return new LookupResult(ResultAccessor.from(() -> {
            throw new UnresolvedLookupException();
        }), UNRESOLVED);
    }

    public static LookupResult lazy(Supplier<LookupResult> lookupResultSupplier) {
        return new LookupResult(lookupResultSupplier);
    }

    /**
     * @return
     * @throws UnresolvedLookupException if lookup Is unresolved
     * @throws RuntimeException          for all user defined exceptions
     */
    public Map<String, Object> executeForResult() {
        LookupResult finalLookup = unWrapLazyLookup();
        return finalLookup.resultAccessor.accessResult();
    }

    public boolean isCertainLookupType(LookupType lookupType) {
        unWrapLazyLookup();
        return this.lookupType.equals(lookupType);
    }

    private LookupResult unWrapLazyLookup() {
        LookupResult finalLookup = this;
        while (finalLookup.lookupType.equals(LAZY)) {
            finalLookup = finalLookup.wrappedLookupResultSupplier.get();
        }

        this.lookupType = finalLookup.getLookupType();
        this.resultAccessor = finalLookup.resultAccessor;

        return finalLookup;
    }

    public enum LookupType {
        EXCEPTIONAL,
        RESOLVED,
        UNRESOLVED,
        LAZY
    }

    private static class ResultAccessor {
        private Function<Object, Map<String, Object>> extractionFunction;
        private Object object;

        private Supplier<Map<String, Object>> extractionSupplier;

        private ResultAccessor(Function<Object, Map<String, Object>> extractionFunction, Object object) {
            this.extractionFunction = extractionFunction;
            this.object = object;
        }

        private ResultAccessor(Supplier<Map<String, Object>> extractionSupplier) {
            this.extractionSupplier = extractionSupplier;
        }

        public static ResultAccessor from(Function<Object, Map<String, Object>> extractionFunction, Object object) {
            return new ResultAccessor(extractionFunction, object);
        }

        public static ResultAccessor from(Supplier<Map<String, Object>> extractionSupplier) {
            return new ResultAccessor(extractionSupplier);
        }

        public Map<String, Object> accessResult() {
            return isNull(extractionSupplier) ? extractionFunction.apply(object) : extractionSupplier.get();
        }
    }
}
