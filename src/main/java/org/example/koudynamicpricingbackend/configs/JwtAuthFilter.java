package org.example.koudynamicpricingbackend.configs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // If header is missing or does not start with Bearer -> skip authentication, proceed with the chain
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7)
                .replaceAll("[\\n\\r\\t ]", "")       // basic whitespace cleaning
                .replace("\u00A0", "");        // non-breaking space cleaning

        // You can keep debug logs if desired, but it is not recommended for production
        System.out.println("RAW HEADER = [" + authHeader + "]");
        System.out.println("EXTRACTED TOKEN = [" + token + "]");
        System.out.println("TOKEN LENGTH = " + token.length());

        System.out.println("*******************************************");
        for (char c : token.toCharArray()) {
            if (Character.isWhitespace(c)) {
                System.out.println("WHITESPACE CHAR CODE: " + (int) c);
            }
        }

        try {

            // 1) Is the token valid? (signature + expiration, etc.)
            if (!jwtUtils.validateToken(token)) {
                unauthorized(response, "Invalid token");
                return;
            }

            // 2) We DO NOT check for blacklist via JTI anymore
            //    Since the system is fully stateless and we do not use a token revocation mechanism.

            // 3) Extract email and roles from the token
            String email = jwtUtils.getEmailFromToken(token);

            List<GrantedAuthority> authorities = jwtUtils.getRolesFromToken(token).stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // User principal (you can also add userId if desired)
            JwtUserPrincipal principal = new JwtUserPrincipal(email, authorities);

            // 4) Set authentication in the SecurityContext
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception ex) {
            // Any error during parse, validate, claim extraction, etc. -> 401
            unauthorized(response, "Invalid or expired token");
            return;
        }

        // 5) Continue the request
        filterChain.doFilter(request, response);
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }

}