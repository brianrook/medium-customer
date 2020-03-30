package com.brianrook.medium.customer.dao.mapper;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerEntityMapperTest {
    @Test
    public void testNullCustomer(){
        assertThat(CustomerEntityMapper.INSTANCE.customerToCustomerEntity(null)).isNull();
    }
    @Test
    public void testNullCustomerDTO(){
        assertThat(CustomerEntityMapper.INSTANCE.customerEntityToCustomer(null)).isNull();
    }
}
