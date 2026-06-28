package hr.pocetnik.bookingapp.dto.booking;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingUnitRequest {

    private String unitType;

    private Integer quantity;

}