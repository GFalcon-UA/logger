package ua.com.gfalcon.logger.loggabletype.cases.nestedextractor;

import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.parameters.loggabletype.ExtractionResolutionStrategy;

@LoggableType(resolutionStrategy = ExtractionResolutionStrategy.EXTRACTOR_FIRST)
public class NestedPojo
{
  @LoggableType.Property
  public String field1 = "POJO_B2_FIELD_1";
}
