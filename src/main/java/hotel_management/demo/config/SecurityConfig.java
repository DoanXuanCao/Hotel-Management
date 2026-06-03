package hotel_management.demo.config;

import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  private final JwtFilter jwtFilter;

  public SecurityConfig(JwtFilter jwtFilter) {
    this.jwtFilter = jwtFilter;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // Auth endpoints — public
            .requestMatchers("/api/auth/**").permitAll()
            // Thymeleaf page routes — public (auth enforced on API calls via JWT)
            .requestMatchers("/", "/homepage", "/admin", "/guest", "/client",
                             "/hotels/**", "/reservation", "/payments/**",
                             "/setting", "/employee").permitAll()
            // Static assets
            .requestMatchers("/assets/**", "/*.js", "/*.css").permitAll()

            // Admin-only write operations
            .requestMatchers(HttpMethod.POST,   "/api/employees").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/employees/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST,   "/api/hotels").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/hotels/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST,   "/api/roomtypes").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/roomtypes/**").hasRole("ADMIN")

            // Employee + Admin
            .requestMatchers("/api/employees/**").hasAnyRole("ADMIN", "EMPLOYEE")
            .requestMatchers(HttpMethod.GET,    "/api/guests/**").hasAnyRole("ADMIN", "EMPLOYEE", "GUEST")
            .requestMatchers(HttpMethod.DELETE, "/api/guests/**").hasAnyRole("ADMIN", "EMPLOYEE")
            .requestMatchers("/api/accounts/**").hasAnyRole("ADMIN", "EMPLOYEE")

            // Reservations — authenticated users
            .requestMatchers("/api/reservations/**").authenticated()
            .requestMatchers("/api/payments/**").authenticated()
            .requestMatchers("/api/rooms/**").authenticated()
            .requestMatchers("/api/hotels/**").authenticated()
            .requestMatchers("/api/roomtypes/**").authenticated()

            .anyRequest().authenticated()
        )
        .httpBasic(httpBasic -> httpBasic.disable())
        .formLogin(form -> form.disable())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
