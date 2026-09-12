package com.example.demo.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.entities.Role;
import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.AuthService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class AuthenticationFilter
        extends OncePerRequestFilter {


    private static final Logger logger =
            LoggerFactory.getLogger(
                    AuthenticationFilter.class
            );


    private final AuthService authService;

    private final UserRepository userRepository;


    public AuthenticationFilter(
            AuthService authService,
            UserRepository userRepository) {

        this.authService = authService;
        this.userRepository = userRepository;
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {


        String requestURI =
                request.getRequestURI();


        logger.info(
                "Request URI: {}",
                requestURI
        );


        // =====================================================
        // PUBLIC ENDPOINTS
        // =====================================================

        if (
                requestURI.equals(
                        "/api/users/register"
                )

                ||

                requestURI.equals(
                        "/api/auth/login"
                )

                ||

                requestURI.equals(
                        "/api/products"
                )

                ||

                requestURI.startsWith(
                        "/api/products/"
                )

                ||

                requestURI.startsWith(
                        "/api/categories"
                )

                ||

                request.getMethod()
                        .equalsIgnoreCase(
                                "OPTIONS"
                        )
        ) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        // =====================================================
        // GET TOKEN FROM HEADER
        // =====================================================

        String token =
                getTokenFromHeader(
                        request
                );


        // =====================================================
        // GET TOKEN FROM COOKIE
        // =====================================================

        if (token == null) {

            token =
                    getAuthTokenFromCookies(
                            request
                    );
        }


        // =====================================================
        // TOKEN MISSING
        // =====================================================

        if (token == null) {

            response.setStatus(
                    HttpServletResponse
                            .SC_UNAUTHORIZED
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(
                    "{\"error\":\"Authentication token missing\"}"
            );

            return;
        }


        // =====================================================
        // TOKEN INVALID
        // =====================================================

        if (!authService.validateToken(token)) {

            SecurityContextHolder
                    .clearContext();


            response.setStatus(
                    HttpServletResponse
                            .SC_UNAUTHORIZED
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(
                    "{\"error\":\"Invalid or expired token\"}"
            );

            return;
        }


        // =====================================================
        // EXTRACT USERNAME
        // =====================================================

        String username =
                authService.extractUsername(
                        token
                );


        // =====================================================
        // FIND USER
        // =====================================================

        Optional<User> userOptional =
                userRepository.findByUsername(
                        username
                );


        if (userOptional.isEmpty()) {

            SecurityContextHolder
                    .clearContext();


            response.setStatus(
                    HttpServletResponse
                            .SC_UNAUTHORIZED
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(
                    "{\"error\":\"User not found\"}"
            );

            return;
        }


        // =====================================================
        // AUTHENTICATED USER
        // =====================================================

        User authenticatedUser =
                userOptional.get();


        Role role =
                authenticatedUser.getRole();


        logger.info(
                "Authenticated User: {}, Role: {}",
                authenticatedUser.getUsername(),
                role
        );


        // =====================================================
        // AUTHORITY
        // =====================================================

        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(
                        "ROLE_" + role.name()
                );


        // =====================================================
        // AUTHENTICATION
        // =====================================================

        UsernamePasswordAuthenticationToken
                authentication =
                new UsernamePasswordAuthenticationToken(
                        authenticatedUser,
                        null,
                        Collections.singletonList(
                                authority
                        )
                );


        SecurityContextHolder
                .getContext()
                .setAuthentication(
                        authentication
                );


        // =====================================================
        // MAKE USER AVAILABLE TO CONTROLLER
        // =====================================================

        request.setAttribute(
                "authenticatedUser",
                authenticatedUser
        );


        // =====================================================
        // CONTINUE
        // =====================================================

        filterChain.doFilter(
                request,
                response
        );
    }


    // =========================================================
    // HEADER TOKEN
    // =========================================================

    private String getTokenFromHeader(
            HttpServletRequest request) {

        String authorizationHeader =
                request.getHeader(
                        "Authorization"
                );


        if (
                authorizationHeader != null
                &&
                authorizationHeader.startsWith(
                        "Bearer "
                )
        ) {

            return authorizationHeader
                    .substring(7);
        }


        return null;
    }


    // =========================================================
    // COOKIE TOKEN
    // =========================================================

    private String getAuthTokenFromCookies(
            HttpServletRequest request) {

        Cookie[] cookies =
                request.getCookies();


        if (cookies == null) {

            return null;
        }


        return Arrays.stream(cookies)

                .filter(
                        cookie ->
                                "authToken"
                                        .equals(
                                                cookie.getName()
                                        )
                )

                .map(
                        Cookie::getValue
                )

                .findFirst()

                .orElse(null);
    }
}