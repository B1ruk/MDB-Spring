package io.java.remove.IMDB_MDS.model;

import java.time.LocalDateTime;

public record SubscriptionHistory(User user,String url,LocalDateTime requestTime) {
}
