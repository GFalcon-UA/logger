package ua.com.gfalcon.logger.loggabletype.cases.conflictinglookup;

import static ua.com.gfalcon.logger.parameters.loggabletype.ExtractionResolutionStrategy.RAISE_EX_ON_CONFLICT;
import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.loggabletype.cases.BasePojo;

@LoggableType(resolutionStrategy = RAISE_EX_ON_CONFLICT)
public class ConflictingLookupPojo implements BasePojo
{
  @LoggableType.Property
  public String field1 = "POJO_A12_FIELD_1";
}
