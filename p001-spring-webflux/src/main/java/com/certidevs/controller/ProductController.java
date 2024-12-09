package com.certidevs.controller;

import com.certidevs.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {


    private ProductService productService;

    // Mono<ResponseEntity<Flux<Product>>>
    // findAll
    // findById
    // create
    // update
    // delete

}
