package com.certidevs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.*;

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
                                .pathMatchers("/hello4").hasAnyRole("USER", "ADMIN")
                                .pathMatchers(HttpMethod.GET, "/api/products/**").hasRole("USER")
                                .pathMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.PATCH, "/api/products/**").hasRole("ADMIN")
                                .pathMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                                .anyExchange()
                                .authenticated() // Para acceder a todas las demás rutas tienes que estar autenticado, da igual con qué usuario y el rol
//                        ).oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
                                // Convertidor personalizado para Keycloak para pasar los roles de keycloak a GrantedAuthority de Spring con prefijo ROLE_
                        ).oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(keycloakRolesConverter())));


        return http.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> keycloakRolesConverter() {
        return new KeycloakRolesConverter();
    }

//    public Mono<AbstractAuthenticationToken> keycloakRolesConverter(Jwt source) {
//        // Extraer roles GrantedAuthority
//        Set<GrantedAuthority> authorities = new HashSet<>();
//
//        // realm_access.roles
//        // extraer los string de roles y convertirlos al formato de Spring Security: ROLE_USER, ROLE_ADMIN usando GrantedAuthority
//        Map<String, Object> realmAccess = source.getClaim("realm_access");
//        if (realmAccess != null && realmAccess.containsKey("roles")) {
//            Object roles = realmAccess.get("roles");
//            if (roles instanceof List) {
//                List<String> roleList = (List<String>) roles;
//                authorities.addAll(
//                        roleList.stream()
//                                .map(role -> "ROLE_" + role.toUpperCase())
//                                .map(SimpleGrantedAuthority::new)
//                                .toList()
//                );
//            }
//        }
//
//        // se pueden seguir agregando más authorities que sacemos de otros claims, como por ejemplo el claim "scope" que tiene email profile
//
//
//
//        return Mono.just(new UsernamePasswordAuthenticationToken(source.getSubject(), source, authorities));
//    }

}
