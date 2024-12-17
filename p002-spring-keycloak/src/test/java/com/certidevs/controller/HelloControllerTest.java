package com.certidevs.controller;

import com.certidevs.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

/*
Test de integración para probar la seguridad del controlador HelloController

hello0, hello1 -> permitAll
hello2 -> USER
hello3 -> ADMIN
hello4 -> USER, ADMIN

Status:

* 200 OK
* 401 UNAUTHORIZED si no estamos autenticados, es decir, no tenemos token JWT por tanto no se identifica el usuario
* 403 FORBIDDEN si estamos autenticados, pero no tenemos el rol suficiente para acceder al recurso

 */
@SpringBootTest
@AutoConfigureWebTestClient
class HelloControllerTest {

    @Autowired
    private WebTestClient client;


    @Test
    void hello0() {
        client.get().uri("/hello0")
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    void hello1() {
        client.get().uri("/hello1")
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    void hello2_noAuth() {
        client.get().uri("/hello2")
                .exchange()
                .expectStatus().isUnauthorized(); // 401

    }

    @Test
    void hello2_roleUSER_OK() {
        client.mutateWith(
                        mockJwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))
                )
                .get().uri("/hello2")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void hello2_roleADMIN_Forbidden() {
        client.mutateWith(
                        mockJwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
                .get().uri("/hello2")
                .exchange()
                .expectStatus().isForbidden(); // 403
    }


    @Test
    void hello3_roleUSER_forbidden() {
        client.mutateWith(
                        mockJwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))
                )
                .get().uri("/hello3")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void hello3_roleADMIN_OK() {
        client.mutateWith(
                        mockJwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
                .get().uri("/hello3")
                .exchange()
                .expectStatus().isOk(); // 200
    }

    @Test
    void hello4_noAuth_Unauthorized() {
        client.get().uri("/hello4")
                .exchange()
                .expectStatus().isUnauthorized(); // 401
    }

    @Test
    void hello4_USER_OK() {
        client.mutateWith(
                        mockJwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))
                )
                .get().uri("/hello4")
                .exchange()
                .expectStatus().isOk(); // 200
    }
    @Test
    void hello4_ADMIN_OK() {
        client.mutateWith(
                        mockJwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
                .get().uri("/hello4")
                .exchange()
                .expectStatus().isOk(); // 200
    }

    @Test
    void tokenConClaims() {
        client.mutateWith(
                        mockJwt().jwt(
                                        jwt -> jwt
                                                // NO HACEN FALTA, PERO SI LOS NECESITAMOS SE PUEDEN AÑADIR ASÍ
                                                .claim("sub", "")
                                                .claim("realm_access", Map.of("roles", List.of("ADMIN")))
                                                .build()
                                )
                                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
                .get().uri("/hello4")
                .exchange()
                .expectStatus().isOk(); //200
    }
}