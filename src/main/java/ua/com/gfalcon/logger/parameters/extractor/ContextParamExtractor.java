package ua.com.gfalcon.logger.parameters.extractor;

import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

import ua.com.gfalcon.logger.annotation.ContextParam;

/**
 * Basic interface for specific class parameter extractor.
 * <br>
 * ctx stands for context
 * <br>
 * Can extract parameters as Map in following ways:
 * <ul>
 * <li>As method parameter({@link Parameter}) and value:
 * {@link #extractParams(Parameter, Object)} <br>
 * If name is provided in an appropriate {@link ContextParam}
 * annotation it is used as name for a ctx param, otherwise - method param's name.
 * </li>
 * <li>As value itself: {@link #extractParams(Object)} - suitable for extracting multiple ctx parameters in a map
 * </li>
 * <li>As name and value: {@link #extractParams(String, Object)} - consider the previous option - here you can nest
 * your ctx parameters and give them a name </li>
 * </ul>
 * <br>
 * To include your custom extractor make it manageable by Spring.
 *
 * @param <E> class which will be extracted for context params
 */
public interface ContextParamExtractor<E> {
    /**
     * @return list of classes that can be maintained by this extractor
     */
    List<Class<?>> getExtractableClasses();

    default Map<String, Object> extractParams(final E parameter) {
        return extractParams(EMPTY, parameter);
    }

    Map<String, Object> extractParams(String name, final E parameter);

    default Map<String, Object> extractParams(Parameter parameter, final E parameterValue) {
        return extractParams(defaultIfBlank(parameter.getAnnotation(ContextParam.class)
                .value(), parameter.getName()), parameterValue);
    }
}
