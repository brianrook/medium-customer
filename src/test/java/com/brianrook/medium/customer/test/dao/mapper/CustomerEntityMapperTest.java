package com.brianrook.medium.customer.test.dao.mapper;

import com.brianrook.medium.customer.dao.mapper.CustomerEntityMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerEntityMapperTest {
    @Test
    public void testNullCustomer(){
        Assertions.assertThat(CustomerEntityMapper.INSTANCE.customerToCustomerEntity(null)).isNull();
    }
    @Test
    public void testNullCustomerDTO(){
        Assertions.assertThat(CustomerEntityMapper.INSTANCE.customerEntityToCustomer(null)).isNull();
    }
}
