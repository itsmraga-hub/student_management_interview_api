package com.api.student_management.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

//@Configuration
//@EnableWebSecurity
//public class SecurityConfiguration {
//    private final AuthenticationProvider authenticationProvider;
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    public SecurityConfiguration(
//            JwtAuthenticationFilter jwtAuthenticationFilter,
//            AuthenticationProvider authenticationProvider
//    ) {
//        this.authenticationProvider = authenticationProvider;
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.cors()
//                .and()
//                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/auth/**","/auth/signup", "/auth/login","/auth/welcome", "/auth/addNewUser", "/auth/generateToken").permitAll()
//                        .requestMatchers("/auth/user/**").hasAuthority("ROLE_USER")
//                        .requestMatchers("/auth/admin/**").hasAuthority("ROLE_ADMIN")
//                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
////                        .requestMatchers("/students/**").permitAll()
//                        .anyRequest().authenticated() // Protect all other endpoints
//                )
//                .sessionManagement(sess -> sess
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No sessions
//                )
//                .authenticationProvider(authenticationProvider) // Custom authentication provider
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter
//
//        return http.build();
//    }
//
//
//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:4200"));
//        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//
//        source.registerCorsConfiguration("/**", configuration);
//
//        return source;
//    }
//}

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
public class SecurityConfiguration {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.cors(corsCustomizer->corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration corsConfiguration=new CorsConfiguration();
                        corsConfiguration.setAllowCredentials(true);
                        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:8080, http://localhost:3000"));
                        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
                        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
                        corsConfiguration.setMaxAge(Duration.ofMinutes(5L));
                        return corsConfiguration;
                    }
                }));

        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/auth/**","/auth/signup", "/auth/login","/auth/welcome", "/auth/addNewUser").permitAll()
                                .requestMatchers("/images/**").permitAll()
                                .requestMatchers("/auth/user/**").hasAuthority("ROLE_USER")
                                .requestMatchers("/auth/admin/**").hasAuthority("ROLE_ADMIN")
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                        .requestMatchers("/students/**").permitAll()
                                .anyRequest().authenticated() // Protect all other endpoints
                )
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No sessions
                )
                .authenticationProvider(authenticationProvider) // Custom authentication provider
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();

    }
}
