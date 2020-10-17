package by.nikshkonda.newhomefinder.service.resource;

import by.nikshkonda.newhomefinder.service.util.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SettingsService {

    private static final String fileName = "settings.json";

    public static Map<Properties, String> props;

    @Autowired
    private JsonParser jsonParser;

    @PostConstruct
    private void setUp(){
        props = read();
    }

    public void set(String property, String value) {
        Properties prop = Properties.valueOf(property.toUpperCase());
        props.put(prop, value);
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(fileName), props);
        } catch (IOException e) {
            System.err.print("Something went wrong");
        }
    }

    public Map<Properties, String> read() {
        StringBuilder str = new StringBuilder();
        try {
            try (FileInputStream fileInputStream = new FileInputStream(fileName);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream))) {
                String readLineStr;
                while ((readLineStr = reader.readLine()) != null) {
                    str.append(readLineStr);
                }
            }
        } catch (IOException e) {
            System.out.println("Error");
        }
        Map<String, Object> map = jsonParser.toMap(str.toString());
        Map<Properties, String> result = new HashMap<>();

        for (String key : map.keySet()) {
            Properties properties = Properties.valueOf(key);
            result.put(properties, map.get(key).toString());
        }
        return result;
    }

    public enum Properties {
        PRICE, ROOM_NUMBER, DISTANCE, YEAR, OUTERMOST_FLOOR, MIN_AREA, SHOW_ALL, MAX_YEAR
    }

}