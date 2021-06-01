package guru.springframework.reactivebeerclient.client;

import guru.springframework.reactivebeerclient.config.WebClientConfig;
import guru.springframework.reactivebeerclient.model.BeerDto;
import guru.springframework.reactivebeerclient.model.BeerPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BeerClientImplTest {

    BeerClientImpl beerClient;

    @BeforeEach
    void setUp() {
        beerClient = new BeerClientImpl(new WebClientConfig().webClient());
    }

    @Test
    void listBeers() {
        final Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null, null, null);
        final BeerPagedList pagedList = beerPagedListMono.block();
        assertThat(pagedList).isNotNull();
        assertThat(pagedList.getContent().size()).isGreaterThan(0);
    }

    @Test
    void listBeersPageSize10() {
        final Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(1, 10, null, null, null);
        final BeerPagedList pagedList = beerPagedListMono.block();
        assertThat(pagedList).isNotNull();
        assertThat(pagedList.getContent().size()).isEqualTo(10);
    }

    @Test
    void listBeersNoRecords() {
        final Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(10, 20, null, null, null);
        final BeerPagedList pagedList = beerPagedListMono.block();
        assertThat(pagedList).isNotNull();
        assertThat(pagedList.getContent().size()).isEqualTo(0);
    }

    @Test
    void createBeer() {
        BeerDto beerDto = new BeerDto();
        beerDto.setBeerName("Franziskaner Weissbier");
        beerDto.setBeerStyle("Weissbier");
        beerDto.setUpc("25450");
        beerDto.setQuantityOnHand(40);
        beerDto.setPrice(new BigDecimal("24,99"));

        final Mono<ResponseEntity> beer = beerClient.createBeer(beerDto);
        final ResponseEntity block = beer.block();
    }

    @Test
    void getBeerById() {
    }


    @Test
    void updateBeer() {
    }

    @Test
    void deleteBeerById() {
    }

    @Test
    void getBeerByUPC() {
    }
}