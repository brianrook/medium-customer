package com.brianrook.medium.customer.controller;

import com.brianrook.medium.customer.controller.dto.CustomerDTO;
import com.brianrook.medium.customer.controller.mapper.CustomerDTOMapper;
import com.brianrook.medium.customer.service.CustomerService;
import com.brianrook.medium.customer.service.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URISyntaxException;

@Controller
@RequestMapping(value = "/customer")
@Slf4j
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @PostMapping(value = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerDTO> saveCustomer(
            @RequestBody @Valid CustomerDTO customerDTO) throws URISyntaxException {
        Customer customer = CustomerDTOMapper.INSTANCE.customerDTOToCustomer(customerDTO);

        Customer savedCustomer = customerService.saveCustomer(customer);

        CustomerDTO savedCustomerDTO = CustomerDTOMapper.INSTANCE.customerToCustomerDTO(savedCustomer);
        return new ResponseEntity<CustomerDTO>(savedCustomerDTO, HttpStatus.CREATED);
    }
}
