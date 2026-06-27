package hr.pocetnik.bookingapp.dto.booking;
import java.time.LocalDate;

public record BookingRangeResponse(
    LocalDate checkIn,
    LocalDate checkOut
) {
}