package hr.pocetnik.bookingapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class ListingUnitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private String label;

    private Integer roomCount;

    private Integer quantity;

    private BigDecimal pricePerNight;

    private Integer maxGuests;

    @ManyToOne
    @JoinColumn(name = "listing_id", nullable = false)
    private ListingEntity listing;

    
}