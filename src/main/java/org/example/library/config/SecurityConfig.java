package org.example.library.config;

//@author Aliaksandra_Baranava

import org.example.library.repository.UserRepository;
import org.example.library.security.LibraryUserDetailsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           LibraryUserDetailsService userDetailsService) throws Exception {

        // После входа: библиотекарь -> /librarian, читатель -> /
        AuthenticationSuccessHandler successHandler = (request, response, authentication) -> {
            boolean librarian = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_LIBRARIAN"));
            response.sendRedirect(request.getContextPath() + (librarian ? "/librarian" : "/"));
        };

        http
                .authorizeHttpRequests(auth -> auth
                        // статика открыта для всех
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html").permitAll()
                        // публичные страницы
                        .requestMatchers("/", "/login", "/registration", "/verify", "/recovery/**", "/error").permitAll()
                        // только библиотекарь
                        .requestMatchers("/librarian/**", "/issue", "/return",
                                "/reservation-issue/**", "/book-add", "/author-add", "/write-off").hasRole("LIBRARIAN")
                        // только читатель
                        .requestMatchers("/book-reservation", "/reader/**").hasRole("READER")
                        // остальное любой вошедший пользователь
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(successHandler)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .rememberMe(rm -> rm
                        .rememberMeParameter("rememberMe")
                        .key("замените-на-длинную-случайную-строку")
                        .tokenValiditySeconds(14 * 24 * 60 * 60)
                        .userDetailsService(userDetailsService)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID", "remember-me")
                );
        return http.build();
    }

}

