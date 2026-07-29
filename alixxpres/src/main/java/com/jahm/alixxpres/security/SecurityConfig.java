package com.jahm.alixxpres.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {
    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(JwtTokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.tokenProvider = tokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider, customUserDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // ============ ENDPOINTS PUBLICOS ============
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/productos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/categorias/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/proveedores/**").permitAll()
                
                // ============ ENDPOINTS DEL CARRITO (NUEVO) ============
                // Cualquier usuario autenticado (cliente o admin) puede ver su carrito
                .requestMatchers(HttpMethod.GET, "/api/v1/carrito/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/carrito").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/v1/carrito/mi-carrito").authenticated()
                
                // Agregar, actualizar y eliminar del carrito - solo clientes
                .requestMatchers(HttpMethod.POST, "/api/v1/carrito/agregar").hasAuthority("ROLE_CLIENTE")
                .requestMatchers(HttpMethod.PUT, "/api/v1/carrito/actualizar").hasAuthority("ROLE_CLIENTE")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/carrito/**").hasAuthority("ROLE_CLIENTE")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/carrito/vaciar").hasAuthority("ROLE_CLIENTE")
                
                // ============ ENDPOINTS DE VENTAS ============
                .requestMatchers(HttpMethod.POST, "/api/v1/ventas/**").hasAuthority("ROLE_CLIENTE")
                .requestMatchers(HttpMethod.GET, "/api/v1/ventas").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/ventas/mis-compras").hasAuthority("ROLE_CLIENTE")
                .requestMatchers(HttpMethod.GET, "/api/v1/ventas/**").hasAnyAuthority("ROLE_CLIENTE", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/ventas/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/ventas/**").hasAuthority("ROLE_ADMIN")
                
                // ============ ENDPOINTS DE PRODUCTOS ============
                .requestMatchers(HttpMethod.POST, "/api/v1/productos/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/productos/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/productos/**").hasAuthority("ROLE_ADMIN")
                
                // ============ ENDPOINTS DE CATEGORIAS ============
                .requestMatchers(HttpMethod.POST, "/api/v1/categorias/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/categorias/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/categorias/**").hasAuthority("ROLE_ADMIN")
                
                // ============ ENDPOINTS DE PROVEEDORES ============
                .requestMatchers(HttpMethod.POST, "/api/v1/proveedores/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/proveedores/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/proveedores/**").hasAuthority("ROLE_ADMIN")
                
                // ============ ENDPOINTS DE PAGOS ============
                .requestMatchers("/api/v1/pagos/**").hasAnyAuthority("ROLE_CLIENTE", "ROLE_ADMIN")
                
                // ============ ENDPOINTS DE CLIENTES ============
                .requestMatchers(HttpMethod.GET, "/api/v1/clientes/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/clientes/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/v1/clientes/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/clientes/**").hasAuthority("ROLE_ADMIN")
                
                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",
            "http://localhost:5174",
            "http://localhost:3000",
            "http://127.0.0.1:5173",
            "http://civeh5hm5leeopm2u7feman9.168.231.67.126.sslip.io:5173",
            "http://epqy26ctakwdqnuavcsjlb33.168.231.67.126.sslip.io:5173",
            "https://civeh5hm5leeopm2u7feman9.168.231.67.126.sslip.io:5173",
            "https://epqy26ctakwdqnuavcsjlb33.168.231.67.126.sslip.io:5173",
            "http://civeh5hm5leeopm2u7feman9.168.231.67.126.sslip.io",
            "http://epqy26ctakwdqnuavcsjlb33.168.231.67.126.sslip.io"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
