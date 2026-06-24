package hr.pocetnik.bookingapp.controller;
import hr.pocetnik.bookingapp.dto.booking.BookingRequest;
import hr.pocetnik.bookingapp.dto.booking.BookingResponse;
import hr.pocetnik.bookingapp.exception.TokenNotFoundException;
import hr.pocetnik.bookingapp.service.BookingService;

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
            @RequestBody BookingRequest request
    ) {
        if (token == null || token.isEmpty()) {
            throw new TokenNotFoundException();
        }

        BookingResponse response = bookingService.createBooking(token, request);

        return ResponseEntity.ok(response);
    }
}