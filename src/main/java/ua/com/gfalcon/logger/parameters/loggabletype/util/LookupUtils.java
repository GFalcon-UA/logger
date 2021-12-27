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

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import java.util.Arrays;
import java.util.List;

import static ua.com.gfalcon.logger.parameters.loggabletype.LookupResult.LookupType.EXCEPTIONAL;
import static ua.com.gfalcon.logger.parameters.loggabletype.LookupResult.LookupType.RESOLVED;
import ua.com.gfalcon.logger.parameters.loggabletype.LookupResult;

public class LookupUtils {

    public static LookupResult conflictingLookup(LookupResult onConflict, LookupResult... results) {
        List<LookupResult> lookupResults = Arrays.stream(results)
                .filter(LookupResult::isResolved)
                .collect(toList());

        if (lookupResults.size() > 1) {
            return onConflict;
        }

        return lookupResults.get(0);
    }

    public static LookupResult errorLookup(LookupResult... lookupOrder) {
        return firstSpecificLookup(EXCEPTIONAL, lookupOrder);
    }

    public static LookupResult firstSpecificLookup(LookupResult.LookupType lookupType, LookupResult... lookupOrder) {
        if (lookupOrder.length == 1) {
            return lookupOrder[0];
        }
        return Arrays.stream(lookupOrder)
                .filter(lookupResult -> lookupResult.isCertainLookupType(lookupType))
                .findFirst()
                .orElse(null);
    }

    public static LookupResult resultingLookup(LookupResult... lookupOrder) {
        LookupResult errorLookup = errorLookup(lookupOrder);
        if (nonNull(errorLookup)) {
            return errorLookup;
        }

        return firstSpecificLookup(RESOLVED, lookupOrder);
    }
}