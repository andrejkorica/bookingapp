package hr.pocetnik.bookingapp.service.implementation;

import hr.pocetnik.bookingapp.dto.listing.ListingAvailableUnitResponse;
import hr.pocetnik.bookingapp.dto.listing.ListingRequest;
import hr.pocetnik.bookingapp.dto.listing.ListingResponse;
import hr.pocetnik.bookingapp.enums.BookingStatus;
import hr.pocetnik.bookingapp.enums.ListingStatus;
import hr.pocetnik.bookingapp.model.BookingEntity;

import hr.pocetnik.bookingapp.model.BookingUnitEntity;
import hr.pocetnik.bookingapp.model.ListingEntity;
import hr.pocetnik.bookingapp.model.ListingPriceAdjustmentEntity;

import hr.pocetnik.bookingapp.model.ListingUnitEntity;
import hr.pocetnik.bookingapp.model.UserEntity;
import hr.pocetnik.bookingapp.repository.BookingRepository;
import hr.pocetnik.bookingapp.repository.ListingRepository;
import hr.pocetnik.bookingapp.repository.ReviewRepository;
import hr.pocetnik.bookingapp.repository.UserRepository;
import hr.pocetnik.bookingapp.service.ListingService;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;

    public ListingServiceImpl(
            ListingRepository listingRepository,
            UserRepository userRepository,
            BookingRepository bookingRepository,
            ReviewRepository reviewRepository) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public ListingResponse createListing(
            String sellerEmail,
            ListingRequest request) {

        UserEntity seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        ListingEntity listing = new ListingEntity();

        listing.setTitle(request.getTitle());
        listing.setLocation(request.getLocation());
        listing.setDescription(request.getDescription());
        listing.setRating(request.getRating());
        listing.setImages(request.getImages());
        listing.setAmenities(request.getAmenities());
        listing.setAvailableFrom(request.getAvailableFrom());
        listing.setSeller(seller);
        listing.setStatus(ListingStatus.PENDING);
        listing.setLowestPrice(request.getLowestPrice());
        listing.setHighestPrice(request.getHighestPrice());
        listing.setLatitude(request.getLatitude());
        listing.setLongitude(request.getLongitude());

        listing.setUnits(mapUnits(request, listing));
        listing.setPriceAdjustments(mapPriceAdjustments(request, listing));

        ListingEntity savedListing = listingRepository.save(listing);

        return mapToResponse(savedListing);
    }

    @Override
    public List<ListingResponse> getMyListings(
            String sellerEmail) {

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
            Long id) {

        ListingEntity listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        return mapToResponse(listing);
    }

    @Override
    public List<ListingResponse> getAllListings() {
        return listingRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ListingResponse approveListing(Long listingId) {
        ListingEntity listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        listing.setStatus(ListingStatus.APPROVED);

        return mapToResponse(listingRepository.save(listing));
    }

    @Override
    public ListingResponse rejectListing(Long listingId) {
        ListingEntity listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        listing.setStatus(ListingStatus.REJECTED);

        return mapToResponse(listingRepository.save(listing));
    }

    @Override
    public ListingResponse deleteListing(Long id, String sellerEmail) {
        ListingEntity listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (!listing.getSeller().getEmail().equals(sellerEmail)) {
            throw new RuntimeException("You do not own this listing");
        }

        listing.setStatus(ListingStatus.DELETED);

        ListingEntity savedListing = listingRepository.save(listing);

        return mapToResponse(savedListing);
    }

    @Override
    public ListingResponse updateListing(
            Long id,
            String sellerEmail,
            ListingRequest request) {

        ListingEntity listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        if (!listing.getSeller().getEmail().equals(sellerEmail)) {
            throw new RuntimeException("You do not own this listing");
        }

        listing.setTitle(request.getTitle());
        listing.setLocation(request.getLocation());
        listing.setDescription(request.getDescription());
        listing.setRating(request.getRating());
        listing.setLowestPrice(request.getLowestPrice());
        listing.setHighestPrice(request.getHighestPrice());
        listing.setImages(request.getImages());
        listing.setAmenities(request.getAmenities());
        listing.setAvailableFrom(request.getAvailableFrom());
        listing.setLatitude(request.getLatitude());
        listing.setLongitude(request.getLongitude());

        if (listing.getUnits() == null) {
            listing.setUnits(new ArrayList<>());
        }

        listing.getUnits().clear();
        listing.getUnits().addAll(mapUnits(request, listing));

        if (listing.getPriceAdjustments() == null) {
            listing.setPriceAdjustments(new ArrayList<>());
        }

        listing.getPriceAdjustments().clear();
        listing.getPriceAdjustments().addAll(mapPriceAdjustments(request, listing));

        ListingEntity savedListing = listingRepository.save(listing);

        return mapToResponse(savedListing);
    }

    private List<ListingUnitEntity> mapUnits(
            ListingRequest request,
            ListingEntity listing) {

        if (request.getUnits() == null) {
            return List.of();
        }

        return request.getUnits()
                .stream()
                .map(unitRequest -> {
                    ListingUnitEntity unit = new ListingUnitEntity();

                    unit.setType(unitRequest.getType());
                    unit.setLabel(unitRequest.getLabel());
                    unit.setQuantity(unitRequest.getQuantity());
                    unit.setPricePerNight(unitRequest.getPricePerNight());
                    unit.setRoomCount(unitRequest.getRoomCount());
                    unit.setListing(listing);
                    unit.setMaxGuests(unitRequest.getMaxGuests());

                    return unit;
                })
                .collect(Collectors.toList());
    }

    private List<ListingPriceAdjustmentEntity> mapPriceAdjustments(
            ListingRequest request,
            ListingEntity listing) {

        if (request.getPriceAdjustments() == null) {
            return List.of();
        }

        return request.getPriceAdjustments()
                .stream()
                .map(adjustmentRequest -> {
                    ListingPriceAdjustmentEntity adjustment = new ListingPriceAdjustmentEntity();

                    adjustment.setStartDate(adjustmentRequest.getStartDate());
                    adjustment.setEndDate(adjustmentRequest.getEndDate());
                    adjustment.setPercent(adjustmentRequest.getPercent());
                    adjustment.setListing(listing);

                    return adjustment;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ListingResponse> searchListings(
            String location,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer adults,
            Integer children,
            Integer rooms) {

        Integer totalGuests = null;

        if (adults != null || children != null) {
            totalGuests = (adults != null ? adults : 0)
                    + (children != null ? children : 0);
        }

        List<ListingEntity> listings;

        if (checkIn == null) {
            listings = listingRepository.searchListingsWithoutDate(
                    ListingStatus.APPROVED,
                    blankToNull(location),
                    rooms,
                    totalGuests);
        } else {
            listings = listingRepository.searchListingsWithDate(
                    ListingStatus.APPROVED,
                    blankToNull(location),
                    checkIn,
                    rooms,
                    totalGuests);
        }

        return listings.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    @Override
    public List<ListingAvailableUnitResponse> getAvailableUnits(Long listingId) {
        ListingEntity listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found."));

        List<BookingEntity> activeBookings = bookingRepository
                .findByListing(listing)
                .stream()
                .filter(booking -> booking.getStatus() != BookingStatus.CANCELLED)
                .toList();

        Map<String, Integer> bookedByUnitType = new HashMap<>();

        for (BookingEntity booking : activeBookings) {
            if (booking.getUnits() == null) {
                continue;
            }

            for (BookingUnitEntity bookedUnit : booking.getUnits()) {
                bookedByUnitType.merge(
                        bookedUnit.getUnitType(),
                        bookedUnit.getQuantity(),
                        Integer::sum);
            }
        }

        return listing.getUnits()
                .stream()
                .map(unit -> {
                    int bookedQuantity = bookedByUnitType.getOrDefault(unit.getType(), 0);
                    int availableQuantity = Math.max(0, unit.getQuantity() - bookedQuantity);

                    ListingAvailableUnitResponse response = new ListingAvailableUnitResponse();

                    response.setType(unit.getType());
                    response.setLabel(unit.getLabel());
                    response.setQuantity(unit.getQuantity());
                    response.setAvailableQuantity(availableQuantity);
                    response.setMaxGuests(unit.getMaxGuests());
                    response.setPricePerNight(unit.getPricePerNight());

                    return response;
                })
                .toList();
    }

    @Override
    public List<String> getLocations() {
        return listingRepository.findDistinctLocations();
    }

    private ListingResponse mapToResponse(
            ListingEntity listing) {

        ListingResponse response = new ListingResponse();
        Long reviewCount = reviewRepository.countByListingId(listing.getId());
        Double averageRating = reviewRepository.findAverageRatingByListingId(listing.getId());

        response.setId(listing.getId());
        response.setTitle(listing.getTitle());
        response.setLocation(listing.getLocation());
        response.setDescription(listing.getDescription());
        response.setReviewCount(reviewCount);
        response.setAverageRating(averageRating != null ? averageRating : 0.0);
        response.setRating(listing.getRating());
        response.setImages(listing.getImages());
        response.setAmenities(listing.getAmenities());
        response.setAvailableFrom(listing.getAvailableFrom());
        response.setStatus(listing.getStatus());
        response.setCreatedAt(listing.getCreatedAt());
        response.setLowestPrice(listing.getLowestPrice());
        response.setHighestPrice(listing.getHighestPrice());
        response.setLatitude(listing.getLatitude());
        response.setLongitude(listing.getLongitude());

        if (listing.getUnits() != null) {
            response.setUnits(
                    listing.getUnits()
                            .stream()
                            .map(unit -> {
                                var unitResponse = new hr.pocetnik.bookingapp.dto.listing.ListingUnitResponse();

                                unitResponse.setId(unit.getId());
                                unitResponse.setType(unit.getType());
                                unitResponse.setLabel(unit.getLabel());
                                unitResponse.setQuantity(unit.getQuantity());
                                unitResponse.setRoomCount(unit.getRoomCount());
                                unitResponse.setPricePerNight(unit.getPricePerNight());
                                unitResponse.setMaxGuests(unit.getMaxGuests());

                                return unitResponse;
                            })
                            .toList());
        }

        if (listing.getPriceAdjustments() != null) {
            response.setPriceAdjustments(
                    listing.getPriceAdjustments()
                            .stream()
                            .map(adjustment -> {
                                var adjustmentResponse = new hr.pocetnik.bookingapp.dto.listing.ListingPriceAdjustmentResponse();

                                adjustmentResponse.setId(adjustment.getId());
                                adjustmentResponse.setStartDate(adjustment.getStartDate());
                                adjustmentResponse.setEndDate(adjustment.getEndDate());
                                adjustmentResponse.setPercent(adjustment.getPercent());

                                return adjustmentResponse;
                            })
                            .toList());
        }

        if (listing.getSeller() != null) {
            response.setSellerEmail(listing.getSeller().getEmail());
        }

        return response;
    }
}