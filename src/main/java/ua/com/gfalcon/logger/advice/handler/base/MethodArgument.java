/*
 * MIT License
 *
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

package ua.com.gfalcon.logger.advice.handler.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

/**
 * Argument of method.
 */
public class MethodArgument {
    private final String name;
    private final Parameter parameter;
    private final Object value;

    private MethodArgument() {
        this("", null, null);
    }

    /**
     * Create instance.
     */
    public MethodArgument(String name, Parameter parameter, Object value) {
        this.name = name;
        this.parameter = parameter;
        this.value = value;
    }

    /**
     * Get name of argument.
     *
     * @return if parameter has own name - return this one else predefined names
     */
    public String getName() {
        if (parameter.isNamePresent()) {
            return parameter.getName();
        } else {
            return name;
        }
    }

    public Object getValue() {
        return value;
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        return parameter.getAnnotation(annotationClass);
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return parameter.isAnnotationPresent(annotationClass);
    }
}
