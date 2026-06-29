package hr.pocetnik.bookingapp.controller;

import hr.pocetnik.bookingapp.dto.listing.ListingAvailableUnitResponse;
import hr.pocetnik.bookingapp.dto.listing.ListingRequest;
import hr.pocetnik.bookingapp.dto.listing.ListingResponse;
import hr.pocetnik.bookingapp.service.ListingService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping
public class ListingController {

    private final ListingService listingService;

    public ListingController(
            ListingService listingService) {
        this.listingService = listingService;
    }

    @PostMapping("/seller/listings")
    public ListingResponse createListing(
            @RequestBody ListingRequest request,
            Authentication authentication) {
        String email = authentication.getName();

        return listingService.createListing(
                email,
                request);
    }

    @GetMapping("/seller/listings")
    public List<ListingResponse> getMyListings(
            Authentication authentication) {
        String email = authentication.getName();

        return listingService.getMyListings(
                email);
    }

    @GetMapping("/listings/locations")
    public ResponseEntity<List<String>> getLocations() {
        return ResponseEntity.ok(listingService.getLocations());
    }

    @GetMapping("/listings/{id}")
    public ListingResponse getListingById(
            @PathVariable("id") Long id) {
        return listingService.getListingById(id);
    }

    @PostMapping("/seller/listings/{id}/delete")
    public ListingResponse deleteListing(
            @PathVariable("id") Long id,
            Authentication authentication) {
        String email = authentication.getName();

        return listingService.deleteListing(id, email);
    }

    @PostMapping("/seller/listings/{id}/update")
    public ListingResponse updateListing(
            @PathVariable("id") Long id,
            @RequestBody ListingRequest request,
            Authentication authentication) {
        String email = authentication.getName();

        return listingService.updateListing(id, email, request);
    }

    @GetMapping("/listings/{listingId}/available-units")
    public List<ListingAvailableUnitResponse> getAvailableUnits(
            @PathVariable Long listingId) {

        return listingService.getAvailableUnits(listingId);
    }
@GetMapping("/listings")
public List<ListingResponse> getAllApprovedListings(
        @RequestParam(required = false) String location,
        @RequestParam(required = false) LocalDate checkIn,
        @RequestParam(required = false) LocalDate checkOut,
        @RequestParam(required = false) Integer adults,
        @RequestParam(required = false) Integer children,
        @RequestParam(required = false) Integer rooms,
        @RequestParam(required = false) List<String> amenities) {

    System.out.println("=== GET /listings ===");
    System.out.println("location = " + location);
    System.out.println("checkIn = " + checkIn);
    System.out.println("checkOut = " + checkOut);
    System.out.println("adults = " + adults);
    System.out.println("children = " + children);
    System.out.println("rooms = " + rooms);
    System.out.println("amenities = " + amenities);

    boolean hasFilters = location != null ||
            checkIn != null ||
            checkOut != null ||
            adults != null ||
            children != null ||
            rooms != null ||
            amenities != null;

    System.out.println("hasFilters = " + hasFilters);

    if (hasFilters) {
        System.out.println("Calling searchListings()");
        return listingService.searchListings(
                location,
                checkIn,
                checkOut,
                adults,
                children,
                rooms,
                amenities);
    }

    System.out.println("Calling getAllApprovedListings()");
    return listingService.getAllApprovedListings();
}

    @GetMapping("/listings/amenities")
    public ResponseEntity<List<String>> getAmenities() {
        return ResponseEntity.ok(listingService.getAmenities());
    }

}