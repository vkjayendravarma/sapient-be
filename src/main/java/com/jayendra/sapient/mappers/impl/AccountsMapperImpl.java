package com.jayendra.sapient.mappers.impl;

import com.jayendra.sapient.domain.Account;
import com.jayendra.sapient.domain.dto.AccountDto;
import com.jayendra.sapient.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class AccountsMapperImpl implements Mapper<Account, AccountDto> {

    private final ModelMapper modelMapper;

    public AccountsMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
    @Override
    public AccountDto mapTo(Account account) {
        return modelMapper.map(account, AccountDto.class);
    }

    @Override
    public Account mapFrom(AccountDto accountDto) {
        return modelMapper.map(accountDto, Account.class);
    }
}
