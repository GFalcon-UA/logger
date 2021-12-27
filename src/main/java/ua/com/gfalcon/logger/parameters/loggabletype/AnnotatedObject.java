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

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

/**
 * Annotated object.
 *
 * @param <A> annotation
 */
public class AnnotatedObject<A extends Annotation> {

    private Object object;
    private A annotation;

    private AnnotatedObject(Object object, A annotation) {
        this.object = object;
        this.annotation = annotation;
    }

    public A getAnnotation() {
        return annotation;
    }

    public Object getObject() {
        return object;
    }

    public Class getObjectClass() {
        return object.getClass();
    }

    public boolean isAnnotated() {
        return annotation != null;
    }

    public static <A extends Annotation> AnnotatedObject<A> createWithAnnotation(Object object,
            Class<A> annotationClass) {
        return new AnnotatedObject<>(object, object.getClass()
                .getAnnotation(annotationClass));
    }

    public static <A extends Annotation> AnnotatedObject<A> createWithAnnotationMethod(Object object,
            Supplier<A> getAnnotationMethod) {
        return new AnnotatedObject<>(object, getAnnotationMethod.get());
    }
}
