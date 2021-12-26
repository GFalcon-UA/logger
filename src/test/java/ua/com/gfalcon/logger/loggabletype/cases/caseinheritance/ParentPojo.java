package ua.com.gfalcon.logger.loggabletype.cases.caseinheritance;

import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.loggabletype.cases.BasePojo;

@LoggableType(ignoreParents = false)
public class ParentPojo extends GrandParentPojo implements BasePojo
{
  @LoggableType.Property
  public String field1b2 = "POJO_A9_BASE_2_FIELD_1b2";
}
