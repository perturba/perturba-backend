package com.hyunwoosing.perturba.common.security;

import com.hyunwoosing.perturba.domain.guest.entity.GuestSession;
import com.hyunwoosing.perturba.domain.guest.repository.GuestSessionRepository;
import com.hyunwoosing.perturba.domain.user.entity.User;
import com.hyunwoosing.perturba.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ActorResolver {
    private final UserRepository userRepository;
    private final GuestSessionRepository guestSessionRepository;

    public Long currentUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    public Optional<User> currentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Optional.empty();
        }
        return userRepository.findById(userId);
    }

     public Optional<GuestSession> currentGuest(HttpServletRequest request) {
         String guestToken = (String) request.getAttribute("perturba_guest");
         if (guestToken == null){
             return Optional.empty();
         }
         return guestSessionRepository.findByPublicToken(guestToken);
     }
}
