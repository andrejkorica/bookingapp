package hr.pocetnik.bookingapp.repository;

import hr.pocetnik.bookingapp.model.BookingEntity;
import hr.pocetnik.bookingapp.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findByGuest(UserEntity guest);

    List<BookingEntity> findByListingId(Long listingId);
}