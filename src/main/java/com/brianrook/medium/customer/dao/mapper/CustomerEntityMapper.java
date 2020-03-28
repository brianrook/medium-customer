package com.brianrook.medium.customer.dao.mapper;

import com.brianrook.medium.customer.dao.entity.CustomerEntity;
import com.brianrook.medium.customer.service.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomerEntityMapper {
    CustomerEntityMapper INSTANCE = Mappers.getMapper(CustomerEntityMapper.class);

    CustomerEntity customerToCustomerEntity(Customer customer);
    Customer customerEntityToCustomer(CustomerEntity customerEntity);
}
