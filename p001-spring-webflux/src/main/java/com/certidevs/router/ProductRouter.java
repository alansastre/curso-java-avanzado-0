package com.certidevs.router;

import com.certidevs.handler.ProductHandler;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Slf4j
@Configuration
public class ProductRouter {

    @Bean
    public RouterFunction<ServerResponse> route(ProductHandler handler) {


        // filtro para logging
        HandlerFilterFunction<ServerResponse, ServerResponse> loggingFilter = (request, next) -> {
            log.info("Request: {} {}", request.method(), request.uri());
            return next.handle(request).doOnNext(response -> log.info("Response status {}", response.statusCode()));
        };

        // filtro para seguridad
        HandlerFilterFunction<ServerResponse, ServerResponse> authenticationFilter = (request, next) -> {
            String authorization = request.headers().firstHeader("Authorization");
            if (authorization != null && authorization.startsWith("Bearer ")) {
                // validar la firma del token JWT con libería jjwt
                boolean isValidToken = true;
                if (isValidToken)
                    return next.handle(request);
                else
                    return ServerResponse.status(HttpStatus.FORBIDDEN).bodyValue("Forbidden"); // 403 si hay token, se sabe quien es, pero no tiene permisos
            } else {
                return ServerResponse.status(HttpStatus.UNAUTHORIZED).bodyValue("Unauthorized"); // 401 si no hay token y no se puede identificar

            }
        };

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
                        .filter(loggingFilter)
//                        .filter(authenticationFilter)
                        .onError(IllegalArgumentException.class, (e, serverRequest) -> ServerResponse.badRequest().bodyValue("Invalid input"))
                        .onError(Exception.class,  (e, serverRequest) -> ServerResponse.status(HttpStatus.CONFLICT).bodyValue("Error"))

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
