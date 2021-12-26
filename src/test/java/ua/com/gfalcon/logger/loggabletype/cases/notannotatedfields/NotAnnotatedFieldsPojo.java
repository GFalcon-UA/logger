package ua.com.gfalcon.logger.loggabletype.cases.notannotatedfields;

import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.loggabletype.cases.BasePojo;
import com.fasterxml.jackson.annotation.JsonIgnore;

@LoggableType
public class NotAnnotatedFieldsPojo implements BasePojo
{
  @JsonIgnore
  public String field1 = "field1";

  @JsonIgnore
  public String field2 = "field2";
}
