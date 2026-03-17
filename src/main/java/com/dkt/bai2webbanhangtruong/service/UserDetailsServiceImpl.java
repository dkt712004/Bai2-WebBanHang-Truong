package com.dkt.bai2webbanhangtruong.service;

import com.dkt.bai2webbanhangtruong.dao.AccountDAO;
import com.dkt.bai2webbanhangtruong.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AccountDAO accountDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountDAO.findAccount(username);

        if (account == null) {
            throw new UsernameNotFoundException("User " + username + " was not found in the database");
        }

        // Vai trò (ROLE_MANAGER, ROLE_EMPLOYEE)
        String role = account.getUserRole();
        List<GrantedAuthority> grantList = new ArrayList<>();
        grantList.add(new SimpleGrantedAuthority(role));

        return new User(account.getUserName(),
                account.getEncrytedPassword(),
                account.isActive(),
                true, true, true, grantList);
    }
}