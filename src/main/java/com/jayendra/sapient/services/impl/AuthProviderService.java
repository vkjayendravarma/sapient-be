package com.jayendra.sapient.services.impl;

import com.jayendra.sapient.domain.Account;
import com.jayendra.sapient.repositories.AccountsRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthProviderService implements UserDetailsService {

    private final AccountsRepository accountsRepository;

    public AuthProviderService(AccountsRepository accountsRepository) {
        this.accountsRepository = accountsRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.accountsRepository.findByUsername(username);

        if (account == null){
            throw  new UsernameNotFoundException("User not found");
        }
        return new UserPrincipal(account);
    }
}
