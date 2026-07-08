package com.example.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Explicitly link it to your Bill service's main application class
@SpringBootTest(classes = com.example.bill.OrderApplication.class) 
class BillApplicationTests {

    @Test
    void contextLoads() {
    }

}