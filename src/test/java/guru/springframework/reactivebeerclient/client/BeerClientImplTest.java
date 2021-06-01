package guru.springframework.reactivebeerclient.client;

import guru.springframework.reactivebeerclient.config.WebClientConfig;
import guru.springframework.reactivebeerclient.model.BeerDto;
import guru.springframework.reactivebeerclient.model.BeerPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
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
    void getBeerById() {
        final Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(1, 1, null, null, null);
        final BeerPagedList pagedList = beerPagedListMono.block();
        assertThat(pagedList).isNotNull();
        assertThat(pagedList.getContent().size()).isEqualTo(1);
        final BeerDto beerDto = pagedList.getContent().get(0);

        final Mono<BeerDto> beerByIdMono = beerClient.getBeerById(beerDto.getId(), null);
        final BeerDto beer = beerByIdMono.block();
        assertThat(beer).isNotNull();
        assertEquals(beerDto, beer);
    }

    @Test
    void getBeerByUPC() {
        final Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(1, 1, null, null, null);
        final BeerPagedList pagedList = beerPagedListMono.block();
        assertThat(pagedList).isNotNull();
        assertThat(pagedList.getContent().size()).isEqualTo(1);
        final BeerDto beerDto = pagedList.getContent().get(0);

        final Mono<BeerDto> beerByIdMono = beerClient.getBeerByUPC(beerDto.getUpc());
        final BeerDto beer = beerByIdMono.block();
        assertThat(beer).isNotNull();
        assertEquals(beerDto, beer);
    }

    @Test
    void createBeer() {
        BeerDto beerDto = BeerDto.builder()
                .beerName("Franziskaner Weissbier")
                .beerStyle("Weissbier")
                .upc("25450")
                .quantityOnHand(40)
                .price(new BigDecimal("24.99"))
                .build();

        final Mono<ResponseEntity<Void>> responseEntityMono = beerClient.createBeer(beerDto);
        final ResponseEntity<Void> responseEntity = responseEntityMono.block();
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void updateBeer() {
    }

    @Test
    void deleteBeerById() {
    }


}