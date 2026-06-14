package hr.pocetnik.bookingapp.repository;

import hr.pocetnik.bookingapp.model.SellerRequestEntity;
import hr.pocetnik.bookingapp.model.SellerRequestStatus;
import hr.pocetnik.bookingapp.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellerRequestRepository extends JpaRepository<SellerRequestEntity, Long> {
    List<SellerRequestEntity> findByStatus(SellerRequestStatus status);
    boolean existsByUserAndStatus(UserEntity user, SellerRequestStatus status);
}