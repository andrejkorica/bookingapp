package hr.pocetnik.bookingapp.model;

import hr.pocetnik.bookingapp.model.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Who made the booking
    @ManyToOne
    @JoinColumn(name = "guest_id", nullable = false)
    private UserEntity guest;

    // Which listing was booked
    @ManyToOne
    @JoinColumn(name = "listing_id", nullable = false)
    private ListingEntity listing;

    // Unit snapshot
    private String unitType;
    private String unitLabel;

    // Dates
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer nights;

    // Price snapshot
    private BigDecimal pricePerNight;
    private BigDecimal totalPrice;

    // Guest snapshot
    private String guestName;
    private String guestSurname;
    private String guestEmail;
    private String guestPhoneNumber;

    // Travel info
    private String travelPurpose;
    private String arrivalTime;
    private String arrivalMethod;

    @Column(columnDefinition = "TEXT")
    private String specialRequests;

    private Boolean hasPets;
    private Boolean needsParking;

    @Column(columnDefinition = "TEXT")
    private String accessibilityRequirements;

    // Payment/confirmation info
    private String billingAddress;

    private Boolean agreedToRules;
    private Boolean agreedToCancellationPolicy;
    private Boolean confirmedInfoCorrect;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();

        if (status == null) {
            status = BookingStatus.PENDING;
        }
    }
}