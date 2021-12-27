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

package ua.com.gfalcon.logger.parameters.extractor;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class ContextParamExtractorFactory {
    private ContextParamExtractor<Object> defaultContextParamExtractor;

    private Map<Class<?>, ContextParamExtractor<?>> contextParamExtractors;

    public ContextParamExtractorFactory(List<? extends ContextParamExtractor> contextParamExtractorsList) {
        contextParamExtractors = createParameterExtractorMap(contextParamExtractorsList);
    }

    public void setDefaultContextParamExtractor(ContextParamExtractor<Object> defaultContextParamExtractor) {
        this.defaultContextParamExtractor = defaultContextParamExtractor;
    }

    private Map<Class<?>, ContextParamExtractor<?>> createParameterExtractorMap(
            List<? extends ContextParamExtractor> contextParamExtractors) {
        return contextParamExtractors.stream()
                .flatMap(this::getParamExtractorsEntries)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    public <E> ContextParamExtractor<E> getExtractorByClass(Class<E> clazz) {
        return (ContextParamExtractor<E>) contextParamExtractors.get(clazz);
    }

    public <E> ContextParamExtractor<E> getExtractorByClassSafe(Class<E> clazz) {
        return Optional.ofNullable(contextParamExtractors.get(clazz))
                .orElse((ContextParamExtractor) defaultContextParamExtractor);
    }

    private Stream<Entry<Class<?>, ? extends ContextParamExtractor<?>>> getParamExtractorsEntries(
            ContextParamExtractor<?> contextParamExtractor) {
        return contextParamExtractor.getExtractableClasses()
                .stream()
                .map(clazz -> new SimpleEntry<Class<?>, ContextParamExtractor<?>>(clazz, contextParamExtractor));
    }
}
