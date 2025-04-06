package com.example.sispas.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfiguration {

    private final JwtFilter jwtFilter;
    private final UserDetailsService userDetailsService;

    @Autowired
    public WebSecurityConfiguration(JwtFilter jwtFilter, UserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/api/authorization/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/authorization/logout").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/authorization/change-password").hasAnyRole("administrator", "user")
                        .requestMatchers(HttpMethod.POST, "/api/account-requests/register").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/user/request/{id}/approve").hasRole("administrator")
                        .requestMatchers(HttpMethod.PUT, "/api/user/request/{id}/reject").hasRole("administrator")
                        .requestMatchers(HttpMethod.GET, "/api/user/requests").hasRole("administrator")
                        .requestMatchers(HttpMethod.GET, "/api/user/users").hasRole("administrator")
                        .requestMatchers(HttpMethod.GET, "/api/user/profile").hasAnyRole("administrator", "user")
                        .requestMatchers(HttpMethod.GET, "/api/facility/all").hasAnyRole("administrator", "user")
                        .requestMatchers(HttpMethod.GET, "/api/facility/{id}").hasAnyRole("administrator", "user")
                        .requestMatchers(HttpMethod.POST, "/api/facility/add-facility").hasRole("administrator")
                        .requestMatchers(HttpMethod.PUT, "/api/facility/activate/{id}").hasRole("administrator")
                        .requestMatchers(HttpMethod.PUT, "/api/facility/deactivate/{id}").hasRole("administrator")
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/disciplines/facility/{id}").hasAnyRole("administrator", "user")
                        .requestMatchers(HttpMethod.GET, "/api/workdays/facility/{id}").hasAnyRole("administrator", "user")
                        .requestMatchers(HttpMethod.GET, "/api/exercises/facility/{id}").hasAnyRole("administrator", "user")
                        .requestMatchers(HttpMethod.GET, "/api/exercises/user/{id}").hasAnyRole("administrator", "user")
                        .requestMatchers(HttpMethod.GET, "/api/exercises/reserve").hasAnyRole("administrator", "user")
                        .anyRequest().authenticated())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        http.authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
