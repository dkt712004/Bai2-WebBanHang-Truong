package com.dkt.bai2webbanhangtruong.config;

import com.dkt.bai2webbanhangtruong.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final CustomSuccessHandler customSuccessHandler;

    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService,
                             CustomSuccessHandler customSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.customSuccessHandler = customSuccessHandler;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {


        http.csrf(csrf -> csrf.disable());

        http.cors(Customizer.withDefaults());


        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/me").permitAll()
                .requestMatchers("/productImage/**").permitAll()
                .requestMatchers("/api/products/**", "/api/orders/**").hasRole("MANAGER")
                .requestMatchers("/admin/orderList", "/admin/order/**").hasAnyRole("EMPLOYEE", "MANAGER")
                .requestMatchers("/admin/product/**", "/admin/productEdit/**").hasRole("MANAGER")
                .anyRequest().permitAll()
        );

        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    if (request.getRequestURI().startsWith("/api/")) {
                        response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                    } else {

                        response.sendRedirect("/admin/login");
                    }
                })
                .accessDeniedPage("/403")
        );

        http.formLogin(form -> form
                .loginProcessingUrl("/j_spring_security_check")
                .loginPage("/admin/login")
                .successHandler(customSuccessHandler)
                .failureUrl("/admin/login?error=true")
                .usernameParameter("userName")
                .passwordParameter("password")
        );

        http.logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
        );

        return http.build();
    }
}