package hr.pocetnik.bookingapp.controller;

import hr.pocetnik.bookingapp.dto.listing.ListingRequest;
import hr.pocetnik.bookingapp.dto.listing.ListingResponse;
import hr.pocetnik.bookingapp.service.ListingService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class ListingController {

    private final ListingService listingService;

    public ListingController(
            ListingService listingService
    ) {
        this.listingService = listingService;
    }

    @PostMapping("/seller/listings")
    public ListingResponse createListing(
            @RequestBody ListingRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();

        return listingService.createListing(
                email,
                request
        );
    }

    @GetMapping("/seller/listings")
    public List<ListingResponse> getMyListings(
            Authentication authentication
    ) {
        String email = authentication.getName();

        return listingService.getMyListings(
                email
        );
    }

    @GetMapping("/listings")
    public List<ListingResponse> getAllApprovedListings() {
        return listingService.getAllApprovedListings();
    }

    @GetMapping("/listings/{id}")
    public ListingResponse getListingById(
            @PathVariable("id") Long id
    ) {
        return listingService.getListingById(id);
    }
}