package kiradev.studio.Eommerce.service;

import kiradev.studio.Eommerce.Enum.UserRole;
import kiradev.studio.Eommerce.Enum.UserStatus;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.repository.UserRepository;
import kiradev.studio.Eommerce.service.Interface.IUserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor for UserService.
     *
     * @param userRepository  the UserRepository instance
     * @param passwordEncoder the PasswordEncoder instance
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Checks if a user with the given email already exists in the database.
     *
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    public boolean isMailExist(String email) {
        return userRepository.findByemail(email).isPresent();
    }

    /**
     * Registers a new user with the provided password and email.
     *
     * @param password the password of the user
     * @param email    the email of the user
     * @return the registered user
     */
    public User registerUser(String password, String email) {
        User user = new User();
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(Instant.now().toString());
        userRepository.save(user);

        return user;
    }

    /**
     * Logs in a user using their username or email and password.
     *
     * @param usernameOrEmail the username or email of the user
     * @param password        the password of the user
     * @return the logged-in user
     */
    public User loginUser(String usernameOrEmail, String password) {
        User user = userRepository.findByname(usernameOrEmail)
                .orElseGet(() -> userRepository.findByemail(usernameOrEmail).orElse(null));

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        } else {
            throw new RuntimeException("Invalid username/email or password");
        }
    }

    /**
     * Updates a user's information based on the provided email.
     *
     * @param email       the email of the user to update
     * @param userDetails the new user details to update
     * @return the updated user
     */
    public User updateUser(String email, User userDetails) {
        return userRepository.findByemail(email).map(user -> {
            if (userDetails.getName() != null && !userDetails.getName().isEmpty()) {
                user.setName(userDetails.getName());
            }
            if (userDetails.getEmail() != null && !userDetails.getEmail().isEmpty()) {
                user.setEmail(userDetails.getEmail());
            }
            if (userDetails.getAddress() != null && !userDetails.getAddress().isEmpty()) {
                user.setAddress(userDetails.getAddress());
            }
            if (userDetails.getPhone() != null && !userDetails.getPhone().isEmpty()) {
                user.setPhone(userDetails.getPhone());
            }
            if (userDetails.getRole() != null) {
                user.setRole(userDetails.getRole());
            }

            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return an Optional containing the user if found, or empty if not found
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByname(username);
    }

    /**
     * Retrieves a user by their email.
     *
     * @param email the email of the user to retrieve
     * @return an Optional containing the user if found, or empty if not found
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByemail(email);
    }

    /**
     * Retrieves a user by their phone number.
     *
     * @param phoneNumber the phone number of the user to retrieve
     * @return an Optional containing the user if found, or empty if not found
     */
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByphone(phoneNumber);
    }

    /**
     * Retrieves a user by their address.
     *
     * @param address the address of the user to retrieve
     * @return an Optional containing the user if found, or empty if not found
     */
    public Optional<User> findByAddress(String address) {
        return userRepository.findByaddress(address);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return an Optional containing the user if found, or empty if not found
     */
    public Optional<User> findById(UUID id) {
        return userRepository.findByID(id);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     */
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a list of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Checks if the user has the specified role.
     *
     * @param user the user to check
     * @param role the role to check against
     * @return true if the user has the specified role, false otherwise
     */
    public boolean hasPermission(User user, UserRole role) {
        return user.getRole() != role;
    }

    /**
     * Retrieves a paginated list of users sorted by the specified field.
     *
     * @param page the page number to retrieve (0-indexed)
     * @param size the number of users per page
     * @param sort the field to sort by (e.g., "name", "email")
     * @return a list of users for the specified page
     */
    public List<User> getAllUsersPage(int page, int size, String sort) {
        return userRepository.findAll(PageRequest.of(page, size, Sort.by(sort))).getContent();
    }

    /**
     * Updates the user's image based on the provided email.
     *
     * @param email the email of the user to update
     * @param image the new image to set
     * @return the updated user
     */
    public User updateImage(String email, byte[] image) {
        return userRepository.findByemail(email).map(user -> {
            user.setImage(image);
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
