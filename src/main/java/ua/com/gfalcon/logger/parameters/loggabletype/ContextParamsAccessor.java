package ua.com.gfalcon.logger.parameters.loggabletype;

import java.util.Map;

public interface ContextParamsAccessor {
    Map<String, Object> extractParams();
}
