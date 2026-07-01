package hr.pocetnik.bookingapp.model;

import java.time.LocalDateTime;

import hr.pocetnik.bookingapp.enums.SellerRequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SellerRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seller_request_id")
    private Long id;

    private String businessName;

    private String oib;

    private String iban;

    private String billingAddress;

    @Column(length = 1000)
    private String requestText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SellerRequestStatus status = SellerRequestStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity user;
}