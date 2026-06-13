package hr.pocetnik.bookingapp.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "seller_requests")
public class SellerRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String requestText;

    @Enumerated(EnumType.STRING)
    private SellerRequestStatus status;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "fk_user_id")
    private UserEntity user;
}