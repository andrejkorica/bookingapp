package hr.pocetnik.bookingapp.service.implementation;

import hr.pocetnik.bookingapp.dto.booking.BookingRequest;
import hr.pocetnik.bookingapp.dto.booking.BookingResponse;
import hr.pocetnik.bookingapp.dto.booking.BookingUnitRequest;
import hr.pocetnik.bookingapp.exception.UserNotFoundException;
import hr.pocetnik.bookingapp.model.*;
import hr.pocetnik.bookingapp.repository.BookingRepository;
import hr.pocetnik.bookingapp.repository.ListingRepository;
import hr.pocetnik.bookingapp.repository.UserRepository;
import hr.pocetnik.bookingapp.service.BookingService;
import hr.pocetnik.bookingapp.service.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import hr.pocetnik.bookingapp.dto.booking.BookingDetailsResponse;
import hr.pocetnik.bookingapp.dto.booking.BookingRangeResponse;
import hr.pocetnik.bookingapp.dto.booking.BookingUnitResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            ListingRepository listingRepository,
            UserRepository userRepository,
            JwtService jwtService) {
        this.bookingRepository = bookingRepository;
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public BookingResponse createBooking(String token, BookingRequest request) {
        Claims claims = jwtService.extractAllClaims(token);
        String email = claims.getSubject();

        UserEntity guest = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        ListingEntity listing = listingRepository.findById(request.getListingId())
                .orElseThrow(() -> new RuntimeException("Listing not found."));

        if (listing.getSeller().getEmail().equals(guest.getEmail())) {
            throw new RuntimeException("You cannot book your own listing.");
        }

        LocalDate checkIn = request.getCheckIn();
        LocalDate checkOut = request.getCheckOut();

        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            throw new RuntimeException("Invalid booking dates.");
        }

        if (request.getUnits() == null || request.getUnits().isEmpty()) {
            throw new RuntimeException("At least one unit must be selected.");
        }

        int nights = (int) ChronoUnit.DAYS.between(checkIn, checkOut);

        BigDecimal basePricePerNight = request.getUnits()
                .stream()
                .map(unitRequest -> {
                    ListingUnitEntity listingUnit = findListingUnit(listing, unitRequest);

                    validateUnitQuantity(unitRequest, listingUnit);

                    return listingUnit.getPricePerNight()
                            .multiply(BigDecimal.valueOf(unitRequest.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPrice = calculateTotalPrice(
                checkIn,
                checkOut,
                basePricePerNight,
                listing);

        BigDecimal averagePricePerNight = totalPrice.divide(
                BigDecimal.valueOf(nights),
                2,
                java.math.RoundingMode.HALF_UP);

        BookingEntity booking = new BookingEntity();

        booking.setGuest(guest);
        booking.setListing(listing);

        booking.setUnits(new ArrayList<>());

        for (BookingUnitRequest unitRequest : request.getUnits()) {
            ListingUnitEntity listingUnit = findListingUnit(listing, unitRequest);
            validateUnitQuantity(unitRequest, listingUnit);

            BookingUnitEntity bookingUnit = new BookingUnitEntity();

            bookingUnit.setUnitType(unitRequest.getUnitType());
            bookingUnit.setQuantity(unitRequest.getQuantity());
            bookingUnit.setBooking(booking);

            booking.getUnits().add(bookingUnit);
        }

        booking.setUnitLabel(
                request.getUnits()
                        .stream()
                        .map(unitRequest -> {
                            ListingUnitEntity listingUnit = findListingUnit(listing, unitRequest);

                            return unitRequest.getQuantity() + " × " + listingUnit.getLabel();
                        })
                        .collect(Collectors.joining(", ")));

        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setNights(nights);

        booking.setPricePerNight(averagePricePerNight);
        booking.setTotalPrice(totalPrice);

        booking.setGuestName(request.getGuestName());
        booking.setGuestSurname(request.getGuestSurname());
        booking.setGuestEmail(request.getGuestEmail());
        booking.setGuestPhoneNumber(request.getGuestPhoneNumber());

        booking.setTravelPurpose(request.getTravelPurpose());
        booking.setArrivalTime(request.getArrivalTime());
        booking.setArrivalMethod(request.getArrivalMethod());
        booking.setSpecialRequests(request.getSpecialRequests());
        booking.setHasPets(request.getHasPets());
        booking.setNeedsParking(request.getNeedsParking());
        booking.setAccessibilityRequirements(request.getAccessibilityRequirements());

        booking.setBillingAddress(request.getBillingAddress());
        booking.setAgreedToRules(request.getAgreedToRules());
        booking.setAgreedToCancellationPolicy(request.getAgreedToCancellationPolicy());
        booking.setConfirmedInfoCorrect(request.getConfirmedInfoCorrect());

        booking.setStatus(BookingStatus.PENDING);

        BookingEntity savedBooking = bookingRepository.save(booking);

        return mapToResponse(savedBooking);
    }

    private ListingUnitEntity findListingUnit(
            ListingEntity listing,
            BookingUnitRequest unitRequest) {

        return listing.getUnits()
                .stream()
                .filter(unit -> unit.getType().equals(unitRequest.getUnitType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Selected unit not found: " + unitRequest.getUnitType()));
    }

    private void validateUnitQuantity(
            BookingUnitRequest unitRequest,
            ListingUnitEntity listingUnit) {

        if (unitRequest.getQuantity() == null || unitRequest.getQuantity() <= 0) {
            throw new RuntimeException("Invalid unit quantity.");
        }

        if (unitRequest.getQuantity() > listingUnit.getQuantity()) {
            throw new RuntimeException(
                    "Not enough available units for: " + listingUnit.getLabel());
        }
    }

    private BigDecimal calculateTotalPrice(
            LocalDate checkIn,
            LocalDate checkOut,
            BigDecimal basePricePerNight,
            ListingEntity listing) {

        BigDecimal total = BigDecimal.ZERO;
        LocalDate current = checkIn;

        while (current.isBefore(checkOut)) {
            BigDecimal nightlyPrice = basePricePerNight;

            if (listing.getPriceAdjustments() != null) {
                for (ListingPriceAdjustmentEntity adjustment : listing.getPriceAdjustments()) {
                    if (!current.isBefore(adjustment.getStartDate()) &&
                            !current.isAfter(adjustment.getEndDate())) {

                        BigDecimal percent = adjustment.getPercent()
                                .divide(BigDecimal.valueOf(100));

                        nightlyPrice = nightlyPrice.add(nightlyPrice.multiply(percent));
                    }
                }
            }

            total = total.add(nightlyPrice);
            current = current.plusDays(1);
        }

        return total;
    }

    private BookingResponse mapToResponse(BookingEntity booking) {
        BookingResponse response = new BookingResponse();

        response.setId(booking.getId());
        response.setListingId(booking.getListing().getId());
        response.setListingTitle(booking.getListing().getTitle());

        response.setUnitLabel(booking.getUnitLabel());

        response.setCheckIn(booking.getCheckIn());
        response.setCheckOut(booking.getCheckOut());
        response.setNights(booking.getNights());

        response.setPricePerNight(booking.getPricePerNight());
        response.setTotalPrice(booking.getTotalPrice());

        response.setStatus(booking.getStatus());
        response.setCreatedAt(booking.getCreatedAt());
        response.setListingLocation(booking.getListing().getLocation());

        if (booking.getListing().getImages() != null &&
                !booking.getListing().getImages().isEmpty()) {

            response.setListingImage(
                    booking.getListing().getImages().get(0));
        }

        return response;
    }

    @Override
    public List<BookingResponse> getMyBookings(String token) {
        Claims claims = jwtService.extractAllClaims(token);
        String email = claims.getSubject();

        UserEntity guest = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        return bookingRepository
                .findByGuestOrderByCreatedAtDesc(guest)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BookingResponse cancelBooking(String token, Long bookingId) {
        Claims claims = jwtService.extractAllClaims(token);
        String email = claims.getSubject();

        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found."));

        if (!booking.getGuest().getEmail().equals(email)) {
            throw new RuntimeException("You can only cancel your own bookings.");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled.");
        }

        LocalDate latestCancellationDate = booking.getCheckIn().minusDays(1);

        if (!LocalDate.now().isBefore(latestCancellationDate)) {
            throw new RuntimeException("Cancellation period is over.");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        BookingEntity savedBooking = bookingRepository.save(booking);

        return mapToResponse(savedBooking);
    }

    @Override
    public BookingDetailsResponse getBookingDetails(String token, Long bookingId) {
        Claims claims = jwtService.extractAllClaims(token);
        String email = claims.getSubject();

        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found."));

        if (!booking.getGuest().getEmail().equals(email)
                && !booking.getListing().getSeller().getEmail().equals(email)) {
            throw new RuntimeException("You are not allowed to view this booking.");
        }

        return mapToDetailsResponse(booking);
    }

    private BookingDetailsResponse mapToDetailsResponse(BookingEntity booking) {
        BookingDetailsResponse response = new BookingDetailsResponse();

        response.setId(booking.getId());

        response.setListingId(booking.getListing().getId());
        response.setListingTitle(booking.getListing().getTitle());
        response.setListingLocation(booking.getListing().getLocation());

        if (booking.getListing().getImages() != null
                && !booking.getListing().getImages().isEmpty()) {
            response.setListingImage(booking.getListing().getImages().get(0));
        }

        response.setUnits(
                booking.getUnits()
                        .stream()
                        .map(unit -> {
                            BookingUnitResponse unitResponse = new BookingUnitResponse();

                            unitResponse.setUnitType(unit.getUnitType());
                            unitResponse.setUnitLabel(unit.getUnitLabel());
                            unitResponse.setQuantity(unit.getQuantity());

                            return unitResponse;
                        })
                        .toList());

        response.setCheckIn(booking.getCheckIn());
        response.setCheckOut(booking.getCheckOut());
        response.setNights(booking.getNights());

        response.setPricePerNight(booking.getPricePerNight());
        response.setTotalPrice(booking.getTotalPrice());

        response.setGuestName(booking.getGuestName());
        response.setGuestSurname(booking.getGuestSurname());
        response.setGuestEmail(booking.getGuestEmail());
        response.setGuestPhoneNumber(booking.getGuestPhoneNumber());

        response.setTravelPurpose(booking.getTravelPurpose());
        response.setArrivalTime(booking.getArrivalTime());
        response.setArrivalMethod(booking.getArrivalMethod());
        response.setSpecialRequests(booking.getSpecialRequests());
        response.setHasPets(booking.getHasPets());
        response.setNeedsParking(booking.getNeedsParking());
        response.setAccessibilityRequirements(booking.getAccessibilityRequirements());

        response.setBillingAddress(booking.getBillingAddress());

        response.setAgreedToRules(booking.getAgreedToRules());
        response.setAgreedToCancellationPolicy(booking.getAgreedToCancellationPolicy());
        response.setConfirmedInfoCorrect(booking.getConfirmedInfoCorrect());

        response.setStatus(booking.getStatus());
        response.setCreatedAt(booking.getCreatedAt());

        return response;
    }

    @Override
    public List<BookingResponse> getBookingsForSellerListing(String token, Long listingId) {
        Claims claims = jwtService.extractAllClaims(token);
        String email = claims.getSubject();

        ListingEntity listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found."));

        if (!listing.getSeller().getEmail().equals(email)) {
            throw new RuntimeException("You can only view bookings for your own listing.");
        }

        return bookingRepository.findByListingOrderByCreatedAtDesc(listing)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BookingResponse approveBooking(String token, Long bookingId) {
        BookingEntity booking = getSellerOwnedBooking(token, bookingId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Only pending bookings can be approved.");
        }

        booking.setStatus(BookingStatus.CONFIRMED);

        return mapToResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse rejectBooking(String token, Long bookingId) {
        BookingEntity booking = getSellerOwnedBooking(token, bookingId);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Only pending bookings can be rejected.");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        return mapToResponse(bookingRepository.save(booking));
    }

    private BookingEntity getSellerOwnedBooking(String token, Long bookingId) {
        Claims claims = jwtService.extractAllClaims(token);
        String email = claims.getSubject();

        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found."));

        if (!booking.getListing().getSeller().getEmail().equals(email)) {
            throw new RuntimeException("You can only manage bookings for your own listings.");
        }

        return booking;
    }

    @Override
    public List<BookingRangeResponse> getBookedRanges(Long listingId) {
        return bookingRepository.findBookedRangesByListingId(listingId);
    }
}