package hr.pocetnik.bookingapp.repository;

import hr.pocetnik.bookingapp.model.SellerDataEntity;
import hr.pocetnik.bookingapp.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerDataRepository extends JpaRepository<SellerDataEntity, Long> {
    Optional<SellerDataEntity> findByUser(UserEntity user);
}