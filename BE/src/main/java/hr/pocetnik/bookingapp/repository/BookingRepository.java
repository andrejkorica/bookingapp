package hr.pocetnik.bookingapp.repository;

import hr.pocetnik.bookingapp.dto.booking.BookingRangeResponse;
import hr.pocetnik.bookingapp.enums.BookingStatus;
import hr.pocetnik.bookingapp.model.BookingEntity;
import hr.pocetnik.bookingapp.model.ListingEntity;
import hr.pocetnik.bookingapp.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findByGuest(UserEntity guest);

    List<BookingEntity> findByListingId(Long listingId);

    List<BookingEntity> findByGuestOrderByCreatedAtDesc(UserEntity guest);

    List<BookingEntity> findByListingOrderByCreatedAtDesc(ListingEntity listing);

    List<BookingEntity> findByListing(ListingEntity listing);

    @Query("""
                select new hr.pocetnik.bookingapp.dto.booking.BookingRangeResponse(
                    b.checkIn,
                    b.checkOut
                )
                from BookingEntity b
                where b.listing.id = :listingId
                and b.status in ('PENDING', 'CONFIRMED')
            """)
    List<BookingRangeResponse> findBookedRangesByListingId(Long listingId);

    List<BookingEntity> findByListingAndStatusNotAndCheckInLessThanAndCheckOutGreaterThan(
            ListingEntity listing,
            BookingStatus status,
            LocalDate checkOut,
            LocalDate checkIn);

    @Query("""
                select coalesce(sum(b.totalPrice), 0)
                from BookingEntity b
                where b.listing.seller = :seller
                and b.status = hr.pocetnik.bookingapp.enums.BookingStatus.CONFIRMED
            """)
    BigDecimal findTotalEarningsBySeller(@Param("seller") UserEntity seller);

    @Query("""
                select count(b)
                from BookingEntity b
                where b.listing.seller = :seller
                and b.status = hr.pocetnik.bookingapp.enums.BookingStatus.CONFIRMED
            """)
    Long countActiveBookingsBySeller(@Param("seller") UserEntity seller);

    List<BookingEntity> findByListingSeller(UserEntity seller);
}