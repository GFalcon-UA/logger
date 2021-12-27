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

package ua.com.gfalcon.logger.integration;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import ua.com.gfalcon.logger.advice.pojo.Pojo;
import ua.com.gfalcon.logger.annotation.ContextParam;
import ua.com.gfalcon.logger.annotation.Log;

@Component
public class SampleService {


    private void timeConsuming() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Entry

    @Log
    @Log.Entry
    public void method() {
    }

    @Log
    @Log.Entry
    public void method(@ContextParam String strParam) {

    }

    @Log
    @Log.Entry
    public void methodWithRenamedParam(@ContextParam("renamedStrParam") String strParam) {

    }

    @Log
    @Log.Entry
    public void methodWithMultipleParams(@ContextParam String strParam, @ContextParam Long longParam) {

    }

    @Log
    @Log.Entry
    public void methodWithComplexParam(@ContextParam Pojo complexParam) {

    }

    //Exec time

    @Log
    @Log.ExecTime
    public void methodWihExecTimeLogging() {
        timeConsuming();
    }

    @Log
    @Log.ExecTime(timeUnit = TimeUnit.MICROSECONDS)
    public void methodWithExectimeLoggingAndOtherTimeUnit() {

    }

    @Log
    @Log.ExecTime(taskName = "newTaskName")
    public void methodWithExectimeLoggingAndOtherTaskName() {

    }

    @Log
    @Log.ExecTime(taskName = "Human readable task name")
    public void methodWithExectimeLoggingAndHumanReadableTaskName() {

    }

    //Exit

    @Log
    @Log.Exit
    public String methodWithStrReturn() {
        return "RETURN_STR";
    }

    @Log
    @Log.Exit
    public Pojo methodWithPojoReturn() {
        return new Pojo();
    }

    @Log
    @Log.Exit
    public void methodWithVoidReturn() {
    }

    @Log
    @Log.Exit
    public void methodTerminatedWithException() {
        throw new RuntimeException();
    }
}

