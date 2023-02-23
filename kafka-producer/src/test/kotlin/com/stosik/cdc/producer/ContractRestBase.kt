package com.stosik.cdc.producer

import com.stosik.kafka.producer.CustomerController
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
abstract class ContractRestBase {

    @Autowired
    private lateinit var customerController: CustomerController

    @BeforeEach
    fun beforeEach() {
        RestAssuredMockMvc.standaloneSetup(customerController)
    }
}