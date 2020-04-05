package com.brianrook.medium.customer.test.controller;

import com.brianrook.medium.customer.controller.dto.CustomerDTO;
import com.brianrook.medium.customer.dao.CustomerDAO;
import com.brianrook.medium.customer.dao.entity.CustomerEntity;
import com.brianrook.medium.customer.config.CustomerCreateBinding;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CustomerControllerMockTest {
    @MockBean
    private CustomerDAO customerDAO;
    @LocalServerPort
    int randomServerPort;

    @Autowired
    MessageCollector messageCollector;
    @Autowired
    CustomerCreateBinding customerCreateBinding;


    String customerPath = "/customer/";

    @BeforeEach
    public void setUp() {
        customerDAO.deleteAll();
    }


    @Test
    public void testAddCustomerDBError() throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + randomServerPort + customerPath;
        URI uri = new URI(baseUrl);
        CustomerDTO customerDTO = CustomerDTO.builder()
                .firstName("test")
                .lastName("user")
                .email("test.user@test.com")
                .phoneNumber("(123)654-7890")
                .build();
        Mockito.when(customerDAO.save(any(CustomerEntity.class))).thenThrow(new QueryTimeoutException("test"));
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<CustomerDTO> request = new HttpEntity<>(customerDTO, headers);

        try {
            restTemplate.postForEntity(uri, request, CustomerDTO.class);
            //expect exception
            fail();
        } catch (HttpStatusCodeException e) {
            //Verify request failed
            Assertions.assertEquals(500, e.getRawStatusCode());
        }
        //validate AMQP
        assertThat(messageCollector
                .forChannel(customerCreateBinding.customerCreate())
                .isEmpty()).isTrue();
    }
}
