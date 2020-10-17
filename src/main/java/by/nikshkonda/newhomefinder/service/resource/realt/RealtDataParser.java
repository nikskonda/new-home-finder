package by.nikshkonda.newhomefinder.service.resource.realt;

import by.nikshkonda.newhomefinder.entity.Apartment;
import by.nikshkonda.newhomefinder.service.util.HtmlParser;
import by.nikshkonda.newhomefinder.service.util.JsonParser;
import org.checkerframework.checker.nullness.Opt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class RealtDataParser {

    @Autowired
    private RealtGetDataService getDataService;

    @Autowired
    private HtmlParser htmlParser;

    @Autowired
    private JsonParser jsonParser;

    public void setDetailApartment(Apartment apartment) {
        Optional<String> optional = getDataService.getApartment(apartment.getUrl());
        optional.ifPresent(s -> htmlParser.parserRealtApartmentPage(apartment, s));
    }

    public Map<Long, Apartment> fillApartments() {
        Map<Long, Apartment> apartments = new HashMap<>();
        Optional<String> searchRes = getDataService.getSearch();
        String search = "";
        if (searchRes.isPresent()) {
            search = htmlParser.getSearch(searchRes.get());
        }

        int page = 0;
        int totalPages = 0;
        while (page <= totalPages) {
            Optional<String> optional = getDataService.getApartments(page, search);
            if (optional.isPresent()) {
                apartments.putAll(htmlParser.parserRealtPage(optional.get()));
                if (page == 0) {
                    Integer pages = htmlParser.getTotalPagesRealt(optional.get());
                    totalPages = pages != null ? pages : totalPages;
                }
            }
            page++;
        }
        return apartments;
    }

}