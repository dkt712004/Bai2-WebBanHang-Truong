package com.dkt.bai2webbanhangtruong.dao;

import com.dkt.bai2webbanhangtruong.entity.Account;
import com.dkt.bai2webbanhangtruong.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AccountDAO {

    @Autowired
    private AccountRepository accountRepository;

    public Account findAccount(String userName) {
        return accountRepository.findByUserName(userName);
    }
}