package com.certidevs.config;


import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KeycloakRolesConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt source) {


        // Extraer roles GrantedAuthority
        Set<GrantedAuthority> authorities = new HashSet<>();

        // realm_access.roles
        // extraer los string de roles y convertirlos al formato de Spring Security: ROLE_USER, ROLE_ADMIN usando GrantedAuthority
        Map<String, Object> realmAccess = source.getClaim("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            Object roles = realmAccess.get("roles");
            if (roles instanceof List) {
                List<String> roleList = (List<String>) roles;
                authorities.addAll(
                        roleList.stream()
                                .map(role -> "ROLE_" + role.toUpperCase())
                                .map(SimpleGrantedAuthority::new)
                                .toList()
                );
            }
        }

        // se pueden seguir agregando más authorities que sacemos de otros claims, como por ejemplo el claim "scope" que tiene email profile



        return Mono.just(new JwtAuthenticationToken(source, authorities));
    }
}
