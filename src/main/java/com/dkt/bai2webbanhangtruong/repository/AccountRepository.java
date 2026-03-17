package com.dkt.bai2webbanhangtruong.repository;

import com.dkt.bai2webbanhangtruong.entity.Account;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends BaseRepository<Account, Long> {
    Account findByUserName(String userName);
}