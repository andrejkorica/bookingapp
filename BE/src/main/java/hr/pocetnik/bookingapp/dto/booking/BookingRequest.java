package hr.pocetnik.bookingapp.dto.booking;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookingRequest {

    private Long listingId;

    private String unitType;

    private LocalDate checkIn;
    private LocalDate checkOut;

    private String guestName;
    private String guestSurname;
    private String guestEmail;
    private String guestPhoneNumber;

    private String travelPurpose;
    private String arrivalTime;
    private String arrivalMethod;
    private String specialRequests;

    private Boolean hasPets;
    private Boolean needsParking;

    private String accessibilityRequirements;

    private String billingAddress;

    private Boolean agreedToRules;
    private Boolean agreedToCancellationPolicy;
    private Boolean confirmedInfoCorrect;
}