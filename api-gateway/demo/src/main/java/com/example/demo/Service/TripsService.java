package com.example.demo.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
public class TripsService {
    private List<Trip> trips;

    public Map<String, Object> getData() {
        try {
            ClassPathResource staticDataResource = new ClassPathResource("./json-server/db.json");
            String staticDataString = IOUtils.toString(staticDataResource.getInputStream(), StandardCharsets.UTF_8);
            JSONObject staticDataJson = new JSONObject(staticDataString);
            return staticDataJson.toMap();
        } catch (Exception e) {

            throw new RuntimeException("Failed to read static data.", e);
        }
    }

    public List<Trip> searchByTags(String keyword) throws Exception {
        ClassPathResource staticDataResource = new ClassPathResource("./json-server/db.json");
        String staticDataString = IOUtils.toString(staticDataResource.getInputStream(), StandardCharsets.UTF_8);
        try {
            if (containsSpecialCharacters(keyword)) {
                log.info("SpecialCharacters");
                throw new Exception("SpecialCharacters");
            }
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonData = objectMapper.readTree(staticDataString);
            JsonNode tripsNode = jsonData.get("trips");
            List<Trip> trips = objectMapper.readValue(tripsNode.toString(), new TypeReference<List<Trip>>() {});
            List<Trip> searchResults = new ArrayList<>();
            for (Trip trip : trips) {
                if (trip.getTags().contains(keyword)) {
                    searchResults.add(trip);
                }
            }
            return searchResults;
        } catch (Exception ex) {
            log.info("SpecialCharacters");
            throw new Exception(ex.getMessage());
        }
    }
    private boolean containsSpecialCharacters(String keyword) {
        String pattern = "[`!@#$%^*()_+\\[\\]{};':\"\\\\|,.<>/?~]";
        try {
            return keyword.matches(".*" + pattern + ".*");
        } catch (Exception ex) {
            log.info(ex.getMessage());
        }
        return false;
    }
}
