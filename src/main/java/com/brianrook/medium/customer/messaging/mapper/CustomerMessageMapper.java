package com.brianrook.medium.customer.messaging.mapper;

import com.brianrook.medium.customer.messaging.message.CustomerCreatedMessage;
import com.brianrook.medium.customer.service.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomerMessageMapper {
    CustomerMessageMapper INSTANCE = Mappers.getMapper(CustomerMessageMapper.class);

    CustomerCreatedMessage customerToCustomerCreatedMessage(Customer customer);
}
