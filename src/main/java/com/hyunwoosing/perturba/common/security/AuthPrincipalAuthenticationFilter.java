package com.hyunwoosing.perturba.common.security;

import com.hyunwoosing.perturba.common.config.props.AuthProps;
import com.hyunwoosing.perturba.common.security.jwt.JwtProvider;
import com.hyunwoosing.perturba.domain.guest.entity.GuestSession;
import com.hyunwoosing.perturba.domain.guest.repository.GuestSessionRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class AuthPrincipalAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final GuestSessionRepository guestRepository;
    private final AuthProps authProps;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws IOException, ServletException {

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        Long userId  = resolveUserIdFromAuthorizationBearer(request);
        Long guestId = (userId == null) ? resolveGuestIdFromCookie(request) : null;

        if (userId != null || guestId != null) {
            List<GrantedAuthority> authorities = AuthorityUtils.NO_AUTHORITIES; // 필요시 ROLE_USER 등
            AuthPrincipal principal = new AuthPrincipal(userId, guestId, null, authorities);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(request, response);
    }



    private Long resolveUserIdFromAuthorizationBearer(HttpServletRequest req) {
        String auth = req.getHeader("Authorization");
        if (!StringUtils.hasText(auth) || !auth.startsWith("Bearer ")) return null;

        String token = auth.substring(7);
        try {
            Claims claims = jwtProvider.verify(token);
            String sub = claims.getSubject();
            if (!StringUtils.hasText(sub)) return null;
            try {
                return Long.parseLong(sub);
            } catch (NumberFormatException nfe) {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }


    private Long resolveGuestIdFromCookie(HttpServletRequest req) {
        String cookieName = authProps.guest().cookieName();
        Cookie[] cookies = req.getCookies();
        if (cookies == null)
            return null;

        String token = null;
        for (Cookie c : cookies) {
            if (cookieName.equals(c.getName())) {
                token = c.getValue();
                break;
            }
        }
        if (!StringUtils.hasText(token)) return null;

        Optional<GuestSession> guest = guestRepository.findByPublicToken(token);
        return guest.map(GuestSession::getId).orElse(null);
    }
}
