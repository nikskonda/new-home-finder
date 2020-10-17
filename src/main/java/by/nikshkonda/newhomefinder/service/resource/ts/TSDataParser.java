package by.nikshkonda.newhomefinder.service.resource.ts;

import by.nikshkonda.newhomefinder.entity.Apartment;
import by.nikshkonda.newhomefinder.service.util.HtmlParser;
import by.nikshkonda.newhomefinder.service.util.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class TSDataParser {

    @Autowired
    private TSGetDataService getDataService;

    @Autowired
    private HtmlParser htmlParser;

    @Autowired
    private JsonParser jsonParser;

    public Map<Long, Apartment> fillApartments() {
        Map<Long, Apartment> apartments = new HashMap<>();
        int page = 1;
        int totalPages = 1;
        while (page <= totalPages) {
            Optional<String> optional = getDataService.getApartments(page);
            if (optional.isPresent()) {
                apartments.putAll(htmlParser.parserTSPage(optional.get()));
                if (page == 0) {
                    Integer pages = htmlParser.getTotalPagesTS(optional.get());
                    totalPages = pages != null ? pages : totalPages;
                }
            }
            page++;
        }
        return apartments;
    }

}