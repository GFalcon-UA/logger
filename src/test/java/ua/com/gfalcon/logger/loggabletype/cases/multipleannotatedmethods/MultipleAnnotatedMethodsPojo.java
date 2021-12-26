package ua.com.gfalcon.logger.loggabletype.cases.multipleannotatedmethods;

import java.util.Map;
import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.loggabletype.cases.BasePojo;
import com.google.common.collect.ImmutableMap;

@LoggableType
public class MultipleAnnotatedMethodsPojo implements BasePojo
{

  @LoggableType.ExtractionMethod
  public Map<String, Object> extract1()
  {
    return ImmutableMap.of();
  }

  @LoggableType.ExtractionMethod
  public Map<String, Object> extract2()
  {
    return ImmutableMap.of();
  }

}
