package ua.com.gfalcon.logger.integration;

import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;
import ua.com.gfalcon.logger.advice.pojo.Pojo;
import ua.com.gfalcon.logger.annotation.ContextParam;
import ua.com.gfalcon.logger.annotation.Log;

@Component
public class SampleService
{


  private void timeConsuming()
  {
    try
    {
      Thread.sleep(300);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace();
    }
  }

  //Entry

  @Log
  @Log.Entry
  public void method()
  {
  }

  @Log
  @Log.Entry
  public void method(@ContextParam String strParam)
  {

  }

  @Log
  @Log.Entry
  public void methodWithRenamedParam(@ContextParam("renamedStrParam") String strParam)
  {

  }

  @Log
  @Log.Entry
  public void methodWithMultipleParams(@ContextParam String strParam,
                                       @ContextParam Long longParam)
  {

  }

  @Log
  @Log.Entry
  public void methodWithComplexParam(@ContextParam Pojo complexParam)
  {

  }

  //Exec time

  @Log
  @Log.ExecTime
  public void methodWihExecTimeLogging()
  {
    timeConsuming();
  }

  @Log
  @Log.ExecTime(timeUnit = TimeUnit.MICROSECONDS)
  public void methodWithExectimeLoggingAndOtherTimeUnit()
  {

  }

  @Log
  @Log.ExecTime(taskName = "newTaskName")
  public void methodWithExectimeLoggingAndOtherTaskName()
  {

  }

  @Log
  @Log.ExecTime(taskName = "Human readable task name")
  public void methodWithExectimeLoggingAndHumanReadableTaskName()
  {

  }

  //Exit

  @Log
  @Log.Exit
  public String methodWithStrReturn()
  {
    return "RETURN_STR";
  }

  @Log
  @Log.Exit
  public Pojo methodWithPojoReturn()
  {
    return new Pojo();
  }

  @Log
  @Log.Exit
  public void methodWithVoidReturn()
  {
  }

  @Log
  @Log.Exit
  public void methodTerminatedWithException()
  {
    throw new RuntimeException();
  }
}

