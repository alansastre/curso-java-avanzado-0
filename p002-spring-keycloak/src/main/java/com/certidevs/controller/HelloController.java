package com.certidevs.controller;

import com.certidevs.service.HelloService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

// http://localhost:8081/hello0
// http://localhost:8081/hello1
// http://localhost:8081/hello2
// http://localhost:8081/hello3
@RestController
@AllArgsConstructor
public class HelloController {

    private final HelloService helloService;

    @GetMapping("/hello0")
    public Mono<Authentication> hello0(Mono<Authentication> auth) {
        return auth;
    }

    @GetMapping("/hello1")
    public Mono<Authentication> hello1() {
        return helloService.doSomethingAndGetAuthentication();
    }

    @GetMapping("/hello2")
    public Mono<Authentication> hello2(Mono<Authentication> auth) {
        return auth;
    }

    @GetMapping("/hello3")
    public Mono<Authentication> hello3(Mono<Authentication> auth) {
        return auth;
    }
}
