package com.certidevs.service;

import com.certidevs.dto.ProductStoreDTO;
import com.certidevs.dto.RatingDTO;
import com.certidevs.entity.Manufacturer;
import com.certidevs.entity.Product;
import com.certidevs.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final WebClient manufacturersClient;
    private final WebClient ratingsClient;

    public Flux<Product> findAll() {
//        return productRepository.findAll();
        return productRepository.findAll().map(p -> {
            p.setTitle(p.getTitle().toUpperCase()); // Ejemplo de transformación antes de devolver el flux
            return p;
        });
    }

    public Mono<Product> save(Product product) {
        // opcional: asignar valores por defecto, también se puede hacer a nivel de base de datos
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

    public Mono<Void> deleteById(Long id) {
        // Alternativa:  setActive(false)
        return productRepository.deleteById(id);
    }

    public Mono<Long> count() {
        return productRepository.count();
    }


    // WebClient para hacer fetch de datos remotos
    public Mono<Product> findByIdWithManufacturer() {
        return null;
    }
    public Flux<Product> findAllWithManufacturers() {

        // supongamos que el manufacturer está en otro microservicio y hay que traerlos

        // opción 1: lanza una petición al microservicio de fabricantes por cada producto (ineficiente)
        return productRepository.findAll()
                .flatMap(product -> manufacturersClient.get()
                        .uri("/api/manufacturers/{id}", product.getManufacturerId())
                        .retrieve()
                        .bodyToMono(Manufacturer.class)
                        .map(manufacturer -> {
                            product.setManufacturer(manufacturer);
                            return product;
                        }).defaultIfEmpty(product)
                );

        // opción 2: traer todos los fabricantes en una sola query por sus ids para procesarlos de golpe

//        return productRepository.findAll()
//                .collectMultimap(Product::getManufacturerId)
//                .flatMapMany(manufacturerIdToProductsMap -> {
//                    List<Long> ids = new ArrayList<>(manufacturerIdToProductsMap.keySet());
//                    return manufacturersClient.post()
//                            .uri("/api/manufacturers/search")
//                            .bodyValue(ids)
//                            .retrieve()
//                            .bodyToFlux(Manufacturer.class)
//                            .flatMap(manufacturer -> {
//                                Collection<Product> products =  manufacturerIdToProductsMap.get(manufacturer.getId());
//                                products.forEach(product -> product.setManufacturer(manufacturer));
//                                return Flux.fromIterable(products);
//                            });
//                });

    }
    public Flux<Product> findAllWithManufacturersAndRatings() {

        return productRepository.findAll()
                .flatMap(product -> {
                    // get manufactuter
                    Mono<Manufacturer> manufacturerMono = manufacturersClient.get()
                            .uri("/api/manufacturer/{id}", product.getManufacturerId())
                            .retrieve()
                            .bodyToMono(Manufacturer.class)
                            .onErrorResume(e -> Mono.empty());

                    // get ratings
                    Flux<RatingDTO> ratingFlux = ratingsClient.get()
                            .uri("/api/ratings?productId={id}", product.getId())
                            .retrieve()
                            .bodyToFlux(RatingDTO.class)
                            .onErrorResume(e -> Flux.empty());

                    // zip
                    return Mono.zip(manufacturerMono, ratingFlux.collectList())
                            .map(tuple -> {
                                product.setManufacturer(tuple.getT1());
                                product.setRatings(tuple.getT2());
                                return product;
                            })
                            .defaultIfEmpty(product);
                });
    }

    public Mono<Product> fetchRemoteProductById(Long id) {
        return null;
    }



    // Spring security


    // Enviar datos a Apache Kafka

    // Recibir datos de kafka


    // webClient traer el fabricante de un producto
    // webClient traer el fabricante y las reseñas del producto: zip para traer las peticiones a la vez
}
