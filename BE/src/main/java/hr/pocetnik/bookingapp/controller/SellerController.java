package hr.pocetnik.bookingapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hr.pocetnik.bookingapp.dto.seller.SellerDataResponse;
import hr.pocetnik.bookingapp.model.SellerDataEntity;
import hr.pocetnik.bookingapp.model.UserEntity;
import hr.pocetnik.bookingapp.repository.UserRepository;
import hr.pocetnik.bookingapp.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/seller")
public class SellerController {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public SellerController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("/me")
    public SellerDataResponse getMySellerData(HttpServletRequest request) {
        String email = getEmailFromCookie(request);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getSellerData() == null) {
            throw new RuntimeException("Seller data not found");
        }

        SellerDataEntity sellerData = user.getSellerData();

        return new SellerDataResponse(
                sellerData.getBusinessName(),
                sellerData.getOib(),
                sellerData.getIban(),
                sellerData.getBillingAddress());
    }

    private String getEmailFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new RuntimeException("Token missing");
        }

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("token")) {
                return jwtService.extractAllClaims(cookie.getValue()).getSubject();
            }
        }

        throw new RuntimeException("Token missing");
    }
}