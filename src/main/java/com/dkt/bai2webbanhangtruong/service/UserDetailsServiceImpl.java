package com.dkt.bai2webbanhangtruong.service;

import com.dkt.bai2webbanhangtruong.dao.AccountDAO;
import com.dkt.bai2webbanhangtruong.entity.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountDAO accountDAO;


    public UserDetailsServiceImpl(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Account account = accountDAO.findAccount(username);

        if (account == null) {
            throw new UsernameNotFoundException("Tài khoản " + username + " không tồn tại trong hệ thống!");
        }


        String role = account.getUserRole(); // Giả sử trong DB lưu là "MANAGER" hoặc "EMPLOYEE"

        List<GrantedAuthority> grantList = new ArrayList<>();


        if (role != null && !role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }


        grantList.add(new SimpleGrantedAuthority(role));


        return new User(
                account.getUserName(),
                account.getEncrytedPassword(),
                account.isActive(),      // Tài khoản còn hoạt động không?
                true,                    // accountNonExpired (Không hết hạn)
                true,                    // credentialsNonExpired (Mật khẩu không hết hạn)
                true,                    // accountNonLocked (Không bị khóa)
                grantList                // Danh sách quyền hạn (Ví dụ: ROLE_MANAGER)
        );
    }
}