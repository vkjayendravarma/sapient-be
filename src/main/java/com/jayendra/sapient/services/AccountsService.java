package com.jayendra.sapient.services;


import com.jayendra.sapient.domain.Account;
import org.apache.coyote.BadRequestException;

public interface AccountsService {
    Account createAccount(Account account);
    Account verifyUser(String username, String password) throws Exception;
    Account findUserByUserName(String username);
}
