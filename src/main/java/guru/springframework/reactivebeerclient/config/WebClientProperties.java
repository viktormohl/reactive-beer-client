package guru.springframework.reactivebeerclient.config;

/**
 * <br />
 * author: Viktor Mohl (viktor.mohl@gmail.com) <br />
 * create at: 01.06.2021 on 21:19
 */
public class WebClientProperties {
    public static final String BASE_URL = "http://api.springframework.guru";
    public static final String BEER = "api/v1/beer";
    public static final String BEER_BY_ID = "api/v1/beer/{beerId}";
    public static final String BEER_BY_UPC = "api/v1/beerUpc/{upc}";
}
