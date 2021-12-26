package ua.com.gfalcon.logger.configuration;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ContextExtractorFactoryConfiguration.class)
@ComponentScan("com.nixsolutions.logging")
public class LoggingConfiguration {

}

