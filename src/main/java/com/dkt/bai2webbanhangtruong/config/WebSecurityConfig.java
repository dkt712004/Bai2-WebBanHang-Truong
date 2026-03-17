package com.dkt.bai2webbanhangtruong.config;

import com.dkt.bai2webbanhangtruong.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Vô hiệu hóa CSRF
        http.csrf(csrf -> csrf.disable());

        // Cấu hình phân quyền (Authorize Requests)
        http.authorizeHttpRequests(auth -> auth
                // Các trang yêu cầu vai trò EMPLOYEE hoặc MANAGER
                .requestMatchers("/admin/orderList", "/admin/order", "/admin/accountInfo")
                .hasAnyRole("EMPLOYEE", "MANAGER")

                // Trang yêu cầu vai trò MANAGER
                .requestMatchers("/admin/product").hasRole("MANAGER")

                // Tất cả các trang còn lại cho phép truy cập tự do
                .anyRequest().permitAll()
        );

        // Cấu hình trang lỗi khi không có quyền truy cập
        http.exceptionHandling(ex -> ex.accessDeniedPage("/403"));

        // Cấu hình trang Đăng nhập (Login)
        http.formLogin(form -> form
                .loginProcessingUrl("/j_spring_security_check")
                .loginPage("/admin/login")
                .defaultSuccessUrl("/admin/accountInfo")
                .failureUrl("/admin/login?error=true")
                .usernameParameter("userName")
                .passwordParameter("password")
        );

        // Cấu hình Đăng xuất (Logout)
        http.logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/")
        );

        return http.build();
    }
}