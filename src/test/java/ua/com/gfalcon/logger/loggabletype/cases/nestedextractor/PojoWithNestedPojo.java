package ua.com.gfalcon.logger.loggabletype.cases.nestedextractor;

import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.loggabletype.cases.BasePojo;

@LoggableType
public class PojoWithNestedPojo implements BasePojo
{
  @LoggableType.Property
  public String field1 = "POJO_A2_FIELD_1";

  @LoggableType.Property
  public NestedPojo pojoB2 = new NestedPojo();
}
