package hr.pocetnik.bookingapp.service.implementation;

import hr.pocetnik.bookingapp.enums.Role;
import hr.pocetnik.bookingapp.enums.SellerRequestStatus;
import hr.pocetnik.bookingapp.exception.UserNotFoundException;
import hr.pocetnik.bookingapp.model.SellerDataEntity;
import hr.pocetnik.bookingapp.model.SellerRequestEntity;
import hr.pocetnik.bookingapp.model.UserEntity;
import hr.pocetnik.bookingapp.repository.SellerDataRepository;
import hr.pocetnik.bookingapp.repository.SellerRequestRepository;
import hr.pocetnik.bookingapp.repository.UserRepository;
import hr.pocetnik.bookingapp.service.SellerRequestService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SellerRequestServiceImpl implements SellerRequestService {

    private final UserRepository userRepository;
    private final SellerDataRepository sellerDataRepository;
    private final SellerRequestRepository sellerRequestRepository;

    @Autowired
    public SellerRequestServiceImpl(
            UserRepository userRepository,
            SellerDataRepository sellerDataRepository,
            SellerRequestRepository sellerRequestRepository) {
        this.userRepository = userRepository;
        this.sellerDataRepository = sellerDataRepository;
        this.sellerRequestRepository = sellerRequestRepository;
    }

    @Override
    public SellerRequestEntity createSellerRequest(
            String email,
            String businessName,
            String oib,
            String iban,
            String billingAddress,
            String requestText) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (sellerRequestRepository.existsByUserAndStatus(user, SellerRequestStatus.PENDING)
                || sellerRequestRepository.existsByUserAndStatus(user, SellerRequestStatus.UPDATE)) {
            throw new RuntimeException("You already have a pending seller request.");
        }

        SellerRequestEntity request = new SellerRequestEntity();

        request.setUser(user);
        request.setBusinessName(businessName);
        request.setOib(oib);
        request.setIban(iban);
        request.setBillingAddress(billingAddress);
        request.setRequestText(requestText);

        if (user.getSellerData() != null) {
            request.setStatus(SellerRequestStatus.UPDATE);
        } else {
            request.setStatus(SellerRequestStatus.PENDING);
        }

        return sellerRequestRepository.save(request);
    }

    @Override
    public List<SellerRequestEntity> getAllSellerRequests() {
        return sellerRequestRepository.findAll();
    }

    @Override
    public SellerRequestEntity approveSellerRequest(Long requestId) {
        SellerRequestEntity request = sellerRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Seller request not found."));

        UserEntity user = request.getUser();

        SellerDataEntity sellerData = user.getSellerData();

        if (sellerData == null) {
            sellerData = new SellerDataEntity();
            sellerData.setUser(user);
            user.setSellerData(sellerData);
        }

        sellerData.setBusinessName(request.getBusinessName());
        sellerData.setOib(request.getOib());
        sellerData.setIban(request.getIban());
        sellerData.setBillingAddress(request.getBillingAddress());

        sellerDataRepository.save(sellerData);

        user.setRole(Role.SELLER);
        userRepository.save(user);

        request.setStatus(SellerRequestStatus.APPROVED);

        return sellerRequestRepository.save(request);
    }

    @Override
    public SellerRequestEntity rejectSellerRequest(Long requestId) {
        SellerRequestEntity request = sellerRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Seller request not found."));

        request.setStatus(SellerRequestStatus.REJECTED);

        return sellerRequestRepository.save(request);
    }
}