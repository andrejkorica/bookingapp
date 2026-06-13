package hr.pocetnik.bookingapp.controller;

import hr.pocetnik.bookingapp.model.UserEntity;
import hr.pocetnik.bookingapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AdminController {

    private final UserRepository userRepository;

    @Autowired
    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public List<Map<String, String>> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapUser)
                .toList();
    }

    private Map<String, String> mapUser(UserEntity user) {
        return Map.of(
                "id", user.getId().toString(),
                "name", user.getName(),
                "surname", user.getSurname(),
                "email", user.getEmail(),
                "phoneNumber", user.getPhoneNumber(),
                "role", user.getRole().name()
        );
    }
}