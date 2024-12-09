package com.certidevs.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;


}