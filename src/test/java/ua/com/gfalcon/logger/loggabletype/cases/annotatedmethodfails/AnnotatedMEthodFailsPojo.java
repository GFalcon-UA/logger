package ua.com.gfalcon.logger.loggabletype.cases.annotatedmethodfails;

import java.util.Map;
import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.loggabletype.cases.BasePojo;
import com.fasterxml.jackson.annotation.JsonIgnore;

@LoggableType
public class AnnotatedMEthodFailsPojo implements BasePojo
{
  public String field1 = "POJO_A10_FIELD_1";

  @JsonIgnore
  @LoggableType.ExtractionMethod
  public Map<String, Object> getLogParams()
  {
    throw new RuntimeException();
  }
}
