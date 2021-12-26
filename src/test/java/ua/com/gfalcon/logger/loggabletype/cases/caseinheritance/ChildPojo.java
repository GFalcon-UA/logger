package ua.com.gfalcon.logger.loggabletype.cases.caseinheritance;

import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.loggabletype.cases.BasePojo;

@LoggableType(ignoreParents = false)
public class ChildPojo extends ParentPojo implements BasePojo
{
  @LoggableType.Property
  public String field1 = "POJO_A9_FIELD_1";
}
