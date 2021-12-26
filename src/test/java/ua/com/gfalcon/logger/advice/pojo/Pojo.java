package ua.com.gfalcon.logger.advice.pojo;

import static ua.com.gfalcon.logger.advice.LoggingResultHelper.PARAM_LONG;
import static ua.com.gfalcon.logger.advice.LoggingResultHelper.PARAM_STR;
import ua.com.gfalcon.logger.annotation.LoggableType;

@LoggableType
public class Pojo
{
  @LoggableType.Property
  public String strParam = PARAM_STR;
  @LoggableType.Property
  public Long longParam = PARAM_LONG;
  @LoggableType.Property
  public EnumType enumParam = EnumType.ENUM_PARAM;
}
