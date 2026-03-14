package io.java.remove.IMDB_MDS.subscription;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.java.remove.IMDB_MDS.model.Subscription;
import io.java.remove.IMDB_MDS.model.SubscriptionHistory;
import io.java.remove.IMDB_MDS.model.SubscriptionType;
import io.java.remove.IMDB_MDS.model.User;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionManagement {

    private static final Logger log =
            LoggerFactory.getLogger(SubscriptionManagement.class);

    private List<SubscriptionHistory> subscriptionHistories;
    private List<User> users;

    @PostConstruct
    public void init() {
        this.subscriptionHistories = new ArrayList<>();
        users = loadUsersFromStorage();

    }


    private List<User> loadUsers() {
        var basicSubscription = new Subscription(SubscriptionType.BASIC, 10);
        var proSubscription = new Subscription(SubscriptionType.PRO, 1000);
        var premiumSubscription = new Subscription(SubscriptionType.PREMIUM, Integer.MAX_VALUE);

        var userBasic = new User("Abebe Kebede", "4e53f5ea-4312-4d1e-a010-0f4aab68bceb","ab1@email.com", basicSubscription);
        var userPro = new User("Abebe Kebede", "e1b65706-56cb-4bd5-a9b9-159502a05321","ab2@email.com", proSubscription);
        var userPremium = new User("Abebe Kebede", "715e5773-a841-462f-92ac-5e758f90d86b", "ab3@email.com",premiumSubscription);

        return List.of(userBasic, userPro, userPremium);
    }

    public Optional<User> resolveUser(String apiKey) {
        return users.stream()
                .filter(user -> user.apiKey().equals(apiKey))
                .findFirst();
    }


    public boolean isSubscriptionLimitReached(User user) {
        var requestCount = totalNumberOfRequest(user);
        log.info("Evaluating subscription for user {} ,with subscription type {} ,current request count {}", user.name(), user.subscription(), requestCount);
        switch (user.subscription().subscriptionType()) {
            case BASIC, PRO -> {
                return requestCount > user.subscription().limit();
            }
            case PREMIUM -> {
                return false;
            }
            default -> throw new IllegalStateException("Unexpected value: " + user.subscription().subscriptionType());
        }
    }

    private long totalNumberOfRequest(User user) {
        return subscriptionHistories.stream().filter(subscriptionHistory -> subscriptionHistory.user().apiKey().equals(user.apiKey()))
                .count();
    }

    public void subscriptionAudit(SubscriptionHistory subscriptionHistory) {
        log.info("subscription request for user {} , on {}", subscriptionHistory.user().name(), subscriptionHistory.requestTime());
        this.subscriptionHistories.add(subscriptionHistory);
    }

    private String generateApiKey(){
        return java.util.UUID.randomUUID().toString();
    }

    private boolean emailExists(String email){
        Optional<User> existingUser = users.stream()
                .filter(user -> user.email().equals(email))
                .findFirst();

       return existingUser.isPresent();
    }

    @PreDestroy
    public void onPreDestroy() {
        saveToStorage();
    }



    private List<User> loadUsersFromStorage() {
        try {
            var userStoragePath = Paths.get(System.getProperty("user.home"), "users.json");
            if (Files.notExists(userStoragePath)) {
                return new ArrayList<>();
            }
            String usersPayloadJson = Files.readString(userStoragePath);
            return List.of(new ObjectMapper().readValue(usersPayloadJson, User[].class));
        } catch (IOException e) {
            log.error("Failed to load users from storage", e);
            return new ArrayList<>();
        }
    }

    private void saveToStorage() {
        try {
            var userStoragePath = Paths.get(System.getProperty("user.home"), "users.json");
            if (Files.notExists(userStoragePath)) {
                Files.createFile(userStoragePath, new FileAttribute<?>[0]);
            }
            String usersPayloadJson = new ObjectMapper().writeValueAsString(users);
            Files.write(userStoragePath, usersPayloadJson.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            log.info("Users saved to storage successfully");
        } catch (IOException e) {
            log.error("Failed to save users to storage", e);
        }
    }


}
