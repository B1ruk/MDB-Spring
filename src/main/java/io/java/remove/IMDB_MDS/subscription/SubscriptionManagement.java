package io.java.remove.IMDB_MDS.subscription;

import io.java.remove.IMDB_MDS.model.SubscriptionHistory;
import io.java.remove.IMDB_MDS.model.User;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionManagement {

    private static final Logger log =
            LoggerFactory.getLogger(SubscriptionManagement.class);

    private List<SubscriptionHistory> subscriptionHistories;
    private final UserService userService;

    public SubscriptionManagement(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        this.subscriptionHistories = new ArrayList<>();
    }

    public Optional<User> resolveUser(String apiKey) {
        return userService.resolveUser(apiKey);
    }


    public boolean isSubscriptionLimitReached(User user) {
        var requestCount = totalNumberOfRequest(user);
        log.info("Evaluating subscription for user {} ,with subscription type {} ,current request count {}", user.name(), user.subscription(), requestCount);
        switch (user.subscription().subscriptionType()) {
            case BASIC, PRO -> {
                return requestCount > user.subscription().limit();
            }
            case PREMIUMs -> {
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
        return userService.findByEmail(email).isPresent();
    }

    @PreDestroy
    public void onPreDestroy() {
        // User storage is handled by UserService; only persist if needed
    }


}
