package hr.pocetnik.bookingapp.service.implementation;

import hr.pocetnik.bookingapp.exception.InvalidCredentialException;
import hr.pocetnik.bookingapp.exception.InvalidEmailFormatException;
import hr.pocetnik.bookingapp.exception.UserAlreadyExistsException;
import hr.pocetnik.bookingapp.exception.UserNotFoundException;
import hr.pocetnik.bookingapp.model.UserEntity;
import hr.pocetnik.bookingapp.repository.UserRepository;
import hr.pocetnik.bookingapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    @Override
    public UserEntity registerUser(String name, String surname, String email, String password, String phoneNumber) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String modifiedEmail = email.toLowerCase(Locale.ROOT);

        if (!EMAIL_PATTERN.matcher(modifiedEmail).matches()) {
            throw new InvalidEmailFormatException();
        }

        if (userRepository.existsByEmail(modifiedEmail)) {
            throw new UserAlreadyExistsException(email);
        }

        UserEntity newUser = new UserEntity();
        newUser.setName(name);
        newUser.setSurname(surname);
        newUser.setEmail(modifiedEmail);
        newUser.setPassword(hashedPassword);
        newUser.setPhoneNumber(phoneNumber);

        return userRepository.save(newUser);
    }

    @Override
    public UserEntity loginUser(String email, String password) {
        String modifiedEmail = email.toLowerCase(Locale.ROOT);

        if (!EMAIL_PATTERN.matcher(modifiedEmail).matches()) {
            throw new InvalidEmailFormatException();
        }

        UserEntity user = userRepository.findByEmail(modifiedEmail)
                .orElseThrow(InvalidCredentialException::new);

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new InvalidCredentialException();
        }

        return user;
    }

    @Override
    public UserEntity updateUser(String currentEmail, String name, String surname, String email, String phoneNumber) {
        UserEntity user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserNotFoundException(currentEmail));

        if (name != null) {
            user.setName(name);
        }

        if (surname != null) {
            user.setSurname(surname);
        }

        if (email != null) {
            String modifiedEmail = email.toLowerCase(Locale.ROOT);

            if (!EMAIL_PATTERN.matcher(modifiedEmail).matches()) {
                throw new InvalidEmailFormatException();
            }

            if (!modifiedEmail.equals(currentEmail) && userRepository.existsByEmail(modifiedEmail)) {
                throw new UserAlreadyExistsException(email);
            }

            user.setEmail(modifiedEmail);
        }

        if (phoneNumber != null) {
            user.setPhoneNumber(phoneNumber);
        }

        return userRepository.save(user);
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }
}