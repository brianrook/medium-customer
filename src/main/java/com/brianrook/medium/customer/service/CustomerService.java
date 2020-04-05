package com.brianrook.medium.customer.service;

import com.brianrook.medium.customer.dao.CustomerDAO;
import com.brianrook.medium.customer.dao.entity.CustomerEntity;
import com.brianrook.medium.customer.dao.mapper.CustomerEntityMapper;
import com.brianrook.medium.customer.exception.CreateCustomerException;
import com.brianrook.medium.customer.exception.CustomerSystemException;
import com.brianrook.medium.customer.messaging.CustomerCreatePublisher;
import com.brianrook.medium.customer.service.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CustomerService {

    @Autowired
    CustomerDAO customerDAO;

    @Autowired
    CustomerCreatePublisher customerCreatePublisher;

    public Customer saveCustomer(Customer customer) {
        if (customerExists(customer))
        {
            throw new CreateCustomerException(String.format("customer with email: %s already exists", customer.getEmail()));
        }
        Customer savedCustomer = persistCustomer(customer);
        customerCreatePublisher.publishCustomerCreate(savedCustomer);

        return savedCustomer;
    }

    private boolean customerExists(Customer customer) {
        return customerDAO.findByEmail(customer.getEmail()).isPresent();
    }

    public Customer persistCustomer(Customer customer) {
        try {
            CustomerEntity customerEntity = CustomerEntityMapper.INSTANCE.customerToCustomerEntity(customer);
            CustomerEntity storedEntity = customerDAO.save(customerEntity);
            Customer returnCustomer = CustomerEntityMapper.INSTANCE.customerEntityToCustomer(storedEntity);
            return returnCustomer;
        }catch (DataAccessException e){
            throw new CustomerSystemException("unable to persist customer data: "+e.getMessage(), e);
        }
    }


    public Optional<Customer> getCustomerById(Long id) {
        Optional<CustomerEntity> customerEntityOptional = customerDAO.findById(id);
        if (customerEntityOptional.isPresent()){
            return Optional.of(CustomerEntityMapper.INSTANCE.customerEntityToCustomer(customerEntityOptional.get()));
        } else {
            return Optional.empty();
        }
    }
}
