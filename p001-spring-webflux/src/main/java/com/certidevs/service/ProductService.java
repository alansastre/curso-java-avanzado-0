package com.certidevs.service;

import com.certidevs.entity.Product;
import com.certidevs.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public Flux<Product> findAll() {
//        return productRepository.findAll();
        return productRepository.findAll().map(p -> {
            p.setTitle(p.getTitle().toUpperCase()); // Ejemplo de transformación antes de devolver el flux
            return p;
        });
    }

    public Mono<Product> save(Product product) {
        product.setActive(true);
        product.setCreationDate(LocalDateTime.now());
        return productRepository.save(product);
//        return productRepository.save(product).block();
    }

    public Mono<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Mono<Product> update(Long id, Product product) {
        return productRepository.findById(id)
                .flatMap(productDB -> {
                       productDB.setPrice(product.getPrice());
                       productDB.setQuantity(product.getQuantity());
                        // BeanUtils.copyProperties(product, productDB);
                        return productRepository.save(productDB);
        });
    }

    public Flux<Product> increasePriceOfActiveProducts(Double percentage) {
//        return productRepository.findAll()
//                .filter(Product::getActive)
//                .flatMap(product -> {
//                    Double price = product.getPrice() != null ? product.getPrice() : 0.0;
//                    Double newPrice = price * (1 + percentage / 100);
//                    product.setPrice(newPrice);
//                    return productRepository.save(product);
//
//                });

        return productRepository.findByActiveTrue()
                .map(product -> {
                    Double price = product.getPrice() != null ? product.getPrice() : 0.0;
                    Double newPrice = price * (1 + percentage / 100);
                    product.setPrice(newPrice);
                    return product;
                }).collectList()
                .flatMapMany(productRepository::saveAll);

//        return productRepository.findByActiveTrue()
//                .map(product -> {
//                    Double price = product.getPrice() != null ? product.getPrice() : 0.0;
//                    Double newPrice = price * (1 + percentage / 100);
//                    product.setPrice(newPrice);
//                    return product;
//                })
//                .buffer(100)
//                .flatMap(productRepository::saveAll);
    }

    public Mono<Void> increasePriceOfActiveProductsVoid(Double percentage) {
        return productRepository.findByActiveTrue()
                .map(product -> {
                    double price = product.getPrice() != null ? product.getPrice() : 0.0;
                    Double newPrice = price * (1 + percentage / 100);
                    product.setPrice(newPrice);
                    return product;
                }).collectList()
                .flatMapMany(productRepository::saveAll)
                .then();
    }

    public Mono<Product> reduceQuantity(Long productId, Integer amount) {
        return productRepository.findById(productId)

                .flatMap(product -> {
                    if (product.getQuantity() >= amount) {
                        product.setQuantity(product.getQuantity() - amount);
                        return productRepository.save(product);
                    } else {
//                        log.warn
                        return Mono.error(new IllegalArgumentException("Cantidad insuficiente"));
                    }
                });
    }

    // webClient traer el fabricante de un producto
    // webClient traer el fabricante y las reseñas del producto: zip para traer las peticiones a la vez
}
