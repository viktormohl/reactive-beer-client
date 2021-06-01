package guru.springframework.reactivebeerclient.config;

import io.netty.handler.logging.LogLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

/**
 * <br />
 * author: Viktor Mohl (viktor.mohl@gmail.com) <br />
 * create at: 01.06.2021 on 21:18
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(WebClientProperties.BASE_URL)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .wiretap("reactor.netty.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)))
                .build();
    }
}
