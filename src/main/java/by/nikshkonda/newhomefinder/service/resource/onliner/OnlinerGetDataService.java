package by.nikshkonda.newhomefinder.service.resource.onliner;

import by.nikshkonda.newhomefinder.service.resource.SettingsService.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

import static by.nikshkonda.newhomefinder.service.resource.SettingsService.Properties.MAX_YEAR;
import static by.nikshkonda.newhomefinder.service.resource.SettingsService.Properties.OUTERMOST_FLOOR;
import static by.nikshkonda.newhomefinder.service.resource.SettingsService.Properties.PRICE;
import static by.nikshkonda.newhomefinder.service.resource.SettingsService.Properties.ROOM_NUMBER;
import static by.nikshkonda.newhomefinder.service.resource.SettingsService.Properties.YEAR;

@Service
public class OnlinerGetDataService {

    public static long COUNT = 0;

    private final RestTemplate restTemplate;

    @Value("${parser.SHOW_URLS}")
    private Boolean SHOW_URLS;

    @Autowired
    public OnlinerGetDataService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public Optional<String> getApartments(int page, Map<Properties, String> props) {
        return sendRequest("https://pk.api.onliner.by/search/apartments?" +
                "building_year[min]=" + props.get(YEAR) +
                "&building_year[max]=" + props.get(MAX_YEAR) +
                "&price[max]=" + Double.valueOf(props.get(PRICE)).intValue() +
                "&currency=usd" +
                "&number_of_rooms[]=" + props.get(ROOM_NUMBER) +
                "&outermost_floor=" + props.get(OUTERMOST_FLOOR) +
                "&bounds[lb][lat]=53.966403&bounds[lb][long]=27.394432&bounds[rt][lat]=53.827591&bounds[rt][long]=27.707303&v=0.8313416536488611" +
                "&page=" + page, HttpMethod.GET, null);
//        try{
//            OkHttpClient client = new OkHttpClient().newBuilder()
//                    .build();
//            Request request = new Request.Builder()
//                    .url("https://pk.api.onliner.by/search/apartments?" +
//                            "building_year%5Bmin%5D=" + props.get(YEAR) +
//                            "&price%5Bmax%5D=" + Double.valueOf(props.get(PRICE)).intValue() +
//                            "&currency=usd" +
//                            "&number_of_rooms%5B%5D=" + props.get(ROOM_NUMBER) +
//                            "&outermost_floor=" + props.get(OUTERMOST_FLOOR) +
//                            "&bounds%5Blb%5D%5Blat%5D=53.966403&bounds%5Blb%5D%5Blong%5D=27.394432&bounds%5Brt%5D%5Blat%5D=53.827591&bounds%5Brt%5D%5Blong%5D=27.707303&v=0.8313416536488611" +
//                            "&page=" + page)
//                    .method("GET", null)
//                    .build();
//            if (SHOW_URLS) System.out.println(request.url());
//            Response response = client.newCall(request).execute();
//            return Optional.ofNullable(response.body().string());
//        } catch (Exception ex) {
//            System.out.println(ex);
//        }
//        return Optional.empty();
    }

    private Optional<String> sendRequest(String url, HttpMethod method, HttpHeaders headers) {
        COUNT++;
        HttpEntity request = new HttpEntity(headers);
        if (SHOW_URLS) System.out.println(COUNT + ") " + url);

        ResponseEntity<String> response = this.restTemplate.exchange(url, method, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return Optional.ofNullable(response.getBody());
        } else {
            return Optional.empty();
        }
    }
}
