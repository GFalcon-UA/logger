package ua.com.gfalcon.logger.configuration;

import static java.util.Collections.EMPTY_MAP;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ua.com.gfalcon.logger.parameters.extractor.ContextParamExtractor;
import ua.com.gfalcon.logger.parameters.extractor.ContextParamExtractorFactory;

@Configuration
public class ContextExtractorFactoryConfiguration {
    private static final ContextParamExtractor<Object> DEFAULT_EXTRACTOR = new ContextParamExtractor<Object>() {
        @Override
        public List<Class<?>> getExtractableClasses() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<String, Object> extractParams(String name, Object parameter) {
            return EMPTY_MAP;
        }
    };

    @Bean
    public ContextParamExtractorFactory contextParamExtractorFactory(
            @Autowired List<? extends ContextParamExtractor> contextParamExtractorsList,
            @Autowired(required = false) @Qualifier("defaultExtractor") ContextParamExtractor defaultExtractor) {
        ContextParamExtractorFactory contextParamExtractorFactory = new ContextParamExtractorFactory(
                contextParamExtractorsList);

        if (Objects.isNull(defaultExtractor)) {
            contextParamExtractorFactory.setDefaultContextParamExtractor(DEFAULT_EXTRACTOR);
        } else {
            contextParamExtractorFactory.setDefaultContextParamExtractor(defaultExtractor);
        }

        return contextParamExtractorFactory;
    }
}
