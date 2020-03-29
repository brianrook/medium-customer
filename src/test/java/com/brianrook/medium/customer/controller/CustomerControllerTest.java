package com.brianrook.medium.customer.controller;

import com.brianrook.medium.customer.controller.dto.CustomerDTO;
import com.brianrook.medium.customer.dao.CustomerDAO;
import com.brianrook.medium.customer.dao.entity.CustomerEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CustomerControllerTest {
    @Autowired
    private CustomerDAO customerDAO;
    @LocalServerPort
    int randomServerPort;


    String customerPath = "/customer/";

    @BeforeEach
    public void setUp(){
        customerDAO.deleteAll();
    }

    @Test
    public void testAddCustomerSuccess() throws URISyntaxException
    {
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:"+randomServerPort+customerPath;
        URI uri = new URI(baseUrl);
        CustomerDTO customerDTO = CustomerDTO.builder()
                .firstName("test")
                .lastName("user")
                .email("test.user@test.com")
                .phoneNumber("123-654-7890")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<CustomerDTO> request = new HttpEntity<>(customerDTO, headers);

        ResponseEntity<CustomerDTO> result = restTemplate.postForEntity(uri, request, CustomerDTO.class);

        //Verify request succeed
        Assertions.assertEquals(201, result.getStatusCodeValue());
        Long newCustomerId = result.getBody().getCustomerId();
        assertTrue(newCustomerId!=null && newCustomerId>0);

        //verify the state of the database
        Optional<CustomerEntity> storedEntityOptional = customerDAO.findById(newCustomerId);
        assertTrue(storedEntityOptional.isPresent());
    }

}
