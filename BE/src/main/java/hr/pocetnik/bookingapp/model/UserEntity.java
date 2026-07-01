package hr.pocetnik.bookingapp.model;

import java.util.HashSet;
import java.util.Set;

import hr.pocetnik.bookingapp.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String name;

    private String surname;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private SellerDataEntity sellerData;

    private String profileImageUrl;

    @ManyToMany
    @JoinTable(name = "user_favorite_listings", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "listing_id"))
    private Set<ListingEntity> favoriteListings = new HashSet<>();
}