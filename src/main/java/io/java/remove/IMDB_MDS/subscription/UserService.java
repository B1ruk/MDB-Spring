package io.java.remove.IMDB_MDS.subscription;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.java.remove.IMDB_MDS.model.Subscription;
import io.java.remove.IMDB_MDS.model.SubscriptionType;
import io.java.remove.IMDB_MDS.model.User;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private List<User> users = new ArrayList<>();

    // Make storage path configurable. Default to project resources folder so users.json lives under src/main/resources/data/
    @Value("${users.storage.path:./src/main/resources/data/users.json}")
    private String usersStoragePath;

    @PostConstruct
    public void init() {
        this.users = loadUsersFromStorage();
    }

    public List<User> getAllUsers() {
        return users;
    }

    public Optional<User> findByEmail(String email) {
        return users.stream()
                .filter(u -> u.email().equalsIgnoreCase(email))
                .findFirst();
    }

    public Optional<User> resolveUser(String apiKey) {
        return users.stream()
                .filter(u -> u.apiKey().equals(apiKey))
                .findFirst();
    }

    public User createUser(String name, String email, SubscriptionType type) {
        if (findByEmail(email).isPresent()) {
            throw new DuplicateUserException("User already exists with email: " + email);
        }

        var subscription = switch (type) {
            case BASIC -> new Subscription(SubscriptionType.BASIC, 10);
            case PRO -> new Subscription(SubscriptionType.PRO, 1000);
            case PREMIUMs -> new Subscription(SubscriptionType.PREMIUMs, Integer.MAX_VALUE);
        };

        var user = new User(name, generateApiKey(), email, subscription);
        users.add(user);
        saveToStorage();
        log.info("Created user {} with email {}", name, email);
        return user;
    }

    public User updateUserSubscription(String email, SubscriptionType type) {
        var opt = findByEmail(email);
        if (opt.isEmpty()) {
            throw new UserNotFoundException("User does not exist with email: " + email);
        }

        var existing = opt.get();
        var subscription = switch (type) {
            case BASIC -> new Subscription(SubscriptionType.BASIC, 10);
            case PRO -> new Subscription(SubscriptionType.PRO, 1000);
            case PREMIUMs -> new Subscription(SubscriptionType.PREMIUMs, Integer.MAX_VALUE);
        };

        var updated = new User(existing.name(), existing.apiKey(), existing.email(), subscription);
        int idx = users.indexOf(existing);
        if (idx >= 0) {
            users.set(idx, updated);
            saveToStorage();
            log.info("Updated subscription for user {} to {}", existing.email(), type);
            return updated;
        }

        throw new IllegalStateException("Failed to update user subscription for " + email);
    }

    private String generateApiKey() {
        return UUID.randomUUID().toString();
    }

    private List<User> loadUsersFromStorage() {
        try {
            Path userStoragePath = Paths.get(usersStoragePath);
            // Ensure parent directory exists
            if (userStoragePath.getParent() != null) {
                Files.createDirectories(userStoragePath.getParent());
            }
            if (Files.notExists(userStoragePath)) {
                // create an empty JSON array file
                Files.writeString(userStoragePath, "[]", StandardOpenOption.CREATE);
                return new ArrayList<>();
            }
            String usersPayloadJson = Files.readString(userStoragePath);
            // mapper.readValue returns an array; List.of(...) would produce an immutable list
            // which causes users.add(...) to throw UnsupportedOperationException. Wrap it
            // in a mutable ArrayList so callers can add/update users safely.
            User[] arr = mapper.readValue(usersPayloadJson, User[].class);
            return new ArrayList<>(List.of(arr));
        } catch (IOException e) {
            log.error("Failed to load users from storage", e);
            return new ArrayList<>();
        }
    }

    private void saveToStorage() {
        try {
            Path userStoragePath = Paths.get(usersStoragePath);
            if (userStoragePath.getParent() != null) {
                Files.createDirectories(userStoragePath.getParent());
            }
            String usersPayloadJson = mapper.writeValueAsString(users);
            Files.write(userStoragePath, usersPayloadJson.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            log.info("Users saved to storage successfully: {}", userStoragePath.toAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to save users to storage", e);
        }
    }
}
