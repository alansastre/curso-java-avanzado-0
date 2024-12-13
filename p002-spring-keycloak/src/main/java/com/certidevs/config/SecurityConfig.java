package com.certidevs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

/*
* Spring Web: SecurityFilterChain, HttpSecurity
* Spring WebFlux: SecurityWebFilterChain, ServerHttpSecurity
*/

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {


//    @Bean
//    public MapReactiveUserDetailsService userDetailsService() {
//        UserDetails user = User. withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//        return new MapReactiveUserDetailsService(user);
//    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                        .authorizeExchange(exchanges -> exchanges
                                .pathMatchers("/hello0", "/hello1").permitAll()
                                .pathMatchers("/hello2").hasRole("USER")
                                .pathMatchers("/hello3").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.GET, "/api/products/**").hasRole("USER")
                                .pathMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.PATCH, "/api/products/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                                .anyExchange()
                                .authenticated() // Para acceder a todas las demás rutas tienes que estar autenticado, da igual con qué usuario y el rol
//                        ).oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
                        ).oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(keycloakRolesConverter())));


        return http.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> keycloakRolesConverter() {
        return new KeycloakRolesConverter();
    }


}
