package hr.pocetnik.bookingapp.dto.listing;

import hr.pocetnik.bookingapp.enums.ListingStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ListingResponse {

    private Long id;

    private String title;

    private String location;

    private String description;

    private Integer rating; 

    private Double averageRating;

    private Long reviewCount;

    private BigDecimal lowestPrice;

    private BigDecimal highestPrice;

    private List<String> images;

    private List<String> amenities;

    private LocalDate availableFrom;

    private List<ListingUnitResponse> units;

    private ListingStatus status;

    private LocalDateTime createdAt;

    private String sellerEmail;

    private List<ListingPriceAdjustmentResponse> priceAdjustments;

    private Double latitude;

    private Double longitude;
}