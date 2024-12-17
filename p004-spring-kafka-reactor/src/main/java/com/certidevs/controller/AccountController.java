package com.certidevs.controller;


import com.certidevs.entity.Account;
import com.certidevs.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

// Entrar por SWAGGER: http://localhost:8080/webjars/swagger-ui/index.html

@RestController
@AllArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {

    private AccountService accountService;

    @PostMapping
    public Mono<ResponseEntity<Account>> create(@RequestBody Account account) {
        return accountService.create(account).map(ResponseEntity::ok);
    }
    @GetMapping("{id}")
    public Mono<ResponseEntity<Account>> findById(@PathVariable Long id) {
        return accountService.findById(id).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
