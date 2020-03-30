package com.brianrook.medium.customer.controller.mapper;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerDTOMapperTest {
    @Test
    public void testNullCustomer(){
        assertThat(CustomerDTOMapper.INSTANCE.customerToCustomerDTO(null)).isNull();
    }
    @Test
    public void testNullCustomerDTO(){
        assertThat(CustomerDTOMapper.INSTANCE.customerDTOToCustomer(null)).isNull();
    }
}
