package hr.pocetnik.bookingapp.dto.seller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class SellerStatisticsResponse {

    private BigDecimal totalEarnings;
    private Long totalListings;
    private Long activeBookings;

}