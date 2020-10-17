package by.nikshkonda.newhomefinder.service.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JsonParser {

    public Map<String, Object> toMap(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return new HashMap<>();
    }

    public List<Map<String, Object>> toListOfMap(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return new ArrayList<>();
    }


}
