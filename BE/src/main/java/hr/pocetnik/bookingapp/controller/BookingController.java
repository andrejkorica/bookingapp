package hr.pocetnik.bookingapp.controller;

import hr.pocetnik.bookingapp.dto.booking.BookingDetailsResponse;
import hr.pocetnik.bookingapp.dto.booking.BookingRangeResponse;
import hr.pocetnik.bookingapp.dto.booking.BookingRequest;
import hr.pocetnik.bookingapp.dto.booking.BookingResponse;
import hr.pocetnik.bookingapp.exception.TokenNotFoundException;
import hr.pocetnik.bookingapp.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @CookieValue(name = "token", required = false) String token,
            @RequestBody BookingRequest request) {
        if (token == null || token.isEmpty()) {
            throw new TokenNotFoundException();
        }

        BookingResponse response = bookingService.createBooking(token, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<BookingResponse>> getMyBookings(
            @CookieValue(name = "token", required = false) String token) {
        if (token == null || token.isEmpty()) {
            throw new TokenNotFoundException();
        }

        return ResponseEntity.ok(bookingService.getMyBookings(token));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @CookieValue(name = "token", required = false) String token,
            @PathVariable("id") Long id) {
        if (token == null || token.isEmpty()) {
            throw new TokenNotFoundException();
        }

        BookingResponse response = bookingService.cancelBooking(token, id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookingId}")
    public BookingDetailsResponse getBooking(
            @CookieValue("token") String token,
            @PathVariable Long bookingId) {

        return bookingService.getBookingDetails(token, bookingId);
    }

    @GetMapping("/seller/listings/{listingId}/bookings")
    public List<BookingResponse> getBookingsForSellerListing(
            @PathVariable Long listingId,
            @CookieValue("token") String token) {

        return bookingService.getBookingsForSellerListing(token, listingId);
    }

    @PostMapping("seller/{bookingId}/approve")
    public BookingResponse approveBooking(
            @CookieValue("token") String token,
            @PathVariable Long bookingId) {

        return bookingService.approveBooking(token, bookingId);
    }

    @PostMapping("seller/{bookingId}/reject")
    public BookingResponse rejectBooking(
            @CookieValue("token") String token,
            @PathVariable Long bookingId) {

        return bookingService.rejectBooking(token, bookingId);
    }

    @GetMapping("/{listingId}/booked-ranges")
    public ResponseEntity<List<BookingRangeResponse>> getBookedRanges(
            @PathVariable Long listingId) {
        return ResponseEntity.ok(bookingService.getBookedRanges(listingId));
    }

    @GetMapping("/seller/bookings")
    public List<BookingDetailsResponse> getSellerBookings(
            @CookieValue("token") String token) {

        if (token == null || token.isEmpty()) {
            throw new TokenNotFoundException();
        }

        return bookingService.getSellerBookings(token);
    }

}