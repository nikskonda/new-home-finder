package by.nikshkonda.newhomefinder.service.resource.etagi;

import by.nikshkonda.newhomefinder.entity.Apartment;
import by.nikshkonda.newhomefinder.service.util.HtmlParser;
import by.nikshkonda.newhomefinder.service.util.JsonParser;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class EtagiDataParser {

    @Value("${parser.RUB}")
    private Double RUB;

    @Autowired
    private EtagiGetDataService getDataService;

    @Autowired
    private JsonParser jsonParser;

    public Map<Long, Apartment> fillApartments() {
        Map<Long, Apartment> apartments = new HashMap<>();
        Optional<String> optional = getDataService.getApartments();
        if (optional.isPresent()) {
            Map<String, Object> map = jsonParser.toMap(optional.get());
            Map<Long, Pair<Double, Double>> dist = new HashMap<>();
            if (map.containsKey("mapObjects")) {
                List<List<Object>> list = (List<List<Object>>)map.get("mapObjects");
                for (List<Object> item : list) {
                    if (item.size()!=4) continue;
                    dist.put(((Integer)item.get(0)).longValue(), Pair.of((Double) item.get(1), (Double)item.get(2)));
                }
            }
            if (map.containsKey("objects")) {
                List<Map<String, Object>> list = (List<Map<String, Object>>)map.get("objects");
                for (Map<String, Object> item : list) {
                    try{
                        Apartment apartment = new Apartment();
                        apartment.setResource(Apartment.Resource.ETAGI);
                        if (item.containsKey("object_id")) {
                            apartment.setResourceId(Long.valueOf(item.get("object_id").toString()));
                            apartment.setUrl(String.format("https://minsk.etagi.com/realty/%d/", apartment.getResourceId()));
                        }
                        if (dist.containsKey(apartment.getResourceId())) {
                            apartment.setLatitude(dist.get(apartment.getResourceId()).getLeft());
                            apartment.setLongitude(dist.get(apartment.getResourceId()).getRight());
                        }
                        if (item.containsKey("city") && item.containsKey("street") && item.containsKey("house_num")) apartment.setAddress(String.format("%s, %s, %s",item.get("city").toString(), item.get("street").toString(), item.get("house_num").toString()));
                        apartment.setUserAddress(apartment.getAddress());
                        if (item.get("square") != null) apartment.setTotalArea(Float.valueOf(item.get("square").toString()));
                        if (item.get("square_kitchen") != null) apartment.setKitchenArea(Float.valueOf(item.get("square_kitchen").toString()));
                        if (item.get("price") != null) apartment.setPriceUSD(Double.valueOf(item.get("price").toString())/RUB);
                        if (item.get("floor") != null) apartment.setFloor(Integer.valueOf(item.get("floor").toString()));
                        if (item.get("floors") != null) apartment.setNumberOfFloor(Integer.valueOf(item.get("floors").toString()));
                        if (item.get("rooms") != null) apartment.setNumberOfRoom(Integer.valueOf(item.get("rooms").toString()));

                        apartments.put(apartment.getResourceId(), apartment);
                    } catch (Exception e) {
                        System.out.println(item);
                    }



                }
            }
        }
        return apartments;
    }

}