package org.example.ruwaa.Config.JWT;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Api.ApiException;
import org.example.ruwaa.Model.Users;
import org.example.ruwaa.Repository.UsersRepository;
import org.example.ruwaa.Service.AuthService;
import org.example.ruwaa.Service.MyUserDetailService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter
{
    private final JwtUtil jwtUtil;
    private final MyUserDetailService userDetailService;
    private final UsersRepository usersRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        return path.equals("/api/v1/auth/signup/customer")
                || path.equals("/api/v1/auth/signup/expert")
                || path.equals("/api/v1/auth/login")
                || path.startsWith("/public/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // If already authenticated, do not re-authenticate
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // No Authorization header or not Bearer → let the request proceed.
        // This is essential for permitAll endpoints and anonymous access.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        if (token.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing JWT token");
            return;
        }

        try {
            // Extract identity from token. (Typically sub=username or userId)
            String username = jwtUtil.extractUsername(token);

            if (username == null || username.isBlank()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT: missing subject");
                return;
            }

            Optional<Users> userOpt = usersRepository.findUserByUsername(username);
            if (userOpt.isEmpty()) {
                // Do NOT throw application exception from a filter
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT: user not found");
                return;
            }

            Users user = userOpt.get();

            // Validate token integrity/expiry/subject (implement this in JwtUtil)
            if (!jwtUtil.isValid(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            }

            // Authenticate the request
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,               // principal (or user.getUsername())
                            null,               // credentials (never store password here)
                            user.getAuthorities()
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            // Catch ALL token parsing/validation failures and return 401 consistently.
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT");
        }
    }
}
