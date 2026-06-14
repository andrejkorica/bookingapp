package hr.pocetnik.bookingapp.dto.listing;

import hr.pocetnik.bookingapp.model.ListingStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ListingResponse {
    private Long id;
    private String title;
    private String location;
    private String description;
    private BigDecimal pricePerNight;
    private Integer rating;
    private List<String> images;
    private List<String> amenities;
    private ListingStatus status;
    private String sellerEmail;
    private LocalDateTime createdAt;

}