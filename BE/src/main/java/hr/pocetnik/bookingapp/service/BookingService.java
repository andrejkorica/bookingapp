package hr.pocetnik.bookingapp.service;

import java.util.List;

import hr.pocetnik.bookingapp.dto.booking.BookingRequest;
import hr.pocetnik.bookingapp.dto.booking.BookingResponse;

public interface BookingService {

    BookingResponse createBooking(String token, BookingRequest request);
    List<BookingResponse> getMyBookings(String token);
    BookingResponse cancelBooking(String token, Long bookingId);
}