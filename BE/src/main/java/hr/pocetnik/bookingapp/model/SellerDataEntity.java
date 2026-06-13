package hr.pocetnik.bookingapp.model;

import jakarta.persistence.*;
@Entity
@Table(name = "seller_data")
public class SellerDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String businessName;

    private String oib;

    private String iban;

    private String billingAddress;

    @OneToOne
    @JoinColumn(name = "fk_user_id")
    private UserEntity user;
}