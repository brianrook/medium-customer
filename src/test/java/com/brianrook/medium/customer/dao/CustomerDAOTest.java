package com.brianrook.medium.customer.dao;

import com.brianrook.medium.customer.dao.entity.CustomerEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class CustomerDAOTest {
    @Autowired
    private CustomerDAO customerDAO;

    @Test
    public void testSaveCustomer()
    {
        CustomerEntity testCustomer = CustomerEntity.builder()
                .firstName("Brian")
                .lastName("Rook")
                .email("test.user@test.com")
                .phoneNumber("303-555-1212")
                .build();

        customerDAO.save(testCustomer);

        Optional<CustomerEntity> returnCustomer = customerDAO.findById(testCustomer.getCustomerId());

        assertThat(returnCustomer.isPresent()).isTrue();
        assertThat(returnCustomer.get()).isEqualTo(testCustomer);
    }
}

