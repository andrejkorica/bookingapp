package hr.pocetnik.bookingapp.dto.booking;

import hr.pocetnik.bookingapp.model.BookingStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookingResponse {

    private Long id;

    private Long listingId;
    private String listingTitle;

    private String unitType;
    private String unitLabel;

    private LocalDate checkIn;
    private LocalDate checkOut;

    private Integer nights;

    private BigDecimal pricePerNight;
    private BigDecimal totalPrice;

    private BookingStatus status;

    private LocalDateTime createdAt;
    private String listingLocation;
    private String listingImage;
}