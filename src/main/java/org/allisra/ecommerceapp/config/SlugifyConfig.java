package org.allisra.ecommerceapp.config;

import com.github.slugify.Slugify;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlugifyConfig {

    @Bean
    public Slugify slugify() {
        return Slugify.builder()
                .lowerCase(true)
                .build();
    }
}