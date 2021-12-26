package ua.com.gfalcon.logger.loggabletype.cases.recursivefail;

import ua.com.gfalcon.logger.annotation.LoggableType;

@LoggableType
public class RecursiveLoopPojo2
{
  @LoggableType.Property
  public RecursiveLoopPojo1 pojoA15;
}
