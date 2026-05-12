package com.session4.customerservice.service;

import com.session4.customerservice.dto.request.CustomerRequestDTO;
import com.session4.customerservice.dto.response.CustomerResponseDTO;
import com.session4.customerservice.entity.User;
import com.session4.customerservice.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public CustomerResponseDTO register(CustomerRequestDTO payload)
    {
        User user = new User();
        user.setEmail(payload.getEmail());
        user.setFullName(payload.getFullName());
        user.setPassword(payload.getPassword());

        User newUser = customerRepository.save(user);
        return CustomerResponseDTO.builder()
                .email(newUser.getEmail())
                .fullName(newUser.getFullName())
                .build();
    }

    public CustomerResponseDTO login(CustomerRequestDTO payload)
    {
        User user = customerRepository.findByEmail(payload.getEmail()).orElseThrow(() -> new RuntimeException("Email or password was wrong."));

        if(user.getPassword().equals(payload.getPassword()))
        {
            throw new RuntimeException("Email or password was wrong");
        }

        return CustomerResponseDTO.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }

    public CustomerResponseDTO findUserById(long id)
    {
        User user = customerRepository.findById(id).orElseThrow(()-> new NoSuchElementException("Not found UserById"));

        return CustomerResponseDTO.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }
}
