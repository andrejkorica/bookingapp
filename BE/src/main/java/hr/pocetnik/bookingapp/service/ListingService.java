package hr.pocetnik.bookingapp.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import hr.pocetnik.bookingapp.dto.listing.ListingAvailableUnitResponse;
import hr.pocetnik.bookingapp.dto.listing.ListingRequest;
import hr.pocetnik.bookingapp.dto.listing.ListingResponse;
import hr.pocetnik.bookingapp.dto.seller.SellerStatisticsResponse;
import hr.pocetnik.bookingapp.model.ListingEntity;

public interface ListingService {

        ListingResponse createListing(
                        String sellerEmail,
                        ListingRequest request);

        List<ListingResponse> getMyListings(
                        String sellerEmail);

        List<ListingResponse> getAllApprovedListings();

        ListingResponse getListingById(
                        Long id);

        List<ListingResponse> getAllListings();

        ListingResponse approveListing(Long listingId);

        ListingResponse rejectListing(Long listingId);

        ListingResponse deleteListing(Long id, String sellerEmail);

        ListingResponse updateListing(Long id, String sellerEmail, ListingRequest request);

        List<ListingAvailableUnitResponse> getAvailableUnits(Long listingId);

        List<String> getLocations();

        List<ListingResponse> searchListings(
                        String location,
                        LocalDate checkIn,
                        LocalDate checkOut,
                        Integer adults,
                        Integer children,
                        Integer rooms,
                        List<String> amenities,
                        BigDecimal minPrice,
                        BigDecimal maxPrice,
                        Integer rating,
                        String sort);

        List<String> getAmenities();

        SellerStatisticsResponse getSellerDashboardStats(String sellerEmail);

        ListingResponse mapListingToResponse(ListingEntity listing);

}
