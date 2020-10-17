package by.nikshkonda.newhomefinder.service.resource.onliner;

import by.nikshkonda.newhomefinder.entity.Apartment;
import by.nikshkonda.newhomefinder.service.resource.ServiceHelper;
import by.nikshkonda.newhomefinder.service.resource.SettingsService;
import by.nikshkonda.newhomefinder.service.resource.SettingsService.Properties;
import by.nikshkonda.newhomefinder.service.rw.ApartmentService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OnlinerService {

    @Autowired
    private OnlinerDataParser onlinerDataParser;

    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    private ServiceHelper helper;


    public List<Apartment> saveNewOrUpdated(boolean SHOW_ALL) {
        List<Apartment> newApartments = onlinerDataParser.fillApartments();
        System.out.println("Parse count:" + newApartments.size());
        Map<Long, Apartment> filtered = filterByParams(newApartments);
        System.out.println("Filtered:" + filtered.size());
        Iterable<Apartment> saved = apartmentService.findAllByResourceIdInAndResource(filtered.keySet(), Apartment.Resource.ONLINER);

        List<Apartment> toReturn = SHOW_ALL ? new ArrayList<>(filtered.values()) : null;
        for (Apartment savedApt : saved) {
            Apartment newApt = filtered.get(savedApt.getResourceId());
            if (!isUpdated(newApt, savedApt)) {
                filtered.remove(savedApt.getResourceId());
            } else {
                newApt.setId(savedApt.getId());
            }
        }
        if (!SHOW_ALL) toReturn = apartmentService.saveAll(filtered.values());
        return toReturn;
    }

    public Map<Long, Apartment> filterByParams(List<Apartment> apartments) {
        Map<Long, Apartment> map = new HashMap<>();
        Map<Properties, String> properties = SettingsService.props;
        String OUTERMOST_FLOOR = properties.get(Properties.OUTERMOST_FLOOR);
        Double PRICE = Double.valueOf(properties.get(Properties.PRICE));
        Integer ROOM_NUMBER = Integer.valueOf(properties.get(Properties.ROOM_NUMBER));
        Double DISTANCE = Double.valueOf(properties.get(Properties.DISTANCE));
        Double MIN_AREA = Double.valueOf(properties.get(Properties.MIN_AREA));
        for (Apartment apt : apartments) {
            if ("true".equals(OUTERMOST_FLOOR) && (apt.getFloor() == 1 || apt.getFloor() == apt.getNumberOfFloor()))
                continue;
            if (apt.getNumberOfFloor() <= 5) continue;
            if (apt.getPriceUSD() > PRICE) continue;
            if (!apt.getNumberOfRoom().equals(ROOM_NUMBER)) continue;
            if (helper.isBadDistance(apt, DISTANCE)) continue;
            if (apt.getTotalArea() != null && apt.getTotalArea() < MIN_AREA) continue;
            map.put(apt.getResourceId(), apt);
        }

        return map;
    }

    private boolean isUpdated(Apartment newApt, Apartment savedApt) {
        if (savedApt.getPriceUSD() != null && newApt.getPriceUSD() != null){
            newApt.setPriceDiff(newApt.getPriceUSD() - savedApt.getPriceUSD());
        }
        return !newApt.getAddress().equalsIgnoreCase(savedApt.getAddress()) ||
                !newApt.getUserAddress().equalsIgnoreCase(savedApt.getUserAddress()) ||
                !newApt.getPriceUSD().equals(savedApt.getPriceUSD()) ||
                !newApt.getLongitude().equals(savedApt.getLongitude()) ||
                !newApt.getLatitude().equals(savedApt.getLatitude());
    }
}
