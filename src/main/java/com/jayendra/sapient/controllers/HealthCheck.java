package com.jayendra.sapient.controllers;


import com.jayendra.sapient.domain.Account;
import com.jayendra.sapient.services.impl.AccountsServiceImpl;
import com.jayendra.sapient.utils.HttpResponseStructs;
import com.jayendra.sapient.utils.JsonHttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;


@Slf4j
@RestController
public class HealthCheck {

    private final DataSource dataSource;
    private final AccountsServiceImpl accountsService;

    public HealthCheck(DataSource dataSource, AccountsServiceImpl accountsService, RedisTemplate redisTemplate) {
        this.dataSource = dataSource;
        this.accountsService = accountsService;
    }


    @GetMapping("/api/health-check")
    public ResponseEntity<JsonHttpResponse> healthCheck(HttpServletRequest request, @AuthenticationPrincipal Account account) {
        final JdbcTemplate restTemplate = new JdbcTemplate(dataSource);

        restTemplate.execute("select 1");
        return HttpResponseStructs.generateJsonResponse("user", 200);
    }
}
