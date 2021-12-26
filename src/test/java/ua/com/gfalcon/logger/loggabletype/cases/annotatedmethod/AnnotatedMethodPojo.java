package ua.com.gfalcon.logger.loggabletype.cases.annotatedmethod;

import java.util.Map;
import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.loggabletype.cases.BasePojo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;

@LoggableType
public class AnnotatedMethodPojo implements BasePojo
{
  public String field1 = "POJO_A5_FIELD_1";

  @JsonIgnore
  @LoggableType.ExtractionMethod
  public Map<String, Object> getLogParams()
  {
    return ImmutableMap.of("field1", field1);
  }
}
