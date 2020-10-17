package by.nikshkonda.newhomefinder.service.util;

import by.nikshkonda.newhomefinder.entity.Apartment;
import by.nikshkonda.newhomefinder.entity.Metro;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HtmlParser {

    public Integer getTotalPagesRealt(String html) {
        Document doc = Jsoup.parse(html);
        String lastPage = doc.getElementsByClass("uni-paging").first().getElementsByTag("a").last().ownText();
        return convertToInteger(lastPage);
    }

    public String getSearch(String html) {
        Document doc = Jsoup.parse(html);
        return doc.getElementsByClass("uni-paging").first().getElementsByClass("active").first().attr("href");
    }

    public void parserRealtApartmentPage(Apartment apartment, String html) {
        Document doc = Jsoup.parse(html);
        Elements metro = doc.getElementsByClass("metro-station-distance");
        for (Element element : metro) {
            String metroName = element.getElementsByTag("span").first().ownText();
            List<Integer> array = convertToIntegers(element.ownText());
            if (array.size() > 0) {
                apartment.addMetro(new Metro(metroName, array.get(0)/1000.0));
            }
        }
//        Elements tableRows = doc.getElementsByClass("table-row");
//        for (Element tableRow : tableRows) {
//            tableRow.getElementsByClass("table-row-left").first().ownText().equals("")
//        }
    }

    public Map<Long, Apartment> parserRealtPage(String html) {
        Map<Long, Apartment> apartments = new HashMap<>();

        Elements list = Jsoup.parse(html).getElementsByClass("bd-table-item");

        for (Element element : list) {
            try{
                Apartment apartment = new Apartment();
                apartment.setResource(Apartment.Resource.REALT);
                apartment.setNumberOfRoom(1);
                Element temp = element.getElementsByClass("ad").first().getElementsByTag("a").first();
                apartment.setAddress(temp.ownText());
                apartment.setUserAddress(apartment.getAddress());
                apartment.setUrl(temp.attr("href"));
                List<Long> id = convertToLongs(apartment.getUrl());
                if (id.size() > 0) apartment.setResourceId(id.get(id.size()-1));
                String flour = element.getElementsByClass("ee").first().child(0).ownText();
                if (StringUtils.isNotBlank(flour)) {
                    List<Integer> array = convertToIntegers(flour);
                    if (array.size() >= 1) apartment.setFloor(array.get(0));
                    if (array.size() >= 2) apartment.setNumberOfFloor(array.get(1));
                }
                String area = element.getElementsByClass("pl").first().child(0).ownText();
                if (StringUtils.isNotBlank(area)) {
                    area = area.replaceAll(",", ".");
                    List<Float> array = convertToFloats(area);
                    if (array.size() >= 1) apartment.setTotalArea(array.get(0));
                    if (array.size() >= 2) apartment.setLivingArea(array.get(1));
                    if (array.size() >= 3) apartment.setKitchenArea(array.get(2));
                }
                String price = element.getElementsByClass("cena").first().childNodeSize() > 1 ? element.getElementsByClass("cena").first().child(0).attr("data-840") : null;
                if (StringUtils.isNotBlank(price)) {
                    apartment.setPriceUSD(convertToDouble(price.replaceAll("[^\\d.]", "")));
                }
                String[] date = element.getElementsByClass("date").first().child(0).ownText().split("\\.");
                apartment.setUpdated(LocalDateTime.of(convertToInteger(date[2]), convertToInteger(date[1]), convertToInteger(date[0]), 0, 0));
                apartments.put(apartment.getResourceId(), apartment);
            } catch (Exception ex) {
                System.out.println(ex);
                System.out.println(element.toString());
            }
        }
        return apartments;
    }

    public Integer getTotalPagesTS(String html) {
        Document doc = Jsoup.parse(html);
        String lastPage = doc.getElementsByClass("pagination-dropdown__list").first().children().last().text();
        return convertToInteger(lastPage);
    }

    public Map<Long, Apartment> parserTSPage(String html) {
        Map<Long, Apartment> apartments = new HashMap<>();

        Elements list = Jsoup.parse(html).getElementsByClass("card-item");

        for (Element element : list) {
            try{
                if (StringUtils.isBlank(element.attr("data-id"))) continue;

                Apartment apartment = new Apartment();
                apartment.setResourceId(convertToLong(element.getElementsByClass("card-item__like").first().attr("data-id")));
                apartment.setResource(Apartment.Resource.TS);
                apartment.setNumberOfRoom(1);
                String address = element.getElementsByClass("card-item__header").first().ownText();
                address = address.replace("1-комнатная квартира, ", "");
                address = address.replace("1-комнатная квартира ", "");
                address = address.replace("Продажа однокомнатной квартиры, ", "");
                apartment.setAddress(address);
                apartment.setUserAddress(apartment.getAddress());
                apartment.setUrl("https://www.t-s.by"+element.getElementsByClass("card-item__link").first().attr("href"));
                Elements els =  element.getElementsByClass("card-item__params card-item__params--flats").first().children();
                if (els.size()==3){
                    String area = els.get(0).text();
                    if (StringUtils.isNotBlank(area)) {
                        area = area.replaceAll(",", ".");
                        List<Float> array = convertToFloats(area);
                        if (array.size() >= 1) apartment.setTotalArea(array.get(0));
                        if (array.size() >= 2) apartment.setLivingArea(array.get(1));
                        if (array.size() >= 3) apartment.setKitchenArea(array.get(2));
                    }
                    String flour = els.get(1).text();
                    if (StringUtils.isNotBlank(flour)) {
                        List<Integer> array = convertToIntegers(flour);
                        if (array.size() >= 1) apartment.setFloor(array.get(0));
                        if (array.size() >= 2) apartment.setNumberOfFloor(array.get(1));
                    }
                    String year = els.get(2).text();
                    if (StringUtils.isNotBlank(year)) {
                        List<Integer> array = convertToIntegers(year);
                        if (array.size() >= 1) apartment.setYear(array.get(0));
                    }
                }

                String price = element.getElementsByClass("card-item__usd-price").first().text();
                if (StringUtils.isNotBlank(price)) {
                    apartment.setPriceUSD(convertToDouble(price.replaceAll("[^\\d.]", "")));
                }
                apartments.put(apartment.getResourceId(), apartment);
            } catch (Exception ex) {
                System.out.println(ex);
                System.out.println(element.toString());
            }
        }
        return apartments;
    }

    private Optional<String> getValueTheDeepestChildOfFirstElementIfExists(Element element) {
        Elements elements = new Elements();
        elements.add(element);
        return getValueTheDeepestChildOfFirstElementIfExists(elements);
    }

    private Optional<String> getValueTheDeepestChildOfFirstElementIfExists(Elements elements) {
        Elements prevChildren = elements;
        Elements children = prevChildren;
        while (true) {
            if (children.isEmpty()) {
                return getOwnTextFirstElementIfExist(prevChildren);
            } else {
                Optional<Element> element = getFirstElementIfExist(children);
                if (element.isPresent()) {
                    prevChildren = children;
                    children = element.get().children();
                } else {
                    return getOwnTextFirstElementIfExist(prevChildren);
                }
            }
        }
    }

    private Optional<Element> getFirstElementIfExist(Elements elements) {
        if (elements.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(elements.first());
    }

    private Optional<String> getOwnTextFirstElementIfExist(Elements elements) {
        Optional<Element> optional = getFirstElementIfExist(elements);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        Element element = optional.get();
        if (element.hasText()) {
            return Optional.of(element.ownText());
        }
        return Optional.empty();
    }

    private Optional<String> getAttrFirstElementIfExist(Elements elements, String attr) {
        Optional<Element> optional = getFirstElementIfExist(elements);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        Element element = optional.get();
        if (element.hasText()) {
            return Optional.ofNullable(element.attr(attr));
        }
        return Optional.empty();
    }

    private Long convertToLong(String str) {
        Long result = null;
        try {
            result = Long.valueOf(str);
        } catch (Exception ex) {

        }
        return result;
    }

    private Double convertToDouble(String str) {
        Double result = null;
        try {
            result = Double.valueOf(str);
        } catch (Exception ex) {

        }
        return result;
    }

    private Float convertToFloat(String str) {
        Float result = null;
        try {
            result = Float.valueOf(str);
        } catch (Exception ex) {

        }
        return result;
    }


    private Integer convertToInteger(String str) {
        Integer result = null;
        try {
            result = Integer.valueOf(str);
        } catch (Exception ex) {

        }
        return result;
    }

    private List<Float> convertToFloats(String str) {
        List<Float> list = new ArrayList<>();
        Matcher m = Pattern.compile("(?!=\\d\\.\\d\\.)([\\d.]+)").matcher(str);
        while (m.find())
        {
            list.add(convertToFloat(m.group(1)));
        }
        return list;
    }

    private List<Integer> convertToIntegers(String str) {
        List<Integer> list = new ArrayList<>();
        Matcher m = Pattern.compile("(?!=\\d)([\\d]+)").matcher(str);
        while (m.find())
        {
            list.add(convertToInteger(m.group(1)));
        }
        return list;
    }

    private List<Long> convertToLongs(String str) {
        List<Long> list = new ArrayList<>();
        Matcher m = Pattern.compile("(?!=\\d)([\\d]+)").matcher(str);
        while (m.find())
        {
            list.add(convertToLong(m.group(1)));
        }
        return list;
    }

}
