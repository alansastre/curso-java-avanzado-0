package com.certidevs;

import com.certidevs.entity.Product;
import com.certidevs.repository.ProductRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;

import java.time.LocalDateTime;

@SpringBootApplication
public class Main1 {

    public static void main(String[] args) {
        var context = SpringApplication.run(Main1.class, args);
        var productRepo = context.getBean(ProductRepository.class);

        if (productRepo.count().block() == 0) {
            productRepo.save(Product.builder()
                    .title("Product 1")
                    .price(10.0)
                    .quantity(50)
                    .active(true)
                    .creationDate(LocalDateTime.now().minusDays(10))
                    .build()).block();
        }



        // alternativas para insertar datos:

        // var r2dbcEntityTemplate = context.getBean(R2dbcEntityTemplate.class);
        // r2dbcEntityTemplate.insert(product);

        // var databaseClient = context.getBean(DatabaseClient.class);
        // databaseClient.sql();
    }

}
