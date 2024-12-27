package mako3.found.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Bean
        PasswordEncoder passwordEncoder() {
                return NoOpPasswordEncoder.getInstance();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.authorizeHttpRequests(
                                authorize -> authorize
                                                .requestMatchers("/css/**", "/login", "/favicon.ico")
                                                .permitAll()
                                                .anyRequest()
                                                .authenticated())
                                .formLogin(formLogin -> formLogin
                                                .loginPage("/login")
                                                .failureUrl("/login?failure")
                                                .defaultSuccessUrl("/")
                                                .permitAll())
                                .exceptionHandling(exception -> exception
                                                .accessDeniedPage("/display-access-denied"))
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID"))
                                .csrf(configureator -> configureator.ignoringRequestMatchers("/*"));

                return http.build();
        }
}
