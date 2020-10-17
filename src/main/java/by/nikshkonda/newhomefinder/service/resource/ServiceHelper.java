package by.nikshkonda.newhomefinder.service.resource;

import by.nikshkonda.newhomefinder.entity.Apartment;
import by.nikshkonda.newhomefinder.entity.Metro;
import by.nikshkonda.newhomefinder.service.util.JsonParser;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ServiceHelper {

    private static final String GET_COORDS = "http://nominatim.openstreetmap.org/search?format=json&addressdetails=0&q=";
    public static long COUNT = 0;
    private static Map<String, Pair<Double, Double>> metro;

    static {
        metro = new HashMap<>();
        metro.put("Малиновка", Pair.of(53.850179, 27.474868));
        metro.put("Петровщина", Pair.of(53.864714, 27.486087));
        metro.put("Михалова", Pair.of(53.876833, 27.496978));
        metro.put("Грушевка", Pair.of(53.886758, 27.514810));
        metro.put("Институт Культуры", Pair.of(53.885987, 27.538587));
        metro.put("Площадь Ленина", Pair.of(53.891999, 27.549090));
        metro.put("Октябрьская", Pair.of(53.902183, 27.562150));
        metro.put("Площадь Победы", Pair.of(53.902183, 27.562150));
        metro.put("Площадь Якуба Коласа", Pair.of(53.916277, 27.583804));
        metro.put("Академия Наук", Pair.of(53.921852, 27.598957));
        metro.put("Парк Челюскинцев", Pair.of(53.924146, 27.613325));
        metro.put("Московская", Pair.of(53.927593, 27.626409));
        metro.put("Восток", Pair.of(53.934539, 27.651518));
        metro.put("Борисовский Тракт", Pair.of(53.938566, 27.666053));
        metro.put("Уручье", Pair.of(53.945350, 27.687787));
        metro.put("Каменная горка", Pair.of(53.906949, 27.435855));
        metro.put("Кунцавщина", Pair.of(53.906264, 27.453939));
        metro.put("Спортивная", Pair.of(53.908555, 27.480867));
        metro.put("Пушкинская", Pair.of(53.909551, 27.495543));
        metro.put("Молодёжная", Pair.of(53.906227, 27.523748));
        metro.put("Фрунзенская", Pair.of(53.907014, 27.522622));
        metro.put("Нямига", Pair.of(53.905590, 27.554127));
        metro.put("Купаловская", Pair.of(53.900289, 27.562590));
        metro.put("Первомайская", Pair.of(53.893786, 27.570245));
        metro.put("Пралетарская", Pair.of(53.890279, 27.585323));
        metro.put("Тракторный завод", Pair.of(53.889888, 27.614716));
        metro.put("Партизанская", Pair.of(53.875662, 27.629259));
        metro.put("Автозаводская", Pair.of(53.868956, 27.648851));
        metro.put("Могилёвская", Pair.of(53.861914, 27.674375));
//        metro.put("", Pair.of());

    }

    private final RestTemplate restTemplate;

    private final JsonParser jsonParser;

    @Value("${parser.SHOW_URLS}")
    private Boolean SHOW_URLS;

    @Autowired
    public ServiceHelper(RestTemplateBuilder restTemplateBuilder, JsonParser jsonParser) {
        this.restTemplate = restTemplateBuilder.build();
        this.jsonParser = jsonParser;
    }

    public boolean isBadDistance(Apartment apartment, Double dist) {
        boolean result = true;
        if (apartment == null || dist == null) return false;
        if (apartment.getLatitude() == null || apartment.getLongitude() == null) {
            Pair<Double, Double> coords = getCoords(apartment.getAddress());
            if (coords != null) {
                apartment.setLatitude(coords.getLeft());
                apartment.setLongitude(coords.getRight());
            }
        }
        if (apartment.getLatitude() == null || apartment.getLongitude() == null) return false;
        for (Map.Entry<String, Pair<Double, Double>> entry : metro.entrySet()) {
            double d = distance(apartment.getLatitude(), apartment.getLongitude(), entry.getValue().getLeft(), entry.getValue().getRight(), "K");
            if (d <= 2) {
                apartment.addMetro(new Metro(entry.getKey(), d));
            }
            if (d<=dist){
                result = false;
            }
        }
        return result;
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }

    public Pair<Double, Double> getCoords(String address) {
        try {
//            address = address.replace(" ул.", " улица");
            address = address.replace(" ул.", "");
            address = address.replace(" просп.", " проспект");
            address = address.replace(", к. ", " к");
            address = address.replace("-", " к");
            Thread.sleep(3000);
            Optional<String> optional = sendRequest(GET_COORDS.concat(address), HttpMethod.GET, null);
            if (optional.isPresent()) {
                List<Map<String, Object>> list = jsonParser.toListOfMap(optional.get());
                if (list.size() >= 1) {
                    return Pair.of(Double.valueOf(list.get(0).get("lat").toString()), Double.valueOf(list.get(0).get("lon").toString()));
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }

    public Optional<String> sendRequest(String url, HttpMethod method, HttpHeaders headers) {
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
