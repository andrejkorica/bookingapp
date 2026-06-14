package hr.pocetnik.bookingapp.dto.listing;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListingRequest {
    private String title;
    private String location;
    private String description;
    private BigDecimal pricePerNight;
    private Integer rating;
    private List<String> images;
    private List<String> amenities;

}