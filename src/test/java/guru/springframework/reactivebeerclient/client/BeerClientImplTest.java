package guru.springframework.reactivebeerclient.client;

import guru.springframework.reactivebeerclient.config.WebClientConfig;
import guru.springframework.reactivebeerclient.model.BeerDto;
import guru.springframework.reactivebeerclient.model.BeerPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BeerClientImplTest {

    BeerClientImpl beerClient;

    @BeforeEach
    void setUp() { // schnellere ausführung, falls die Initialisierung nicht über Spring abläuft
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
                .beerStyle("PILSNER")
                .upc("25450")
                .price(new BigDecimal("24.99"))
                .build();

        final Mono<ResponseEntity<Void>> responseEntityMono = beerClient.createBeer(beerDto);
        final ResponseEntity<Void> responseEntity = responseEntityMono.block();

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void updateBeer() {
        final Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(1, 1, null, null, null);
        final BeerPagedList pagedList = beerPagedListMono.block();
        assertThat(pagedList).isNotNull();
        assertThat(pagedList.getContent().size()).isEqualTo(1);

        final BeerDto firstBeer = pagedList.getContent().get(0);

        BeerDto updatedBeer = BeerDto.builder()
                .beerName("Franziskaner Weissbier")
                .beerStyle(firstBeer.getBeerStyle())
                .upc(firstBeer.getUpc())
                .price(firstBeer.getPrice())
                .build();

        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.updateBeer(firstBeer.getId(), updatedBeer);
        ResponseEntity<Void> updatedBeerMono = responseEntityMono.block();

        assertThat(updatedBeerMono).isNotNull();
        assertThat(updatedBeerMono.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteBeerById() {
        final Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(1, 1, null, null, null);
        final BeerPagedList pagedList = beerPagedListMono.block();
        assertThat(pagedList).isNotNull();
        assertThat(pagedList.getContent().size()).isEqualTo(1);

        final BeerDto firstBeer = pagedList.getContent().get(0);

        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeerById(firstBeer.getId());
        ResponseEntity<Void> responseEntity = responseEntityMono.block();

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }

    // Exception Handling
    @Test
    void deleteBeerByIdNotFoundExpectException() {
        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeerById(UUID.randomUUID());

        assertThrows(WebClientResponseException.class, () ->{
            ResponseEntity<Void> responseEntity = responseEntityMono.block();
            // following will be not executed
            assertThat(responseEntity).isNotNull();
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        });
    }

    @Test
    void deleteBeerByIdNotFoundHandleException() {
        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeerById(UUID.randomUUID());

        ResponseEntity<Void> responseEntity = responseEntityMono.onErrorResume(throwable -> {
            if (throwable instanceof WebClientResponseException) {
                WebClientResponseException wcre = (WebClientResponseException) throwable;
                return Mono.just(ResponseEntity.status(wcre.getStatusCode()).build());
            }else{
                throw new RuntimeException(throwable);
            }
        }).block();

        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    // Functional Style Example
    @Test
    void getBeerByIdShowInventoryTrue() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers( //
                null, null, null, null,null);

        BeerPagedList pagedList = beerPagedListMono.block();

        assertThat(pagedList).isNotNull();
        UUID beerId = pagedList.getContent().get(0).getId();

        Mono<BeerDto> beerDtoMono = beerClient.getBeerById(beerId, true);

        BeerDto beerDto = beerDtoMono.block();

        assertThat(beerDto).isNotNull();
        assertThat(beerDto.getId()).isEqualTo(beerId);
        assertThat(beerDto.getQuantityOnHand()).isNotNull();
    }

    @Test
    void functionalTestGetBeerById() throws InterruptedException {
        AtomicReference<String> beerName = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        beerClient.listBeers(null, null, null, null, null)
                .map(beerPagedList -> beerPagedList.getContent().get(0).getId())
                .map(beerId -> beerClient.getBeerById(beerId, false))
                .flatMap(mono -> mono)
                .subscribe(beerDto -> {
                    beerName.set(beerDto.getBeerName()); // set value
                    countDownLatch.countDown(); // and notify
                });

        countDownLatch.await(); // await value (signaled by countDown())
        assertThat(beerName.get()).isEqualTo("Mango Bobs");
    }


}