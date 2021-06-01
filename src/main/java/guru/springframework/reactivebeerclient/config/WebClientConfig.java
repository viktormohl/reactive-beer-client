package guru.springframework.reactivebeerclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * <br />
 * author: Viktor Mohl (viktor.mohl@gmail.com) <br />
 * create at: 01.06.2021 on 21:18
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(WebClientProperties.BASE_URL).build();
    }
}
