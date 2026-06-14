package hr.pocetnik.bookingapp.service.implementation;

import hr.pocetnik.bookingapp.dto.listing.ListingRequest;
import hr.pocetnik.bookingapp.dto.listing.ListingResponse;
import hr.pocetnik.bookingapp.model.ListingEntity;
import hr.pocetnik.bookingapp.model.ListingStatus;
import hr.pocetnik.bookingapp.model.UserEntity;
import hr.pocetnik.bookingapp.repository.ListingRepository;
import hr.pocetnik.bookingapp.repository.UserRepository;
import hr.pocetnik.bookingapp.service.ListingService;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    public ListingServiceImpl(
            ListingRepository listingRepository,
            UserRepository userRepository
    ) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ListingResponse createListing(
            String sellerEmail,
            ListingRequest request
    ) {
        UserEntity seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        ListingEntity listing = new ListingEntity();

        listing.setTitle(request.getTitle());
        listing.setLocation(request.getLocation());
        listing.setDescription(request.getDescription());
        listing.setPricePerNight(request.getPricePerNight());
        listing.setRating(request.getRating());
        listing.setImages(request.getImages());
        listing.setAmenities(request.getAmenities());
        listing.setSeller(seller);
        listing.setStatus(ListingStatus.PENDING);

        ListingEntity savedListing = listingRepository.save(listing);

        return mapToResponse(savedListing);
    }

    @Override
    public List<ListingResponse> getMyListings(
            String sellerEmail
    ) {
        UserEntity seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        return listingRepository.findBySeller(seller)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ListingResponse> getAllApprovedListings() {
        return listingRepository.findByStatus(ListingStatus.APPROVED)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ListingResponse getListingById(
            Long id
    ) {
        ListingEntity listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        return mapToResponse(listing);
    }

    private ListingResponse mapToResponse(
            ListingEntity listing
    ) {
        ListingResponse response = new ListingResponse();

        response.setId(listing.getId());
        response.setTitle(listing.getTitle());
        response.setLocation(listing.getLocation());
        response.setDescription(listing.getDescription());
        response.setPricePerNight(listing.getPricePerNight());
        response.setRating(listing.getRating());
        response.setImages(listing.getImages());
        response.setAmenities(listing.getAmenities());
        response.setStatus(listing.getStatus());
        response.setCreatedAt(listing.getCreatedAt());

        if (listing.getSeller() != null) {
            response.setSellerEmail(
                    listing.getSeller().getEmail()
            );
        }

        return response;
    }
}