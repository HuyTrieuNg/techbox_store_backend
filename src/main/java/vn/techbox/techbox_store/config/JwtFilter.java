package vn.techbox.techbox_store.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.techbox.techbox_store.user.service.AuthService;
import vn.techbox.techbox_store.user.service.MyUserDetailService;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final AuthService authService;
    private final ApplicationContext context;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtFilter(AuthService authService, ApplicationContext context) {
        this.authService = authService;
        this.context = context;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = authService.extractUserName(token);
            } catch (SignatureException e) {
                logger.error("Invalid JWT signature: {}", e.getMessage());
                sendErrorResponse(response, "INVALID_SIGNATURE", "Invalid JWT signature", false);
                return;
            } catch (ExpiredJwtException e) {
                logger.error("JWT token expired: {}", e.getMessage());
                sendErrorResponse(response, "TOKEN_EXPIRED", "Access token expired. Please use refresh token to get a new access token.", true);
                return;
            } catch (MalformedJwtException e) {
                logger.error("Malformed JWT token: {}", e.getMessage());
                sendErrorResponse(response, "MALFORMED_TOKEN", "Malformed JWT token", false);
                return;
            } catch (Exception e) {
                logger.error("JWT processing error: {}", e.getMessage());
                sendErrorResponse(response, "JWT_ERROR", "JWT processing error", false);
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = context.getBean(MyUserDetailService.class).loadUserByUsername(username);

                if (authService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                logger.error("Error validating token: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String errorCode, String message, boolean requiresRefresh) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", errorCode);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());
        errorResponse.put("requiresRefresh", requiresRefresh);

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
