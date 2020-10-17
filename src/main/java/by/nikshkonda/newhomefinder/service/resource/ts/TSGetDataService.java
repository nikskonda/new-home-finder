package by.nikshkonda.newhomefinder.service.resource.ts;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

import static by.nikshkonda.newhomefinder.service.resource.SettingsService.Properties.OUTERMOST_FLOOR;
import static by.nikshkonda.newhomefinder.service.resource.SettingsService.Properties.PRICE;
import static by.nikshkonda.newhomefinder.service.resource.SettingsService.Properties.ROOM_NUMBER;
import static by.nikshkonda.newhomefinder.service.resource.SettingsService.Properties.YEAR;
import static by.nikshkonda.newhomefinder.service.resource.SettingsService.props;

@Service
public class TSGetDataService {

    private static final String GET_APARTMENTS_PARAMS = "https://www.t-s.by/buy/flats/filter/rooms-is-%s/price-to-%s/?PAGEN_1=%d";

    public static long COUNT = 0;

    private final OkHttpClient client = new OkHttpClient();

    @Value("${parser.SHOW_URLS}")
    private Boolean SHOW_URLS;


    public Optional<String> getApartments(int page) {
        String url = String.format(GET_APARTMENTS_PARAMS, props.get(ROOM_NUMBER), props.get(PRICE), page);
        COUNT++;

        Request request = new Request.Builder()
                .url(url)
                .build();
        if (SHOW_URLS) System.out.println(request.url());


        Call call = client.newCall(request);

        String result = null;
        try {
            Response response = call.execute();
            result = response.body().string();
        } catch (Exception ex) {

        }
        return Optional.ofNullable(result);
    }
}
