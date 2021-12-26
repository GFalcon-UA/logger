package ua.com.gfalcon.logger.loggabletype.cases.simpleextractor;

import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.parameters.loggabletype.ExtractionResolutionStrategy;
import ua.com.gfalcon.logger.loggabletype.cases.BasePojo;

@LoggableType(resolutionStrategy = ExtractionResolutionStrategy.EXTRACTOR_FIRST)
public class SimpleExtractorPojo implements BasePojo
{
  public String field1 = "POJO_A1_FIELD_1";
}
