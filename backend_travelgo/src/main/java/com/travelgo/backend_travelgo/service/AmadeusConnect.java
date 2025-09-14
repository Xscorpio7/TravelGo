package com.travelgo.backend_travelgo.service;



import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.resources.Location;
import com.amadeus.resources.FlightOfferSearch;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.referencedata.Locations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AmadeusConnect {

    

    private final Amadeus amadeus;

    public AmadeusConnect(
            @Value("${amadeus.client.id}") String clientId,
            @Value("${amadeus.client.secret}") String clientSecret) {
        this.amadeus = Amadeus.builder(clientId, clientSecret).build();
    }
     public Location[] location(String keyword) throws ResponseException {
        return amadeus.referenceData.locations.get(Params.with("keyword",keyword)
            .and("subType", Locations.AIRPORT));
    }

}
