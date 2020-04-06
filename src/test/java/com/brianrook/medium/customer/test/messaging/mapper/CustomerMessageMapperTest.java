package com.brianrook.medium.customer.test.messaging.mapper;

import com.brianrook.medium.customer.dao.mapper.CustomerEntityMapper;
import com.brianrook.medium.customer.messaging.mapper.CustomerMessageMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class CustomerMessageMapperTest {
    @Test
    public void testNullCustomer(){
        Assertions.assertThat(CustomerMessageMapper.INSTANCE.customerToCustomerCreatedMessage(null)).isNull();
    }

}
