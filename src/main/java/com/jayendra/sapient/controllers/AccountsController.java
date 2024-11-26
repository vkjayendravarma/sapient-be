package com.jayendra.sapient.controllers;

import com.jayendra.sapient.domain.Account;
import com.jayendra.sapient.domain.dto.AccountDto;
import com.jayendra.sapient.mappers.Mapper;
import com.jayendra.sapient.requestPayloads.LoginPayload;
import com.jayendra.sapient.services.AccountsService;
import com.jayendra.sapient.services.impl.JwtServiceImpl;
import com.jayendra.sapient.utils.HttpResponseStructs;
import com.jayendra.sapient.utils.JsonHttpResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/accounts")
public class AccountsController {

    public Mapper<Account, AccountDto> accountsMapper;
    public AccountsService accountsService;
    public AuthenticationManager authenticationManager;
    public JwtServiceImpl jwtService;

    public AccountsController(Mapper<Account, AccountDto> accountsMapper, AccountsService accountsService, AuthenticationManager authenticationManager, JwtServiceImpl jwtService) {
        this.accountsMapper = accountsMapper;
        this.accountsService = accountsService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<JsonHttpResponse> createAccount(@RequestBody AccountDto accountDto) {
        Account account = accountsMapper.mapFrom(accountDto);
        try {
            Account accountNew = accountsService.createAccount(account);
            return HttpResponseStructs.generateJsonResponse("account created", 200);
        } catch (Exception e) {
            return HttpResponseStructs.generateJsonResponse("user email is already in use", 400);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JsonHttpResponse> login(@RequestBody LoginPayload loginPayload, HttpServletResponse response) throws BadRequestException {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginPayload.getUsername(), loginPayload.getPassword()));
            if (authentication.isAuthenticated()) {
                final Object token = jwtService.generateToken(loginPayload.getUsername());
                return HttpResponseStructs.generateJsonResponse(token, 200);
            }
            return HttpResponseStructs.generateJsonResponse("Invalid credentials", 403);
        } catch (Exception e) {
            log.error(e.getMessage());
            return HttpResponseStructs.generateJsonResponse("Invalid credentials", 403);
        }
    }

}
