package com.certidevs.config;

import com.certidevs.dto.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
//@Configuration
public class KafkaMessages {

//    @Bean
//    public Supplier<Message<Product>> productProducer() {
//        return () -> {
//            var product = new Product(1L, "Laptop", 500d);
//            return MessageBuilder.withPayload(product).setHeader("X-Custom-Header", "ValorEjemplo").build();
//        };
//    }
//
//    @Bean
//    public Consumer<Message<Product>> productConsumer() {
//        return message -> {
//            var product = message.getPayload();
//            String customHeader = (String) message.getHeaders().get("X-Custom-Header");
//            log.info("productConsumer recibido payload {} y headers {}", product, customHeader);
//        };
//    }
@Bean
public Supplier<Flux<Message<Product>>> productProducer() {
    return () -> Flux.interval(Duration.ofSeconds(5))
            .map(tick -> {
                var product = new Product(tick, "product_" + tick, 100.0 + tick);
                return MessageBuilder.withPayload(product).setHeader("X-Custom-Header", "ValorEjemplo" + tick).build();
            }).doOnNext(productMessage -> {
                log.info("productProducer emitido message payload {} y headers {}", productMessage.getPayload(), productMessage.getHeaders());
            // Buffer por si el consumidor no puede seguir el ritmo para consumir los mensajes
            }).onBackpressureBuffer(
                    100,
                    message -> log.warn("Buffer de mensajes lleno: descartando mensaje {}", message)
            );
}

@Bean
public Consumer<Flux<Message<Product>>> productConsumer() {
    return flux -> flux
            // Control de backpressure: limitamos el procesamiento, pedimos 10 mensajes a la vez
            .limitRate(10)
//            .flatMapSequential(message -> {
//
//            })
            .concatMap(message -> {
                var product = message.getPayload();
                String customHeader = (String) message.getHeaders().get("X-Custom-Header");
                log.info("productConsumer recibido payload {} y headers {}", product, customHeader);

                return WebClient.create("http://localhost:8080/api/notification").get()
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnNext(response -> log.info(""))
                        .doOnError(e -> log.error(""))
//                        .retry(2)
                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)).doBeforeRetry(retrySignal -> log.warn("Reintento")))
                        .onErrorContinue((throwable, o) -> log.error(""))
                        .map(response -> {
                            // usar la respuesta de webclient para enriquecer la respuesta si vamos a devolver algo
                            // guardar en base de datos
                            return MessageBuilder.withPayload(product).build();
                        }).then();
            }).subscribe(
                    unused -> log.info(""),
                    error -> log.error(""),
                    () -> log.info("")
            );
}




}
