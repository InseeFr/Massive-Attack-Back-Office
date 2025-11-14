package fr.insee.sabianedata.ws;

import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

import fr.insee.sabianedata.ws.config.BearerAuthInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ConfigurationPropertiesScan
@Slf4j
public class AppWS {


    public static void main(String[] args) {
        SpringApplication.run(AppWS.class, args);
    }

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        final Environment env = event.getApplicationContext().getEnvironment();
        log.info("================================ Properties ================================");
        final MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();
        StreamSupport.stream(sources.spliterator(), false).filter(EnumerablePropertySource.class::isInstance)
                .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames()).flatMap(Arrays::stream).distinct()
                .filter(prop -> !(prop.contains("credentials") || prop.contains("password")))
                .filter(prop -> prop.startsWith("feature") || prop.startsWith("logging") || prop.startsWith("spring")
                        || prop.startsWith("application"))
                .sorted().forEach(prop -> log.info("{}: {}", prop, env.getProperty(prop)));
        log.info("===========================================================================");
        log.info("Available CPU : {}", Runtime.getRuntime().availableProcessors());
        log.info(String.format("Max memory : %.2f GB", Runtime.getRuntime().maxMemory() / 1e9d));
        log.info("===========================================================================");
    }

    @EventListener
    public void handleApplicationReady(ApplicationReadyEvent event) {
        log.info("=============== Massive Attack API has successfully started. ===============");

    }

    @Bean
    public RestTemplate restTemplate(BearerAuthInterceptor bearerAuthInterceptor) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(bearerAuthInterceptor);

        restTemplate.setMessageConverters(List.of(
                new MappingJackson2HttpMessageConverter()
        ));

        return restTemplate;
    }

}
