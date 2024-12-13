package com.certidevs.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

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
    void hello2() {
        // da error porque ya no es 200 OK es 401 UNAUTHORIZED
//        client.get().uri("/hello2")
//                .exchange()
//                .expectStatus().isOk();

        client.get().uri("/hello2")
                .exchange()
                .expectStatus().isUnauthorized();


//        client.mutateWith(mockJwt()).get().uri("/hello2")
//                .exchange()
//                .expectStatus().isOk();

        client.mutateWith(
                mockJwt().jwt(jwt -> jwt
                        .claim("sub", "testuser")
                        .claim("roles", List.of("USER"))
                ))
                .get().uri("/hello2")
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    void hello3() {
        client.mutateWith(
                        mockJwt().jwt(jwt -> jwt
                                .claim("sub", "testuser")
                                .claim("roles", List.of("ADMIN"))
                        ))
                .get().uri("/hello3")
                .exchange()
                .expectStatus().isOk();
    }

}