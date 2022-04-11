package com.authenticationswagger.authenticationswagger.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

  private static final Logger logger = LoggerFactory.getLogger(AccessDeniedHandler.class);

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
    logger.error("Access Denied error: {}", e.getMessage());

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

    final Map<String, Object> body = new HashMap<>();
    body.put("status", HttpServletResponse.SC_FORBIDDEN);
    body.put("error", "Access Denied");
    body.put("message", e.getMessage());
    body.put("path", request.getServletPath());

    final ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(response.getOutputStream(), body);
  }

}
