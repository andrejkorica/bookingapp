package hr.pocetnik.bookingapp.service;



import java.util.List;

import hr.pocetnik.bookingapp.dto.listing.ListingRequest;
import hr.pocetnik.bookingapp.dto.listing.ListingResponse;

public interface ListingService {

    ListingResponse createListing(
            String sellerEmail,
            ListingRequest request
    );

    List<ListingResponse> getMyListings(
            String sellerEmail
    );

    List<ListingResponse> getAllApprovedListings();

    ListingResponse getListingById(
            Long id
    );
}
