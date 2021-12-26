package ua.com.gfalcon.logger.loggabletype.cases.unextractablefield;

import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.loggabletype.cases.BasePojo;

@LoggableType
public class PojoWithUnextractableField implements BasePojo
{
  @LoggableType.Property
  public UnextractablePojo foreignPojo = new UnextractablePojo();
}
