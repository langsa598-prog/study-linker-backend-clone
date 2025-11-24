package com.study.service.security;

import com.study.service.config.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                   CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        System.out.println(">>> Authorization header = " + header);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            System.out.println(">>> token = " + token);

            boolean valid = jwtTokenProvider.validateToken(token);
            System.out.println(">>> jwt validateToken = " + valid);

            if (valid && SecurityContextHolder.getContext().getAuthentication() == null) {

                String username = jwtTokenProvider.getUsername(token);
                System.out.println(">>> jwt username = " + username);

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                System.out.println(">>> loaded userDetails class = " +
                        userDetails.getClass().getName());

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println(">>> SecurityContext setAuthentication 완료");
            } else {
                System.out.println(">>> 토큰이 유효하지 않거나, 이미 Authentication 이 존재함");
            }
        } else {
            System.out.println(">>> Authorization 헤더 없음 또는 Bearer 로 시작 안 함");
        }

        filterChain.doFilter(request, response);
    }
}