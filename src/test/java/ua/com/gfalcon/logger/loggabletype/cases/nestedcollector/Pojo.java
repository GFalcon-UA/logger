package ua.com.gfalcon.logger.loggabletype.cases.nestedcollector;

import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.loggabletype.cases.BasePojo;

@LoggableType
public class Pojo implements BasePojo
{
  @LoggableType.Property
  public String field1 = "POJO_A3_FIELD_1";

  @LoggableType.Property
  public NestedPojo pojoB3 = new NestedPojo();
}
