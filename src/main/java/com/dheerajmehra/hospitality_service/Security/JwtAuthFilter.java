package com.dheerajmehra.hospitality_service.Security;

import com.dheerajmehra.hospitality_service.Service.UserService;
import com.dheerajmehra.hospitality_service.entity.User;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       try {
           String header = request.getHeader("Authorization");
           if (header == null || header.isBlank() || !header.startsWith("Bearer")) {
               filterChain.doFilter(request, response);
               return;
           }
           String token = header.split("Bearer ")[1];
           if (token == null || token.isBlank()) {
               filterChain.doFilter(request, response);
               return;
           }
           Long userId = jwtService.getUserIdFromToken(token);

           if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

               User user = userService.getUserByUserId(userId);
               UsernamePasswordAuthenticationToken authenticationToken =
                       new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

               authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

               SecurityContextHolder.getContext().setAuthentication(authenticationToken);
           }
           filterChain.doFilter(request, response);
       } catch (JwtException ex){
           handlerExceptionResolver.resolveException(request,response,null,ex);
       }
    }
}
