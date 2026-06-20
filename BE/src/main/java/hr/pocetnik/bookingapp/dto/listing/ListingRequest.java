package hr.pocetnik.bookingapp.dto.listing;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ListingRequest {

    private String title;

    private String location;

    private String description;

    private Integer rating;

    private List<String> images;

    private List<String> amenities;

    private LocalDate availableFrom;

    private List<ListingUnitRequest> units;
}