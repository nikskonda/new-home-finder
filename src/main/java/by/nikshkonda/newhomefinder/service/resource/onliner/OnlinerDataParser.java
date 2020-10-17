package by.nikshkonda.newhomefinder.service.resource.onliner;

import by.nikshkonda.newhomefinder.entity.Apartment;
import by.nikshkonda.newhomefinder.service.resource.SettingsService;
import by.nikshkonda.newhomefinder.service.util.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class OnlinerDataParser {

    @Autowired
    private OnlinerGetDataService getDataService;

    @Autowired
    private JsonParser jsonParser;


    public List<Apartment> fillApartments() {
        List<Apartment> apartments = new ArrayList<>();
        Map<SettingsService.Properties, String> props = SettingsService.props;
        int page = 1;
        int totalPages = 1;
        while (page <= totalPages) {

            Optional<String> optional = getDataService.getApartments(page, props);
            if (optional.isPresent()) {
                Map<String, Object> map = jsonParser.toMap(optional.get());
                Map<String, Object> pageMap = (Map<String, Object>) map.get("page");
                totalPages = (Integer) pageMap.get("last");
                if (map.containsKey("apartments")) {
                    List<Map<String, Object>> aprts = (List<Map<String, Object>>) map.get("apartments");
                    for (Map<String, Object> aprt : aprts) {
                        try {
                            Apartment apartment = new Apartment();
                            apartment.setResource(Apartment.Resource.ONLINER);
                            apartment.setResourceId(((Integer) aprt.get("id")).longValue());

                            Map<String, Object> inner = (Map<String, Object>) aprt.get("location");
                            apartment.setAddress(inner.get("address").toString());
                            apartment.setUserAddress(inner.get("user_address").toString());
                            apartment.setLatitude((Double) inner.get("latitude"));
                            apartment.setLongitude((Double) inner.get("longitude"));

                            inner = (Map<String, Object>) aprt.get("price");
                            inner = (Map<String, Object>) inner.get("converted");
                            inner = (Map<String, Object>) inner.get("USD");
                            apartment.setPriceUSD(inner.get("amount") != null ? Double.valueOf(inner.get("amount").toString()) : null);

                            apartment.setNumberOfRoom(aprt.get("number_of_rooms") != null ? (Integer) aprt.get("number_of_rooms") : null);
                            apartment.setFloor(aprt.get("floor") != null ? (Integer) aprt.get("floor") : null);
                            apartment.setNumberOfFloor(aprt.get("number_of_floors") != null ? (Integer) aprt.get("number_of_floors") : null);

                            inner = (Map<String, Object>) aprt.get("area");
                            apartment.setTotalArea(inner.get("total") != null ? Float.valueOf(inner.get("total").toString()) : null);
                            apartment.setLivingArea(inner.get("living") != null ? Float.valueOf(inner.get("living").toString()) : null);
                            apartment.setKitchenArea(inner.get("kitchen") != null ? Float.valueOf(inner.get("kitchen").toString()) : null);

                            inner = (Map<String, Object>) aprt.get("seller");
                            apartment.setSellerType(inner.get("type") != null ? inner.get("type").toString() : null);

                            apartment.setCreated(LocalDateTime.parse(aprt.get("created_at").toString().substring(0, 19)));
                            apartment.setUpdated(LocalDateTime.parse(aprt.get("last_time_up").toString().substring(0, 19)));
                            apartment.setUrl(aprt.get("url").toString());

                            apartments.add(apartment);
                        } catch (Exception ex) {
                            System.out.println("Convertation Exception");
                            System.out.println(ex);
                        }
                    }
                }
            }
            page++;
        }
        System.out.println("Parsed count:" + apartments.size());
        return apartments;
    }

}