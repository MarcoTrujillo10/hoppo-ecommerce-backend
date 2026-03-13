package com.example.HPPO_Backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                        .csrf(AbstractHttpConfigurer::disable)
                        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                        .sessionManagement(sm -> sm.sessionCreationPolicy(STATELESS))
                        .authorizeHttpRequests(auth -> auth
                            
                                .requestMatchers("/api/v1/auth/**", "/auth/**", "/error/**").permitAll()

                                // Endpoint para obtener perfil del usuario autenticado
                                .requestMatchers(HttpMethod.GET, "/users/myuser").hasAnyRole("COMPRADOR", "VENDEDOR")

                                .requestMatchers(HttpMethod.GET, "/products/**", "/categories/**", "/brands/**", "/carousel").permitAll()
                                .requestMatchers(HttpMethod.POST,   "/products/**").hasRole("VENDEDOR")
                                .requestMatchers(HttpMethod.PUT,    "/products/**").hasRole("VENDEDOR")
                                .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("VENDEDOR")


                                .requestMatchers(HttpMethod.POST,   "/categories/**").hasRole("VENDEDOR")
                                .requestMatchers(HttpMethod.PUT,    "/categories/**").hasRole("VENDEDOR")
                                .requestMatchers(HttpMethod.DELETE, "/categories/**").hasRole("VENDEDOR")


                                .requestMatchers(HttpMethod.POST,   "/brands/**").hasRole("VENDEDOR")
                                .requestMatchers(HttpMethod.PUT,    "/brands/**").hasRole("VENDEDOR")
                                .requestMatchers(HttpMethod.DELETE, "/brands/**").hasRole("VENDEDOR")


                                .requestMatchers(HttpMethod.GET,  "/carts/my-cart").hasRole("COMPRADOR")
                                .requestMatchers(HttpMethod.POST, "/carts").hasRole("COMPRADOR")
                                .requestMatchers(HttpMethod.GET,  "/carts", "/carts/*").hasRole("VENDEDOR")
                                .requestMatchers(HttpMethod.PUT,  "/carts/*").hasAnyRole("VENDEDOR", "COMPRADOR")


                                .requestMatchers(HttpMethod.GET, "/cart-products/**").hasAnyRole("VENDEDOR", "COMPRADOR")
                                .requestMatchers(HttpMethod.POST,   "/cart-products").hasRole("COMPRADOR")
                                .requestMatchers(HttpMethod.PUT,    "/cart-products/*").hasRole("COMPRADOR")
                                .requestMatchers(HttpMethod.DELETE, "/cart-products/*").hasRole("COMPRADOR")


                                .requestMatchers(HttpMethod.GET, "/orders/my-orders").hasRole("COMPRADOR")
                                .requestMatchers(HttpMethod.PATCH, "/orders/*/cancel").hasRole("COMPRADOR")
                                .requestMatchers(HttpMethod.POST,  "/orders").hasRole("COMPRADOR")

                                .requestMatchers(HttpMethod.PUT,   "/orders/*").hasAnyRole("VENDEDOR", "COMPRADOR")
                                .requestMatchers(HttpMethod.GET, "/orders", "/orders/*").hasRole("VENDEDOR")

                                // Upload endpoints
                                    .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                                    .requestMatchers(HttpMethod.POST, "/uploads").hasRole("VENDEDOR")

                                    // Payment endpoints
                                    .requestMatchers(HttpMethod.POST, "/payments/**").hasRole("COMPRADOR")

                                    // Admin carousel endpoints
                                    .requestMatchers("/admin/carousel/**").hasRole("VENDEDOR")

                                    .anyRequest().authenticated()
                        )
                        .authenticationProvider(authenticationProvider)
                        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.addAllowedOriginPattern("*"); // Permitir cualquier origen en desarrollo
                configuration.addAllowedMethod("*"); // Permitir todos los métodos HTTP
                configuration.addAllowedHeader("*"); // Permitir todos los headers
                configuration.setAllowCredentials(true); // Permitir credenciales
                
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
