package ua.com.gfalcon.logger.loggabletype.cases.recursivefail;

import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.loggabletype.cases.BasePojo;

@LoggableType
public class RecursiveLoopPojo1 implements BasePojo
{
  public RecursiveLoopPojo1()
  {
    pojoB15 = new RecursiveLoopPojo2();
    pojoB15.pojoA15 = this;
  }

  @LoggableType.Property
  public RecursiveLoopPojo2 pojoB15;
}
