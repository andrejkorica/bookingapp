package hr.pocetnik.bookingapp.dto.listing;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ListingUnitResponse {

    private Long id;

    private String type;

    private String label;

    private Integer quantity;

    private Integer roomCount;

    private BigDecimal pricePerNight;

    private Integer maxGuests;
}