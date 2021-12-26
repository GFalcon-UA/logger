package ua.com.gfalcon.logger.loggabletype.cases.repeatedfieldnames;

import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.loggabletype.cases.BasePojo;

@LoggableType(ignoreParents = false)
public class RepeatedFieldnamesPojo extends RepatedFieldsParentPojo implements BasePojo
{
  @LoggableType.Property
  public String field1 = "POJO_A11_FIELD_1";
}
