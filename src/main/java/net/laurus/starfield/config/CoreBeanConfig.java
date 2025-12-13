package net.laurus.starfield.config;

import java.time.Clock;
import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class CoreBeanConfig {

    /*
     * =============== JSON / Object Mapping ===============
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {

        ObjectMapper mapper = JsonMapper
                .builder()
                // Java 8+ support
                .addModule(new Jdk8Module())
                .addModule(new JavaTimeModule())

                // Sensible defaults
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)

                // Optional but useful
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build();

        // Hook for future custom serializers/deserializers
        SimpleModule customModule = new SimpleModule("starfield-custom");
        mapper.registerModule(customModule);

        return mapper;
    }

    /*
     * =============== Async / Background Execution ===============
     */
    @Bean(name = "backgroundExecutor")
    public Executor backgroundExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("bg-");
        executor.initialize();
        return executor;
    }

    /*
     * ========================================================== System Clock
     * (testable!) ==========================================================
     */
    @Bean
    public Clock systemClock() {
        return Clock.systemUTC();
    }

}
