package com.certidevs.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/*
* Spring Web + Spring Security: SecurityContextHolder
* Spring WebFlux + Spring Security: ReactiveSecurityContextHolder
 */
@Service
public class HelloService {

    public Mono<Authentication> doSomethingAndGetAuthentication() {
        Mono<Authentication> auth = ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication);

        // podemos usar el authentication para sacar el usuario, asociarlo a una entidad o asociar su id como clave foránea

        // lo devolvemos para verlo en el controlador, pero normalmente no lo devuelves, simplemente lo usas

        // Es común crear una clase de utilidad static que use el ReactiveSecurityContextHolder para darnos el usuario
        // SecurityUtils.getAuthenticatedUser().orElseThrows()
        // SecurityUtils.isAuthenticatedUserAdminRole()
        return auth;

    }
}
