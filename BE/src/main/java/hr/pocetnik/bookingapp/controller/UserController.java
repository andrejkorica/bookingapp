package hr.pocetnik.bookingapp.controller;

import hr.pocetnik.bookingapp.exception.TokenNotFoundException;
import hr.pocetnik.bookingapp.exception.UserNotFoundException;
import hr.pocetnik.bookingapp.model.UserEntity;
import hr.pocetnik.bookingapp.repository.UserRepository;
import hr.pocetnik.bookingapp.service.JwtService;
import hr.pocetnik.bookingapp.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class UserController {

        private final UserService userService;
        private final JwtService jwtService;
        private final UserRepository userRepository;

        @Autowired
        public UserController(UserService userService, JwtService jwtService, UserRepository userRepository) {
                this.userService = userService;
                this.jwtService = jwtService;
                this.userRepository = userRepository;
        }

        @PostMapping("/register")
        public ResponseEntity<Map<String, String>> registerUser(@RequestBody Map<String, String> userMap) {
                String name = userMap.get("name");
                String surname = userMap.get("surname");
                String email = userMap.get("email");
                String password = userMap.get("password");

                Objects.requireNonNull(name, "First name must not be null");
                Objects.requireNonNull(surname, "Surname must not be null");
                Objects.requireNonNull(email, "Email must not be null");
                Objects.requireNonNull(password, "Password must not be null");

                UserEntity user = userService.registerUser(name, surname, email, password);
                String token = jwtService.generateToken(user);

                ResponseCookie cookie = ResponseCookie.from("token", token)
                                .httpOnly(true)
                                .secure(false)
                                .path("/")
                                .maxAge(Duration.ofHours(1))
                                .sameSite("Strict")
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(Map.of("message", "Login successful"));
        }

        @PostMapping("/login")
        public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, String> userMap) {
                String email = userMap.get("email");
                String password = userMap.get("password");

                Objects.requireNonNull(email, "Email must not be null");
                Objects.requireNonNull(password, "Password must not be null");

                UserEntity user = userService.loginUser(email, password);
                String token = jwtService.generateToken(user);

                ResponseCookie cookie = ResponseCookie.from("token", token)
                                .httpOnly(true)
                                .secure(false)
                                .path("/")
                                .maxAge(Duration.ofHours(1))
                                .sameSite("Strict")
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(Map.of("message", "Login successful", "token", token));
        }

        @PostMapping("/logout")
        public ResponseEntity<Void> logoutUser() {
                ResponseCookie cookie = ResponseCookie.from("token", "")
                                .httpOnly(true)
                                .secure(false)
                                .path("/")
                                .maxAge(0)
                                .sameSite("Strict")
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .build();
        }

        @PostMapping("/update")
        public ResponseEntity<Map<String, String>> updateCurrentUser(
                        @CookieValue(name = "token", required = false) String token,
                        @RequestBody Map<String, String> userMap) {
                if (token == null || token.isEmpty()) {
                        throw new TokenNotFoundException();
                }

                Claims claims = jwtService.extractAllClaims(token);
                String currentEmail = claims.getSubject();

                UserEntity updatedUser = userService.updateUser(
                                currentEmail,
                                userMap.get("name"),
                                userMap.get("surname"),
                                userMap.get("email"));

                return ResponseEntity.ok(Map.of(
                                "email", updatedUser.getEmail(),
                                "name", updatedUser.getName(),
                                "surname", updatedUser.getSurname(),
                                "role", claims.get("Role", String.class)));
        }

        @GetMapping("/me")
        public ResponseEntity<Map<String, String>> getCurrentUser(
                        @CookieValue(name = "token", required = false) String token) {
                if (token == null || token.isEmpty()) {
                        throw new TokenNotFoundException();
                }

                Claims claims = jwtService.extractAllClaims(token);
                String email = claims.getSubject();
                String role = claims.get("Role", String.class);

                UserEntity user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new UserNotFoundException(email));

                Map<String, String> userData = Map.of(
                                "email", email,
                                "name", user.getName(),
                                "surname", user.getSurname(),
                                "role", role);
                return ResponseEntity.ok(userData);
        }
}