package ua.com.gfalcon.logger.loggabletype.cases.renamedcomplexfield;

import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.loggabletype.cases.BasePojo;
import com.fasterxml.jackson.annotation.JsonProperty;

@LoggableType
public class ComplexFieldRenamedPojo implements BasePojo
{
  @JsonProperty("pojob16")
  @LoggableType.Property(name = "pojob16")
  public ComplexPojo field1 = new ComplexPojo();
}
