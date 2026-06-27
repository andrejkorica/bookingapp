package hr.pocetnik.bookingapp.repository;

import hr.pocetnik.bookingapp.model.ListingEntity;
import hr.pocetnik.bookingapp.model.ListingStatus;
import hr.pocetnik.bookingapp.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
