package com.hyunwoosing.perturba.common.config;


import com.hyunwoosing.perturba.common.config.props.AuthProps;
import com.hyunwoosing.perturba.common.security.filter.GuestAuthFilter;
import com.hyunwoosing.perturba.common.security.filter.JwtAuthFilter;
import com.hyunwoosing.perturba.domain.auth.web.oauth.CustomOAuth2UserService;
import com.hyunwoosing.perturba.domain.auth.web.oauth.OAuth2AuthFailureHandler;
import com.hyunwoosing.perturba.domain.auth.web.oauth.OAuth2AuthSuccessHandler;
import com.hyunwoosing.perturba.domain.guest.repository.GuestSessionRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter; // 기존
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthSuccessHandler oAuth2AuthSuccessHandler;
    private final OAuth2AuthFailureHandler oAuth2AuthFailureHandler;
    private final GuestSessionRepository guestSessionRepository;
    private final AuthProps authProps;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        var guestAuthFilter = new GuestAuthFilter(guestSessionRepository, authProps);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(m -> m.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, e) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"unauthorized\"}");
                        })
                        .accessDeniedHandler((request, response, e) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"forbidden\"}");
                        })
                )
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers(
                                "/actuator/health",
                                "/v1/auth/refresh",
                                "/v1/auth/logout",
                                "/oauth2/**",
                                "/login/oauth2/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(o -> o
                        .authorizationEndpoint(a -> a.baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(r -> r.baseUri("/login/oauth2/code/*"))
                        .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthSuccessHandler)
                        .failureHandler(oAuth2AuthFailureHandler)
                )
                //JWT가 먼저, 게스트는 그 다음
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(guestAuthFilter, JwtAuthFilter.class)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
