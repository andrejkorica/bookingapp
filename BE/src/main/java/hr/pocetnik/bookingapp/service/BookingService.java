package hr.pocetnik.bookingapp.service;

import hr.pocetnik.bookingapp.dto.booking.BookingRequest;
import hr.pocetnik.bookingapp.dto.booking.BookingResponse;

public interface BookingService {

    BookingResponse createBooking(String token, BookingRequest request);
}