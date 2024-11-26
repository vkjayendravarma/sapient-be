package com.jayendra.sapient.repositories;

import com.jayendra.sapient.domain.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AccountsRepository extends CrudRepository<Account, Long>, PagingAndSortingRepository<Account, Long> {
    Account findByUsername(String username);
}
