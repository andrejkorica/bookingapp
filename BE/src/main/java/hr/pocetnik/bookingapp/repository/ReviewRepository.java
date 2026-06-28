package hr.pocetnik.bookingapp.repository;

import hr.pocetnik.bookingapp.model.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository
        extends JpaRepository<ReviewEntity, Long> {

    List<ReviewEntity> findByListingIdOrderByCreatedAtDesc(Long listingId);

    long countByListingId(Long listingId);

    @Query("""
                select avg(r.rating)
                from ReviewEntity r
                where r.listing.id = :listingId
            """)
    Double findAverageRatingByListingId(Long listingId);

}