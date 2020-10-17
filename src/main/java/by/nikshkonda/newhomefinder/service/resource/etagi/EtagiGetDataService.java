package by.nikshkonda.newhomefinder.service.resource.etagi;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
public class EtagiGetDataService {

    public static long COUNT = 0;

    private final OkHttpClient client = new OkHttpClient();

    @Value("${parser.SHOW_URLS}")
    private Boolean SHOW_URLS;

    @Value("${parser.RUB}")
    private Double RUB;

    public Optional<String> getApartments() {
        Request request = new Request.Builder()
                .url("https://minsk.etagi.com/msearcher_ajax.php?price_max="+(Double.valueOf(props.get(PRICE))*RUB)+"&" +
                        "building_year_min=" + props.get(YEAR) + "&" +
                        "building_year_max=" + props.get(MAX_YEAR) + "&" +
                        "order=object_id%3D1604412%20desc%2Ccontract_type%3D%27exclusive%27%20desc%20nulls%20last%2C(visual%20is%20null)%2Cprof_photo%3D%27f%27%2Cdate_rise%20desc%2Cdate_update%20desc%2Cobject_id%20desc&" +
                        "limit=100&" +
                        "class=flats&" +
                        "city_id=1066&" +
                        "rooms[]=" + props.get(ROOM_NUMBER) + "&" +
                        "type[]=flat&" +
                        "floor=" + (props.get(OUTERMOST_FLOOR).equals("false") ? "!1" : "") + "&" +
                        "floors=" + (props.get(OUTERMOST_FLOOR).equals("false") ? "!floor" : "") + "&" +
                        "archiveEnable=1&" +
                        "getMapData=1&" +
                        "getObjects=1&" +
                        "action=modular_search&" +
                        "subAction=searcher").build();
        if (SHOW_URLS) System.out.println(request.url());

        Call call = client.newCall(request);

        String result = null;
        try {
            Response response = call.execute();
            result = response.body().string();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return Optional.ofNullable(result);
    }
}
