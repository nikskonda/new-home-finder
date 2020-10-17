package by.nikshkonda.newhomefinder.service.resource.ts;

import by.nikshkonda.newhomefinder.entity.Apartment;
import by.nikshkonda.newhomefinder.service.NewHomeFinderBot;
import by.nikshkonda.newhomefinder.service.resource.ServiceHelper;
import by.nikshkonda.newhomefinder.service.resource.SettingsService;
import by.nikshkonda.newhomefinder.service.resource.SettingsService.Properties;
import by.nikshkonda.newhomefinder.service.rw.ApartmentService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TSService {

    @Autowired
    private TSDataParser dataParser;

    @Autowired
    private ApartmentService apartmentService;

    @Autowired
    private ServiceHelper helper;

    @Autowired
    private NewHomeFinderBot bot;

    public List<Apartment> saveNewOrUpdated(boolean SHOW_ALL) {
        Map<Long, Apartment> newApartments = dataParser.fillApartments();
        System.out.println("Parse count:" + newApartments.size());
        Iterable<Apartment> saved = apartmentService.findAllByResourceIdInAndResource(newApartments.keySet(), Apartment.Resource.TS);
        List<Apartment> savedToReturn = new ArrayList<>();
        for (Apartment savedApt : saved) {
            Apartment newApt = newApartments.get(savedApt.getResourceId());
            if (!isUpdated(newApt, savedApt)) {
                newApartments.remove(savedApt.getResourceId());
                savedToReturn.add(savedApt);
            } else {
                newApt.setId(savedApt.getId());
            }
        }
        List<Apartment> toCalculate = (apartmentService.saveAll(newApartments.values()));
        System.out.println("To Calculate:" + toCalculate.size() + "     " + toCalculate.size()*3 + " sec, " + LocalDateTime.now().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        List<Apartment> toReturn = calculateCoordsAndFilter(toCalculate);
        if (SHOW_ALL) {
            savedToReturn.forEach(apt -> {
                if (filterByParams(apt)) {
                    bot.sendMessage(apt.toString());
                }
            });
            toReturn.addAll(savedToReturn);
        }
        return toReturn;
    }

    private List<Apartment> calculateCoordsAndFilter(List<Apartment> toCalculate){
        List<Apartment> filtered = new ArrayList<>();
        for (Apartment apartment : toCalculate) {
            try {
                Pair<Double, Double> coords = helper.getCoords(apartment.getAddress());
                if (coords != null) {
                    apartment.setLatitude(coords.getLeft());
                    apartment.setLongitude(coords.getRight());
                }
                if (filterByParams(apartment)) {
                    filtered.add(apartment);
                    bot.sendMessage(apartment.toString());
                }
                apartmentService.save(apartment);
            } catch (Exception ex) {
                System.out.println(ex);
            }

        }
        return filtered;
    }

    public boolean filterByParams(Apartment apt) {
        List<Apartment> result = new ArrayList<>();
        Map<Properties, String> properties = SettingsService.props;
        String OUTERMOST_FLOOR = properties.get(Properties.OUTERMOST_FLOOR);
        Double PRICE = Double.valueOf(properties.get(Properties.PRICE));
        Integer ROOM_NUMBER = Integer.valueOf(properties.get(Properties.ROOM_NUMBER));
        Integer YEAR = Integer.valueOf(properties.get(Properties.YEAR));
        Double DISTANCE = Double.valueOf(properties.get(Properties.DISTANCE));
        Double MIN_AREA = Double.valueOf(properties.get(Properties.MIN_AREA));
        Integer MAX_YEAR = Integer.valueOf(properties.get(Properties.MAX_YEAR));
        if ("false".equals(OUTERMOST_FLOOR) && apt.getFloor() != null && (apt.getFloor() == 1 || (apt.getNumberOfFloor() != null && apt.getFloor().equals(apt.getNumberOfFloor()))))
                return false;
            if (apt.getPriceUSD() != null && apt.getPriceUSD() > PRICE) return false;
            if (!ROOM_NUMBER.equals(apt.getNumberOfRoom())) return false;
        if (apt.getTotalArea() != null && apt.getTotalArea() < MIN_AREA) return false;
        if (apt.getYear() != null) {
            if (apt.getYear() > MAX_YEAR) return false;
            if (apt.getYear() < YEAR) return false;
        }
            if (helper.isBadDistance(apt, DISTANCE)) return false;

        return true;
    }

    private boolean isUpdated(Apartment newApt, Apartment savedApt) {
        if (savedApt.getPriceUSD() != null && newApt.getPriceUSD() != null){
            newApt.setPriceDiff(newApt.getPriceUSD() - savedApt.getPriceUSD());
        }
        return !newApt.getAddress().equalsIgnoreCase(savedApt.getAddress()) ||
                (newApt.getPriceUSD() != null && !newApt.getPriceUSD().equals(savedApt.getPriceUSD()));
    }


}
