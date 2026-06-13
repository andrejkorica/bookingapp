package hr.pocetnik.bookingapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

import static jakarta.persistence.DiscriminatorType.STRING;

// Osnovna struktura iz artikla: https://medium.com/@rihab.beji099/creating-my-first-entity-with-spring-boot-version-3-3-5-and-hibernate-version-6-2-7-b0e63bf8856f

@Entity  // Definira klasu kao nacrt za tablicu u bazi. Doc: https://jakarta.ee/specifications/persistence/3.1/apidocs/jakarta.persistence/jakarta/persistence/entity
@Table(name = "users")  // Definira ime tablice u bazi podataka. Doc: https://jakarta.ee/specifications/persistence/3.0/apidocs/jakarta.persistence/jakarta/persistence/table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)  // Definira strategiju za nasljeđivanje između entiteta, omogućava da spremamo Admin i Seller profile u istu tablicu te se automatski nadovezuje na slijedeću liniju. Doc: https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/inheritance
@DiscriminatorColumn(name = "ROLETYPE", discriminatorType = STRING, length = 10)  // Koristi se za stvaranje dodatne kolone da bi se odredila vrsta svakog ulaznog podatka te omogućuje definiciju uloge korisničkog profila. Doc: https://jakarta.ee/specifications/persistence/3.1/apidocs/jakarta.persistence/jakarta/persistence/discriminatorcolumn
@DiscriminatorValue("USER")  // Postavlja osnovnu vrijednost kolone ako nije specificirano tijekom izrade. Doc: https://jakarta.ee/specifications/persistence/3.0/apidocs/jakarta.persistence/jakarta/persistence/discriminatorvalue
@Getter
@Setter
public class UserEntity {

    @Id  // Definira da je slijedeća varijabla PK za entitet. Doc: https://jakarta.ee/specifications/persistence/3.0/apidocs/jakarta.persistence/jakarta/persistence/id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Pravilnik za generaciju PK, 'IDENTITY' definira da je baza zadužna za auto-inkrementalnu generaciju. Doc: https://jakarta.ee/specifications/persistence/3.1/apidocs/jakarta.persistence/jakarta/persistence/generatedvalue
    @Column(name = "user_id")
    private Long id;

    private String name;
    private String surname;

    @Column(name = "email", unique = true)  // Ima puno koristi, no u ovoj implementaciji se koristi samo za definiciju da je email 'UNIQUE'. Doc: https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/column
    private String email;
    private String password;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)  // Definira odnos između entiteta. Doc: https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/onetomany
    private Set<BillingDataEntity> billingData;

    

    @Transient
    public String getRoleType() {
        DiscriminatorValue dv = this.getClass().getAnnotation(DiscriminatorValue.class);
        return dv != null ? dv.value() : "USER";
    }

}