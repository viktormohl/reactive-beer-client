package guru.springframework.reactivebeerclient.client;

import guru.springframework.reactivebeerclient.config.WebClientProperties;
import guru.springframework.reactivebeerclient.model.BeerDto;
import guru.springframework.reactivebeerclient.model.BeerPagedList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

/**
 * <a href="https://sfg-beer-works.github.io/brewery-api/#tag/Beer-Service">API-Ref</a>
 * Created by jt on 3/13/21.
 */
@Service
@RequiredArgsConstructor
public class BeerClientImpl implements BeerClient {

    private final WebClient webClient;

    @Override
    public Mono<BeerPagedList> listBeers(Integer pageNumber, Integer pageSize, String beerName, String beerStyle, Boolean showInventoryOnHand) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(WebClientProperties.BEER)
                        .queryParamIfPresent("pageNumber", Optional.ofNullable(pageNumber))
                        .queryParamIfPresent("pageSize", Optional.ofNullable(pageSize))
                        .queryParamIfPresent("beerName", Optional.ofNullable(beerName))
                        .queryParamIfPresent("beerStyle", Optional.ofNullable(beerStyle))
                        .queryParamIfPresent("showInventoryOnHand", Optional.ofNullable(showInventoryOnHand))
                        .build()
                )
                .retrieve()
                .bodyToMono(BeerPagedList.class);
    }

    @Override
    public Mono<BeerDto> getBeerById(UUID id, Boolean showInventoryOnHand) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(WebClientProperties.BEER_BY_ID)
                        .queryParamIfPresent("showInventoryOnHand", Optional.ofNullable(showInventoryOnHand))
                        .build(id)
                )
                .retrieve()
                .bodyToMono(BeerDto.class);
    }

    @Override
    public Mono<BeerDto> getBeerByUPC(String upc) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(WebClientProperties.BEER_BY_UPC)
                        .build(upc)
                )
                .retrieve()
                .bodyToMono(BeerDto.class);
    }

    @Override
    public Mono<ResponseEntity<Void>> createBeer(BeerDto beerDto) {
        return webClient.post()
                .uri(WebClientProperties.BEER)
                .body(BodyInserters.fromValue(beerDto))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public Mono<ResponseEntity<Void>> updateBeer(UUID beerId, BeerDto beerDto) {
        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(WebClientProperties.BEER_BY_ID)
                        .build(beerId)
                )
                .bodyValue(beerDto)// shortcut for .body(BodyInserters.fromValue(beerDto))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteBeerById(UUID beerId) {
        return webClient.delete()
                .uri(uriBuilder -> uriBuilder.path(WebClientProperties.BEER_BY_ID).build(beerId))
                .retrieve()
                .toBodilessEntity();
    }

}
