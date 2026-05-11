package com.session4.orderservice.controller;

import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class OrderController {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EurekaClient discoveryClient;

    @GetMapping("/order/call-product")
    public String getProductInfo() {
        List<ServiceInstance> instances = discoveryClient.getInstancesById("PRODUCT-SERVICE");

        if (instances != null && !instances.isEmpty()) {
            ServiceInstance serviceInstance = instances.getFirst();
            String baseUrl = serviceInstance.getUri().toString();

            String url = baseUrl + "/api/v1/products";
            return restTemplate.getForObject(url, String.class);
        }

        return "Không tìm thấy instance nào của PRODUCT-SERVICE";
    }
}
