package com.certidevs.config;

import com.certidevs.dto.Notification;
import com.certidevs.dto.Order;
import com.certidevs.dto.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/*
Supplier
Function
Consumer
 */
@Slf4j
@Configuration
public class KafkaConfig {

    // Se pueden producir datos creando beans o desde clases controller o service utilizan StreamBridge

    // Producer de String: emitir datos cada 5 segundos
//    @Bean
//    public Supplier<Flux<String>> stringProducer() {
//        return () -> Flux.interval(Duration.ofSeconds(5))
//                .map(tick -> "Texto generado número " + tick)
//                .doOnNext(mensaje -> log.info("stringProducer: Mensaje emitido: {}", mensaje))
//                .onErrorResume(e -> {
//                    log.error("Ha ocurrido un error", e);
//                    return Flux.empty();
//                });
//    }
//
//    @Bean
//    public Consumer<Flux<String>> stringConsumer() {
//        return flux -> flux
//                .doOnNext(mensaje -> {
//                    log.info("stringConsumer: recibido string {}", mensaje);
//                    // guardar en base de datos
//                    // llamar a otro microservicio usando webclient
//
//                    // Si ocurre error onErrorContinue ignora el error y sigue con el resto sin romper el consumer
//                    // Spring Cloud Stream no reintentará ni emitirá el valor a un topic DLQ
//                }).onErrorContinue((throwable, o) -> {
//                    log.error("Error procesando mensaje");
//                })
//                .subscribe(message -> {
//                    log.info("stringConsumer subscribe {}", message);
//                });
//    }

    // PRoducer de productos
//    @Bean
//    public Supplier<Flux<Product>> productProducer() {
//        return () -> Flux.interval(Duration.ofSeconds(5))
//                .map(tick -> {
//                    if (tick == 5) throw new RuntimeException("Excepción productProducer simulada");
//
//                    var product = new Product(tick, "Product_" + tick, 34.50 + tick);
//                    log.info("productProducer creado product {}" , product);
//                    return product;
//                })
//                .onErrorContinue((e, o) -> {
//                    log.error("productProducer onErrorContinue error", e);
//                });
//    }
//
//    // Consumer de productos
//    @Bean
//    public Consumer<Flux<Product>> productConsumer() {
//        return flux -> flux
//                .index()
//                .doOnNext(tuple -> {
//                    Long index = tuple.getT1();
//                    Product product = tuple.getT2();
//
//                    if (index == 5) throw new RuntimeException("Excepción productConsumer simulada");
//
//                    log.info("Consumer productConsumer recibió Product {} {}", index, product);
//
//                    // guardar en base de datos el producto
//                    // hacer alguna operación con el producto
//                }).onErrorContinue((e, o) -> {
//                    log.error("productConsumer onErrorContinue error", e);
//
//                }).subscribe(tuple -> {
//                    log.info("productConsumer subscribe {}", tuple.getT2());
//                });
//    }

    // Producer de Order

    @Bean
    public Supplier<Flux<Order>> orderProducer() {
        return () -> Flux.interval(Duration.ofSeconds(5))
                .map(tick -> {

                    int amount = (int)(1 + tick);
                    if (tick == 4) {
                        amount = -1; // Ponemos número negativo para simular error en el consumer orderProcessor
                    }

                    var order = new Order(tick, "deposit", amount);
                    log.info("orderProducer creado order {}" , order);
                    return order;
                })
                .doOnNext(order -> log.info("orderProducer Emitido order {}", order))
                .onErrorResume(e -> {
                    log.error("Error orderProducer", e);
                    return Flux.empty();
                });
    }

    // consumer de Order que también produce Notification
    // Estrategia 1: onError continue no se propaga a Spring Cloud Steam, sin DLQ
//    @Bean
//    public Function<Flux<Order>, Flux<Notification>> orderProcessor() {
//
//        return orderFlux -> orderFlux.flatMap(order -> {
//
//            if (order.amount() < 0) throw new RuntimeException("orderProcessor Cantidad no válida: " + order.amount());
//
//            // operaciones
//            Notification notification = new Notification("admin@localhost", "Order procesado correctamente");
//            log.info("orderProcessor creado notification {}", notification);
//            return Flux.just(notification);
//
//
//        }).doOnError(e -> {
//            // se ejecuta cuando el flatmap lanza excepción
//            log.error("orderProcessor Error creando o procesando order");
//        })
//        // Si hacemos onErrorContinue se gestiona aquí el error y no se propaga a Spring cloud stream ni DLQ
//        .onErrorContinue((throwable, o) -> log.info("orderProcessor onErrorContinue"));
//    }


    // Estrategia 2: se propaga a Spring Cloud Stream, con DLQ, NO USAR onErrorContinue
    @Bean
    public Function<Order, Notification> orderProcessor() {
        return order -> {
            if (order.amount() < 0) {
                // Lanzar excepción y que se propague a Spring Cloud Stream para que mueva el dato a topic DLQ
                throw new RuntimeException("Cantidad incorrecta " + order.amount());
            }

            // logica de negocio
            return new Notification("admin@localhost", "Mensaje");

        };
    }





    // consumer de notification
    @Bean
    public Consumer<Flux<Notification>> notificationConsumer() {
        return flux -> flux
                .doOnNext(notification -> {

                    log.info("Consumer notificationConsumer recibió Notification {} {}", notification);

                    // guardar en base de datos el producto
                    // hacer alguna operación con el producto
                }).onErrorContinue((e, o) -> {
                    log.error("productConsumer onErrorContinue error", e);

                }).subscribe(notification -> {
                    log.info("notificationConsumer subscribe {}", notification);
                });
    }



}
