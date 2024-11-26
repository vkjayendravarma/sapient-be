package com.jayendra.sapient.config;

import com.jayendra.sapient.services.impl.AccountsServiceImpl;
import com.jayendra.sapient.services.impl.AuthProviderService;
import com.jayendra.sapient.services.impl.JwtServiceImpl;
import com.jayendra.sapient.services.impl.RedisService;
import com.jayendra.sapient.utils.HttpResponseStructs;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    JwtServiceImpl jwtService;

    @Autowired
    ApplicationContext context;

    RedisService redisService;

    public JwtFilter(RedisService redisService) {
        this.redisService = redisService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String userName = null;
        String token = null;
        Object cacheCheck = null;

        if (authHeader == null){
            filterChain.doFilter(request, response);
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                cacheCheck = redisService.getKey("auth-" + token);
                System.out.println("validate token" + cacheCheck);
                if (cacheCheck != null) {
                    System.out.println("validate token in cache");
                    userName = String.valueOf(cacheCheck);
                } else {
                    userName = jwtService.extractUserName(token);
                }
            } catch (Exception e) {
                HttpResponseStructs.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "token expired");
                return;
            }

        }


        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = context.getBean(AuthProviderService.class).loadUserByUsername(userName);
            if (cacheCheck !=null || jwtService.validateToken(token, userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
