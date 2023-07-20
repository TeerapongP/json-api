package com.example.demo.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

//@Slf4j
@Service
public class TripsService {
    private List<Trip> trips;

    public Map<String, Object> getData() {
        try {
            ClassPathResource staticDataResource = new ClassPathResource("./json-server/db.json");
            String staticDataString = IOUtils.toString(staticDataResource.getInputStream(), StandardCharsets.UTF_8);
            JSONObject staticDataJson = new JSONObject(staticDataString);
            return staticDataJson.toMap();
        } catch (IOException e) {

            throw new RuntimeException("Failed to read static data.", e);
        }
    }

    public List<Trip> searchByTags(String keyword) throws Exception {

        ClassPathResource staticDataResource = new ClassPathResource("./json-server/db.json");
        String staticDataString = IOUtils.toString(staticDataResource.getInputStream(), StandardCharsets.UTF_8);

        try {
            if (containsSpecialCharacters(keyword)) {
                throw new Exception("SpecialCharacters");
            }
            JsonNode jsonData = new ObjectMapper().readTree(staticDataString);
            JsonNode tripsNode = jsonData.get("trips");
            List<Trip> trips = new ObjectMapper().readValue(tripsNode.toString(), new TypeReference<List<Trip>>() {});

            List<Trip> searchResults = new ArrayList<>();
            for (Trip trip : trips) {
                if (trip.getTags().contains(keyword)) {
                    searchResults.add(trip);
                }
            }
            return searchResults;
        } catch (Exception e) {
            throw new Exception("An error occurred during the search.");
        }
    }
        private boolean  containsSpecialCharacters (String keyword) {
            String pattern = ("[`!@#$%^*()_+=\\[\\]{};':\"\\\\|,.<>/?~]");

            return keyword.equalsIgnoreCase(pattern) ? true : false;
        }
}
