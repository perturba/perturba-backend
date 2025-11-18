package com.hyunwoosing.perturba.domain.guest.web;

import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.common.security.AuthPrincipal;
import com.hyunwoosing.perturba.domain.guest.web.dto.CreateGuestSessionResponse;
import com.hyunwoosing.perturba.domain.guest.web.dto.GuestSessionMeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/guest")
public class GuestController {

    @PostMapping("/session")
    public ApiResponse<CreateGuestSessionResponse> createGuestSession() {
        return null;
    }

    @PostMapping("/me")
    public ApiResponse<GuestSessionMeResponse> me(@AuthenticationPrincipal AuthPrincipal userPrincipal) {
        Long guestId = (userPrincipal != null) ? userPrincipal.guestId() : null;
        return null;
    }
}
