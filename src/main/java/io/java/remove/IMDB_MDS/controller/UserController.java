package io.java.remove.IMDB_MDS.controller;

import io.java.remove.IMDB_MDS.model.SubscriptionType;
import io.java.remove.IMDB_MDS.model.User;
import io.java.remove.IMDB_MDS.subscription.DuplicateUserException;
import io.java.remove.IMDB_MDS.subscription.UserNotFoundException;
import io.java.remove.IMDB_MDS.subscription.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        SubscriptionType type;
        try {
            type = parseSubscriptionType(request.subscriptionType());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid subscriptionType: " + request.subscriptionType());
        }

        try {
            User user = userService.createUser(request.name(), request.email(), type);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (DuplicateUserException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/subscription")
    public ResponseEntity<?> updateSubscription(@RequestBody UpdateSubscriptionRequest request) {
        SubscriptionType type;
        try {
            type = parseSubscriptionType(request.subscriptionType());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid subscriptionType: " + request.subscriptionType());
        }

        try {
            User updated = userService.updateUserSubscription(request.email(), type);
            return ResponseEntity.ok(updated);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public List<User> listUsers() {
        return userService.getAllUsers();
    }

    // Accepts common variants like "BASICS" or "basic" and maps them to SubscriptionType
    private SubscriptionType parseSubscriptionType(String input) {
        if (input == null) throw new IllegalArgumentException("subscriptionType is required");
        String normalized = input.trim().toUpperCase();
        // accept trailing 'S' (BASICS -> BASIC)
        if (normalized.endsWith("S")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        switch (normalized) {
            case "BASIC", "BASICS" -> {
                return SubscriptionType.BASIC;
            }
            case "PRO", "PROFESSIONAL" -> {
                return SubscriptionType.PRO;
            }
            case "PREMIUM" -> {
                return SubscriptionType.PREMIUMs;
            }
            default -> {
                // Try exact enum parsing as a last resort
                try {
                    return SubscriptionType.valueOf(normalized);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Unknown subscription type: " + input);
                }
            }
        }
    }
}
