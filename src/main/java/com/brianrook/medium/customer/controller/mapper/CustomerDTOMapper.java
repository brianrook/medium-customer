package com.brianrook.medium.customer.controller.mapper;

import com.brianrook.medium.customer.controller.dto.CustomerDTO;
import com.brianrook.medium.customer.service.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import javax.annotation.Generated;
import java.util.List;

@Mapper
@Generated("mapstruct")
public interface CustomerDTOMapper {
    CustomerDTOMapper INSTANCE = Mappers.getMapper(CustomerDTOMapper.class);

    CustomerDTO customerToCustomerDTO(Customer customer);

    Customer customerDTOToCustomer(CustomerDTO customerEntity);

    List<CustomerDTO> customerListToCustomerDTOList(List<Customer> customerList);
}
