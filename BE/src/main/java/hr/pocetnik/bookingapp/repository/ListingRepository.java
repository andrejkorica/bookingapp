package hr.pocetnik.bookingapp.repository;

import hr.pocetnik.bookingapp.enums.ListingStatus;
import hr.pocetnik.bookingapp.model.ListingEntity;
import hr.pocetnik.bookingapp.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ListingRepository extends JpaRepository<ListingEntity, Long> {
        List<ListingEntity> findBySeller(UserEntity seller);

        List<ListingEntity> findByStatus(ListingStatus status);

        List<ListingEntity> findBySellerAndStatus(UserEntity seller, ListingStatus status);

        List<ListingEntity> findAllByOrderByCreatedAtDesc();

        @Query("""
                            select distinct l.location
                            from ListingEntity l
                            where l.location is not null
                            and l.location <> ''
                            order by l.location
                        """)
        List<String> findDistinctLocations();

        @Query("""
                            select distinct l
                            from ListingEntity l
                            join l.units u
                            where l.status = :status
                            and lower(l.location) like lower(concat('%', coalesce(:location, ''), '%'))
                            and (:checkIn is null or l.availableFrom <= :checkIn)
                            and (:rooms is null or u.roomCount >= :rooms)
                            and (:totalGuests is null or u.maxGuests >= :totalGuests)
                            order by l.createdAt desc
                        """)
        List<ListingEntity> searchListings(
                        @Param("status") ListingStatus status,
                        @Param("location") String location,
                        @Param("checkIn") LocalDate checkIn,
                        @Param("rooms") Integer rooms,
                        @Param("totalGuests") Integer totalGuests);

        @Query("""
                            select distinct amenity
                            from ListingEntity l
                            join l.amenities amenity
                            order by amenity
                        """)
                        
        List<String> findDistinctAmenities();

        Long countBySeller(UserEntity seller);
}
