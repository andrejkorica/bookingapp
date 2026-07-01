package hr.pocetnik.bookingapp.controller;

import hr.pocetnik.bookingapp.dto.listing.ListingResponse;
import hr.pocetnik.bookingapp.model.ListingEntity;
import hr.pocetnik.bookingapp.model.UserEntity;
import hr.pocetnik.bookingapp.repository.ListingRepository;
import hr.pocetnik.bookingapp.repository.UserRepository;
import hr.pocetnik.bookingapp.service.JwtService;
import hr.pocetnik.bookingapp.service.ListingService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteListingsController {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final JwtService jwtService;
    private final ListingService listingService;

    @PostMapping("/{listingId}/toggle")
    public Map<String, Object> toggleFavorite(
            @PathVariable Long listingId,
            HttpServletRequest request) {
        String email = getEmailFromCookie(request);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ListingEntity listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        boolean isFavorited;

        if (user.getFavoriteListings().contains(listing)) {
            user.getFavoriteListings().remove(listing);
            isFavorited = false;
        } else {
            user.getFavoriteListings().add(listing);
            isFavorited = true;
        }

        userRepository.save(user);

        return Map.of("favorited", isFavorited);
    }

    @GetMapping("/{listingId}")
    public Map<String, Boolean> isFavorited(
            @PathVariable Long listingId,
            HttpServletRequest request) {
        String email = getEmailFromCookie(request);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean favorited = user.getFavoriteListings()
                .stream()
                .anyMatch(listing -> listing.getId().equals(listingId));

        return Map.of("favorited", favorited);
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

    @GetMapping
    public List<ListingResponse> getFavoriteListings(HttpServletRequest request) {
        String email = getEmailFromCookie(request);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getFavoriteListings()
                .stream()
                .map(listingService::mapListingToResponse)
                .toList();
    }
}