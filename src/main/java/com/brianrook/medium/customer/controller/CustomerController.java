package com.brianrook.medium.customer.controller;

import com.brianrook.medium.customer.config.LoggingEnabled;
import com.brianrook.medium.customer.controller.dto.CustomerDTO;
import com.brianrook.medium.customer.controller.mapper.CustomerDTOMapper;
import com.brianrook.medium.customer.exception.CreateCustomerException;
import com.brianrook.medium.customer.service.CustomerService;
import com.brianrook.medium.customer.service.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/customer")
@Slf4j
@LoggingEnabled
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @PostMapping(value = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerDTO> saveCustomer(
            @RequestBody @Valid CustomerDTO customerDTO) {
        try {
            Customer customer = CustomerDTOMapper.INSTANCE.customerDTOToCustomer(customerDTO);

            Customer savedCustomer = customerService.saveCustomer(customer);

            CustomerDTO savedCustomerDTO = CustomerDTOMapper.INSTANCE.customerToCustomerDTO(savedCustomer);
            return new ResponseEntity<CustomerDTO>(savedCustomerDTO, HttpStatus.CREATED);
        } catch (CreateCustomerException e) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
    }

    @GetMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerDTO> getCustomerById(
            @PathVariable("id") Long id) {

        Optional<Customer> customerOptional = customerService.getCustomerById(id);

        if (customerOptional.isPresent()) {
            CustomerDTO savedCustomerDTO = CustomerDTOMapper.INSTANCE.customerToCustomerDTO(customerOptional.get());
            return new ResponseEntity<CustomerDTO>(savedCustomerDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.OK);
        }
    }

    @GetMapping(value = "/all",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CustomerDTO>> getCustomerById() {

        List<Customer> customerList = customerService.getAllCustomers();

        List<CustomerDTO> customerDTOList = CustomerDTOMapper.INSTANCE.customerListToCustomerDTOList(customerList);
        return new ResponseEntity<List<CustomerDTO>>(customerDTOList, HttpStatus.OK);
    }
}
