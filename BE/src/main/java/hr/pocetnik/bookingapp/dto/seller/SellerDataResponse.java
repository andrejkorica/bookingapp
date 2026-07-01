package hr.pocetnik.bookingapp.dto.seller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SellerDataResponse {
    private String businessName;
    private String oib;
    private String iban;
    private String billingAddress;
}