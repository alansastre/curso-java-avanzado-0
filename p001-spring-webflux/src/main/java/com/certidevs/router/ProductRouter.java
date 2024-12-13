package com.certidevs.router;

import com.certidevs.handler.ProductHandler;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ProductRouter {

    @Bean
    public RouterFunction<ServerResponse> route(ProductHandler handler) {

        return RouterFunctions.route()
                .path("/api/route/products", builder -> builder
                        .GET("", handler::findAll)
                        .GET("/paginated", handler::findAllPaginated)
                        .GET("{id}", handler::findById)
                        .POST("", handler::create)
                        .PUT("{id}", handler::update)
                        .DELETE("{id}", handler::deleteById)
                        .GET("increase/{percentage}", handler::increasePriceOfActiveProducts)
                        .GET("reduce/{id}", handler::reduceQuantity)

//                        .PUT("", )
//                        .DELETE("", )
//                                .filter()
//                                .onError()
                ).build();
    }

//    @Bean
//    public GroupedOpenApi productApi() {
//        return GroupedOpenApi.builder()
//                .group("Products")
//                .pathsToMatch("/api/route/products/**")
//                .build();
//    }
}
