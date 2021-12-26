package ua.com.gfalcon.logger.loggabletype.cases.nestedcollector;

import ua.com.gfalcon.logger.annotation.LoggableType;

@LoggableType
public class NestedPojo
{
  @LoggableType.Property
  public String field1 = "POJO_B3_FIELD_1";

  @LoggableType.Property
  public EvenMoreNestedPojo pojoC3 = new EvenMoreNestedPojo();
}
