package ua.com.gfalcon.logger.loggabletype.cases.renamedfield;

import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.loggabletype.cases.BasePojo;
import com.fasterxml.jackson.annotation.JsonProperty;

@LoggableType
public class RenamedFieldPojo implements BasePojo
{
  @JsonProperty("field1Renamed")
  @LoggableType.Property(name = "field1Renamed")
  public String field1 = "POJO_A8_FIELD_1_RENAMED";
}
