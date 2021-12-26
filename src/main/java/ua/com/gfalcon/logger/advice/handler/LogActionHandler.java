package ua.com.gfalcon.logger.advice.handler;

import java.util.Map;

public interface LogActionHandler {
    void perform(Map<String, Object> params);
}
