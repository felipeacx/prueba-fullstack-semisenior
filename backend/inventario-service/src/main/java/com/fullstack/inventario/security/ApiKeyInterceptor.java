package com.fullstack.inventario.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    @Value("${app.api.key:secret-key-inventario}")
    private String apiKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();

        String path = contextPath != null && !contextPath.isEmpty() && uri.startsWith(contextPath)
            ? uri.substring(contextPath.length())
            : uri;

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        if (path.startsWith("/h2-console") ||
            path.startsWith("/h2-") ||
            path.startsWith("/h2/") ||
            path.startsWith("/actuator") ||
            path.startsWith("/swagger-ui") ||
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/webjars")) {
            return true;
        }

        String headerApiKey = request.getHeader("X-API-Key");

        if (headerApiKey == null || !headerApiKey.equals(apiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"API Key no valida o ausente\"}");
            return false;
        }

        return true;
    }
}

