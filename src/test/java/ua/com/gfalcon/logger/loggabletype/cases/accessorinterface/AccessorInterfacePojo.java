package ua.com.gfalcon.logger.loggabletype.cases.accessorinterface;

import java.util.Map;
import ua.com.gfalcon.logger.annotation.LoggableType;
import ua.com.gfalcon.logger.parameters.loggabletype.ContextParamsAccessor;
import ua.com.gfalcon.logger.loggabletype.cases.BasePojo;
import com.google.common.collect.ImmutableMap;

@LoggableType
public class AccessorInterfacePojo implements BasePojo, ContextParamsAccessor
{
  public String field1 = "POJO_A4_FIELD_1";

  @Override
  public Map<String, Object> extractParams()
  {
    return ImmutableMap.of("field1", field1);
  }
}
