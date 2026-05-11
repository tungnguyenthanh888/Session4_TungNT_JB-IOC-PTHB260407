package com.session4.productservice.service;

import com.session4.productservice.entity.Product;
import com.session4.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;

    public List<Product> findProducts()
    {
        return repository.findAll();
    }
}
