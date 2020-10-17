package by.nikshkonda.newhomefinder.service.resource.realt;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static by.nikshkonda.newhomefinder.service.resource.SettingsService.Properties.MAX_YEAR;
import static by.nikshkonda.newhomefinder.service.resource.SettingsService.Properties.OUTERMOST_FLOOR;
import static by.nikshkonda.newhomefinder.service.resource.SettingsService.Properties.PRICE;
import static by.nikshkonda.newhomefinder.service.resource.SettingsService.Properties.ROOM_NUMBER;
import static by.nikshkonda.newhomefinder.service.resource.SettingsService.Properties.YEAR;
import static by.nikshkonda.newhomefinder.service.resource.SettingsService.props;

@Service
public class RealtGetDataService {

    private static final String GET_APARTMENTS_PARAMS = "&view=0&page=%d";

    public static long COUNT = 0;

    private final OkHttpClient client = new OkHttpClient();

    @Value("${parser.SHOW_URLS}")
    private Boolean SHOW_URLS;

    public Optional<String> getApartment(String url) {
        String result = null;
        try {
            Thread.sleep(3000);
            COUNT++;

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            if (SHOW_URLS) System.out.println(request.url());

            Call call = client.newCall(request);

            Response response = call.execute();
            result = response.body().string();

        } catch (Exception e) {

        }

        return Optional.ofNullable(result);
    }

    public Optional<String> getApartments(int page, String search) {
        String url = search.concat(String.format(GET_APARTMENTS_PARAMS, page));
        COUNT++;
        if (SHOW_URLS) System.out.println(COUNT + ") " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);

        String result = null;
        try {
            Response response = call.execute();
            result = response.body().string();
        } catch (Exception ex) {

        }
        return Optional.ofNullable(result);
    }

    public Optional<String> getSearch() {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "tx_uedbflat_pi2[DATA][state_region_id][e]=5&tx_uedbflat_pi2[DATA][town_id][e]=5102&" +
                    "tx_uedbflat_pi2[DATA][rooms][e][1]=" + props.get(ROOM_NUMBER) + "&" +
                    "tx_uedbflat_pi2[DATA][building_year][ge]=" + props.get(YEAR) + "&" +
                    "tx_uedbflat_pi2[DATA][building_year][le]=" + props.get(MAX_YEAR) + "&" +
                    "tx_uedbflat_pi2[DATA][storey][ne]=" + (props.get(OUTERMOST_FLOOR).equals("false") ? "1" : "") + "&" +
                    "tx_uedbflat_pi2[DATA][storey][fne]=" + (props.get(OUTERMOST_FLOOR).equals("false") ? "storeys" : "") + "&" +
                    "tx_uedbflat_pi2[DATA][price][le]=" + (Double.valueOf(props.get(PRICE)) / 1000) + "&" +
                    "tx_uedbflat_pi2[rec_per_page]=100&" +
                    "tx_uedbflat_pi2[asc_desc][0]=0&" +
                    "tx_uedbflat_pi2[asc_desc][1]=0");
            Request request = new Request.Builder()
                    .url("https://realt.by/sale/flats/")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            Response response = client.newCall(request).execute();
            return Optional.of(response.body().string());
        } catch (Exception ex) {
            System.out.println(ex);
            return Optional.empty();
        }

    }
}
