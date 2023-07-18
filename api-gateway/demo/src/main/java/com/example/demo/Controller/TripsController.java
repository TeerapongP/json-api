package com.example.demo.Controller;

import com.example.demo.Service.Trip;
import flexjson.JSONSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import com.example.demo.Service.TripsService;
@RestController
@RequestMapping
@CrossOrigin(origins = "*")
public class TripsController {

    @Autowired
    private TripsService tripsService;

    @GetMapping("/trips")
    public ResponseEntity<Map<String, Object>> getRadarData() throws Exception {
       try {
           Map<String, Object> trips = (Map<String, Object>) tripsService.getData();
           return ResponseEntity.ok(trips);
       }catch (Exception ex){
           throw new Exception("Failed to read static data: " + ex.getMessage());
       }
    }

    @PostMapping("/trips/search")
    public ResponseEntity<?> searchTrips(@RequestBody Map<String, String> requestBody) {
        HttpHeaders headers = new HttpHeaders();
        String responseMsg = "";
        headers.add("Content-Type", "application/json;charset=utf-8");
        try {
            String keyword = requestBody.get("keyword");
            List<Trip> trips = tripsService.searchByTags(keyword);
            return new ResponseEntity<>(new JSONSerializer().exclude("*.class").deepSerialize(trips), headers, HttpStatus.OK);
        } catch (Exception ex) {
            if(ex.getMessage().equalsIgnoreCase("SpecialCharacters")){
                headers.add("responseCode", "-1");
                headers.add("errMsg", ex.getMessage());
                responseMsg = "Error Input Special Characters.";
                return new ResponseEntity<String>(new JSONSerializer().deepSerialize(responseMsg)
                        , headers, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<String>(new JSONSerializer().deepSerialize(responseMsg)
                    , headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
