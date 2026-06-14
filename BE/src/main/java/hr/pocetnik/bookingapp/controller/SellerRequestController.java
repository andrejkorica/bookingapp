package hr.pocetnik.bookingapp.controller;

import hr.pocetnik.bookingapp.exception.TokenNotFoundException;
import hr.pocetnik.bookingapp.model.SellerRequestEntity;
import hr.pocetnik.bookingapp.service.JwtService;
import hr.pocetnik.bookingapp.service.SellerRequestService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/seller-requests")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class SellerRequestController {

    private final SellerRequestService sellerRequestService;
    private final JwtService jwtService;

    @Autowired
    public SellerRequestController(
            SellerRequestService sellerRequestService,
            JwtService jwtService
    ) {
        this.sellerRequestService = sellerRequestService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public Map<String, String> createSellerRequest(
            @CookieValue(name = "token", required = false) String token,
            @RequestBody Map<String, String> body
    ) {
        if (token == null || token.isEmpty()) {
            throw new TokenNotFoundException();
        }

        Claims claims = jwtService.extractAllClaims(token);
        String email = claims.getSubject();

        SellerRequestEntity request = sellerRequestService.createSellerRequest(
                email,
                body.get("businessName"),
                body.get("oib"),
                body.get("iban"),
                body.get("address"),
                body.get("requestText")
        );

        return Map.of(
                "message", "Seller request created successfully.",
                "requestId", request.getId().toString(),
                "status", request.getStatus().name()
        );
    }
}