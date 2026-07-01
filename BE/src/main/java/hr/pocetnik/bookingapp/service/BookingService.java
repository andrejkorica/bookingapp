package hr.pocetnik.bookingapp.service;

import java.util.List;

import hr.pocetnik.bookingapp.dto.booking.BookingDetailsResponse;
import hr.pocetnik.bookingapp.dto.booking.BookingRangeResponse;
import hr.pocetnik.bookingapp.dto.booking.BookingRequest;
import hr.pocetnik.bookingapp.dto.booking.BookingResponse;

public interface BookingService {

    BookingResponse createBooking(String token, BookingRequest request);

    List<BookingResponse> getMyBookings(String token);

    BookingResponse cancelBooking(String token, Long bookingId);

    BookingDetailsResponse getBookingDetails(String token, Long bookingId);

    List<BookingResponse> getBookingsForSellerListing(String token, Long listingId);

    BookingResponse approveBooking(String token, Long bookingId);

    BookingResponse rejectBooking(String token, Long bookingId);

    List<BookingRangeResponse> getBookedRanges(Long listingId);

    List<BookingDetailsResponse> getSellerBookings(String sellerEmail);

}