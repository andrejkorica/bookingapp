package hr.pocetnik.bookingapp.service;

import java.util.List;

import hr.pocetnik.bookingapp.model.SellerRequestEntity;

public interface SellerRequestService {

    SellerRequestEntity createSellerRequest(
            String email,
            String businessName,
            String oib,
            String iban,
            String billingAddress,
            String requestText
    );
    List<SellerRequestEntity> getAllSellerRequests();

    SellerRequestEntity approveSellerRequest(Long requestId);

    SellerRequestEntity rejectSellerRequest(Long requestId);
}