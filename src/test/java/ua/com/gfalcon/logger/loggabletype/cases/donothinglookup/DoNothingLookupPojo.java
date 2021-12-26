package ua.com.gfalcon.logger.loggabletype.cases.donothinglookup;

import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.parameters.loggabletype.ExtractionResolutionStrategy;
import ua.com.gfalcon.logger.loggabletype.cases.BasePojo;
import com.fasterxml.jackson.annotation.JsonIgnore;

@LoggableType(resolutionStrategy = ExtractionResolutionStrategy.DO_NOTHING)
public class DoNothingLookupPojo implements BasePojo
{
  @JsonIgnore
  @LoggableType.Property
  public String field1 = "POJO_A17_FIELD_1";
}
