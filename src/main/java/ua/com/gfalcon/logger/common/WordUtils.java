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

package ua.com.gfalcon.logger.common;

import java.text.BreakIterator;

import static org.apache.commons.text.WordUtils.capitalizeFully;
import static org.apache.commons.text.WordUtils.uncapitalize;
import org.apache.commons.lang3.StringUtils;

/**
 * Text utils.
 */
public class WordUtils {

    public static final String EMPTY = "";

    private WordUtils() {
    }

    /**
     * Converts {@code sourceString} to a camelCase string using {@code delimiters}.
     * Examples:
     * <ul>
     * <li>toCamelCase("Load renewal"," ") - loadRenewal</li>
     * <li>toCamelCase("Load renewal! Now"," !") - loadRenewalNow</li>
     * <li>toCamelCase("Load ReNEwaL"," ") - loadRenewal</li>
     * <li>toCamelCase("Loadrenewal","") - loadrenewal </li>
     * </ul>
     *
     * @param sourceString string
     * @param delimiters   delimiters in a single string
     */
    public static String toCamelCase(String sourceString, String delimiters) {
        String result = capitalizeFully(sourceString, delimiters.toCharArray());
        for (String delimiter : delimiters.split(EMPTY)) {
            result = result.replaceAll(delimiter, EMPTY);
        }
        return uncapitalize(result);
    }

    /**
     * Truncate string on closest  word boundary.
     *
     * <pre>
     *   WordUtils.truncateWithWordBoundary(null, *) = ""
     *   WordUtils.truncateWithWordBoundary(*, 0) = ""
     *   WordUtils.truncateWithWordBoundary(*, -1) = ""
     *   WordUtils.truncateWithWordBoundary("abc", 5) = "abc"
     *   WordUtils.truncateWithWordBoundary("abc dfe", 5) = "abc"
     *   WordUtils.truncateWithWordBoundary("abc,:;dfc", 5) = "abc
     * </pre>
     *
     * @param string    - the String to be truncated, may be null
     * @param maxLength - max length of truncated string
     * @return same string if string length less then max length or truncated string
     */
    public static String truncateWithWordBoundary(String string, int maxLength) {
        if (StringUtils.isBlank(string) || maxLength <= 0) {
            return EMPTY;
        }

        if (string.length() < maxLength) {
            return string;
        }

        BreakIterator breakIterator = BreakIterator.getWordInstance();
        breakIterator.setText(string);

        int currentWordStart = breakIterator.preceding(maxLength);

        return string.substring(0, breakIterator.following(currentWordStart - 2));
    }
}
