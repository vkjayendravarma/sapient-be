package com.jayendra.sapient.services.impl;


import com.jayendra.sapient.domain.Account;
import com.jayendra.sapient.repositories.AccountsRepository;
import com.jayendra.sapient.services.AccountsService;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountsServiceImpl implements AccountsService {
    private final AccountsRepository accountsRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10);

    public AccountsServiceImpl(AccountsRepository accountsRepository) {
        this.accountsRepository = accountsRepository;
    }

    @Override
    public Account createAccount(Account account) {
        account.setPassword(bCryptPasswordEncoder.encode(account.getPassword()));
        return this.accountsRepository.save(account);
    }

    @Override
    public Account findUserByUserName(String username) {
        return this.accountsRepository.findByUsername(username);
    }

    @Override
    public Account verifyUser(String username, String password) throws Exception {
        Account userAccount = this.accountsRepository.findByUsername(username);
        if (userAccount == null) {
            throw new Exception("Account not found");
        }

        if (bCryptPasswordEncoder.matches(password, userAccount.getPassword())) {
            return userAccount;
        }
        throw new Exception("Invalid password");
    }
}
