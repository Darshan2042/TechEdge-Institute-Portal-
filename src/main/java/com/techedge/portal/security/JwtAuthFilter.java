package com.techedge.portal.security;

import com.techedge.portal.entity.User;
import com.techedge.portal.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public JwtAuthFilter(
            JwtTokenProvider jwtTokenProvider,
            UserRepository userRepository
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {

            if (!jwtTokenProvider.isTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            Long userId = jwtTokenProvider.extractUserId(token);

            Optional<User> userOptional =
                    userRepository.findById(userId);

            if (userOptional.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            User user = userOptional.get();

            /*
             * Important:
             * Do not trust only the JWT.
             * Check the current database state.
             *
             * This ensures a deactivated user cannot
             * continue using an old valid token.
             */
            if (!Boolean.TRUE.equals(user.getActive())) {
                filterChain.doFilter(request, response);
                return;
            }

            UserPrincipal principal =
                    new UserPrincipal(user);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            principal.getAuthorities()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

        } catch (Exception ignored) {
            // Invalid JWT → continue without authentication
        }

        filterChain.doFilter(request, response);
    }
}