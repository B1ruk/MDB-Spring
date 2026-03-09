package io.java.remove.IMDB_MDS.subscription;

import io.java.remove.IMDB_MDS.model.Subscription;
import io.java.remove.IMDB_MDS.model.SubscriptionHistory;
import io.java.remove.IMDB_MDS.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionManagement {
    private List<Subscription> subscriptions;
    private List<SubscriptionHistory> subscriptionHistories;

    @PostConstruct
    public void init() {
        this.subscriptionHistories = new ArrayList<>();
    }


    public boolean isSubscriptionLimitReached(User user) {
        var requestCount = totalNumberOfRequest(user);
        switch (user.subscription().subscriptionType()) {
            case BASIC,PRO -> {
                return requestCount > user.subscription().limit();
            }
            case PREMIUM -> {
                return false;
            }
            default ->  throw new IllegalStateException("Unexpected value: " + user.subscription().subscriptionType());
        }
    }

    private long totalNumberOfRequest(User user) {
        return subscriptionHistories.stream().filter(subscriptionHistory -> subscriptionHistory.user().apiKey().equals(user.apiKey()))
                .count();
    }

    public void subscriptionAudit(SubscriptionHistory subscriptionHistory) {
        this.subscriptionHistories.add(subscriptionHistory);
    }


}
