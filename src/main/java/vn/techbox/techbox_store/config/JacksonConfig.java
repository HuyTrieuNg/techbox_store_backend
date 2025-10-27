package vn.techbox.techbox_store.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.techbox.techbox_store.config.serializer.UTCLocalDateTimeDeserializer;
import vn.techbox.techbox_store.config.serializer.UTCLocalDateTimeSerializer;

import java.time.LocalDateTime;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.serializerByType(LocalDateTime.class, new UTCLocalDateTimeSerializer());
            builder.deserializerByType(LocalDateTime.class, new UTCLocalDateTimeDeserializer());

            builder.timeZone("UTC");

            builder.featuresToDisable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }
}
