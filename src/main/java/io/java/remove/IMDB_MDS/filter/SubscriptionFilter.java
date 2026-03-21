package io.java.remove.IMDB_MDS.filter;

import io.java.remove.IMDB_MDS.model.SubscriptionHistory;
import io.java.remove.IMDB_MDS.subscription.SubscriptionManagement;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class SubscriptionFilter extends OncePerRequestFilter {

    @Autowired
    private SubscriptionManagement subscriptionManagement;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        String url = request.getRequestURI();
        if (!url.startsWith("/api/v1/search")){
            filterChain.doFilter(request,response);
            return;
        }

        var apiKey = request.getHeader("X-API-KEY");
        var user = subscriptionManagement.resolveUser(apiKey);
        if (user.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("application/json");
            response.getWriter().write("""
                    User is not found
                    """);
            return;
        }
        if (subscriptionManagement.isSubscriptionLimitReached(user.get())) {
            response.setStatus(429);
            return;
        }

        subscriptionManagement.subscriptionAudit(
                new SubscriptionHistory(
                        user.get(),
                        url,
                        LocalDateTime.now()
                )
        );

        filterChain.doFilter(request, response);
    }
}
