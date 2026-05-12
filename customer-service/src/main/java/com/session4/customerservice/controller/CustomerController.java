package com.session4.customerservice.controller;

import com.netflix.discovery.converters.Auto;
import com.session4.customerservice.dto.request.CustomerRequestDTO;
import com.session4.customerservice.dto.response.ApiResponseDTO;
import com.session4.customerservice.dto.response.CustomerResponseDTO;
import com.session4.customerservice.entity.User;
import com.session4.customerservice.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<CustomerResponseDTO>> register(@RequestBody CustomerRequestDTO payload)
    {
        CustomerResponseDTO newUser = customerService.register(payload);
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(
                ApiResponseDTO.<CustomerResponseDTO>builder()
                        .message("Created new user!")
                        .status(HttpStatus.CREATED)
                        .data(newUser)
                        .build()
        );
    };

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CustomerResponseDTO>> findCustomerByID(@PathVariable long id)
    {
        CustomerResponseDTO newUser = customerService.findUserById(id);
        return ResponseEntity.status(HttpStatus.OK.value()).body(
                ApiResponseDTO.<CustomerResponseDTO>builder()
                        .message("Customer was found!")
                        .status(HttpStatus.OK)
                        .data(newUser)
                        .build()
        );
    };
}
