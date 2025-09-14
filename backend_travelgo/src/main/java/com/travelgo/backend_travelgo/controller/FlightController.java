package com.travelgo.backend_travelgo.controller;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.FlightOfferSearch;
import com.amadeus.resources.Location;
import org.springframework.web.bind.annotation.*;
import com.travelgo.backend_travelgo.service.AmadeusConnect;

@RestController
@RequestMapping("/flights")
public class FlightController {

    private final AmadeusConnect amadeusConnect;

    public FlightController(AmadeusConnect amadeusConnect) {
        this.amadeusConnect = amadeusConnect;
    }
    
     @GetMapping("/locations")
    public Location[] locations(@RequestParam(required=true) String keyword) throws ResponseException {
        return amadeusConnect.location(keyword);
    }
   /*  @GetMapping
    public FlightOfferSearch[] flights(@RequestParam(required=true) String origin,
            @RequestParam (required = true) String destination,
            @RequestParam (required = true) String departDate,
            @RequestParam (required = true) String adults,
            @RequestParam(required = true) String returnDate)
            throws ResponseException {
        return amadeusConnect.flights(origin, destination, departDate, adults, returnDate);
    }*/


}
