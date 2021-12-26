package ua.com.gfalcon.logger.loggabletype.cases.enumtypefield;

import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.loggabletype.cases.BasePojo;

@LoggableType
public class PojoWithEnumField implements BasePojo
{
  @LoggableType.Property
  public EnumType foreignPojo = EnumType.ALPHA;
}
