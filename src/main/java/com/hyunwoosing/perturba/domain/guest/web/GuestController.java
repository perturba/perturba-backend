package com.hyunwoosing.perturba.domain.guest.web;

import com.hyunwoosing.perturba.common.api.factory.ApiResponseFactory;
import com.hyunwoosing.perturba.common.api.response.ApiResponse;
import com.hyunwoosing.perturba.common.security.AuthPrincipal;
import com.hyunwoosing.perturba.domain.guest.service.GuestSessionService;
import com.hyunwoosing.perturba.domain.guest.web.dto.CreateGuestSessionResponse;
import com.hyunwoosing.perturba.domain.guest.web.dto.GuestSessionMeResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/guest")
public class GuestController {

    private final GuestSessionService guestSessionService;

    @PostMapping("/session")
    public ApiResponse<CreateGuestSessionResponse> createGuestSession(HttpServletRequest request, HttpServletResponse response) {
        return ApiResponseFactory.success(guestSessionService.issueOrRefresh(request, response));
    }

    @PostMapping("/me")
    public ApiResponse<GuestSessionMeResponse> me(@AuthenticationPrincipal AuthPrincipal userPrincipal) {
        Long guestId = (userPrincipal != null) ? userPrincipal.guestId() : null;
        return ApiResponseFactory.success(guestSessionService.me(guestId));
    }
}
