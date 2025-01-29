package mako3.found.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Autowired
        private CustomLoginSuccessHandler customLoginSuccessHandler;

        @Bean
        PasswordEncoder passwordEncoder() {
                return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.authorizeHttpRequests(
                                authorize -> authorize
                                                .requestMatchers("/css/**", "/login", "/favicon.ico",
                                                                "/pw-reset/*")
                                                .permitAll()
                                                .anyRequest()
                                                .authenticated())
                                .formLogin(formLogin -> formLogin
                                                .loginPage("/login")
                                                .failureUrl("/login?failure")
                                                //.defaultSuccessUrl("/", true)
                                                .successHandler(customLoginSuccessHandler)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID"))
                                .exceptionHandling(exception -> exception
                                                .accessDeniedPage("/display-access-denied"))
                                .csrf(configureator -> configureator.ignoringRequestMatchers("/**"));

                return http.build();
        }
}
