package com.brianrook.medium.customer.test.controller;

import com.brianrook.medium.customer.controller.dto.CustomerDTO;
import com.brianrook.medium.customer.dao.CustomerDAO;
import com.brianrook.medium.customer.dao.entity.CustomerEntity;
import com.brianrook.medium.customer.config.CustomerCreateBinding;
import com.brianrook.medium.customer.messaging.message.CustomerCreatedMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CustomerControllerTest {
    @Autowired
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
    public void testAddCustomerSuccess() throws URISyntaxException, JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + randomServerPort + customerPath;
        URI uri = new URI(baseUrl);
        CustomerDTO customerDTO = CustomerDTO.builder()
                .firstName("test")
                .lastName("user")
                .email("test.user@test.com")
                .phoneNumber("(123)654-7890")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<CustomerDTO> request = new HttpEntity<>(customerDTO, headers);

        ResponseEntity<CustomerDTO> result = restTemplate.postForEntity(uri, request, CustomerDTO.class);

        //Verify request succeed
        Assertions.assertEquals(201, result.getStatusCodeValue());
        Long newCustomerId = result.getBody().getCustomerId();
        assertTrue(newCustomerId != null && newCustomerId > 0);

        //verify the state of the database
        Optional<CustomerEntity> storedEntityOptional = customerDAO.findById(newCustomerId);
        assertTrue(storedEntityOptional.isPresent());

        //validate AMQP
        Message<String> publishedMessage = (Message<String>)messageCollector
                .forChannel(customerCreateBinding.customerCreate())
                .poll();
        assertThat(publishedMessage).isNotNull();
        ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());

        CustomerCreatedMessage appointmentMessage = om.readValue(
                publishedMessage.getPayload(),
                CustomerCreatedMessage.class);
        assertThat(appointmentMessage.getCustomerId()).isGreaterThan(0l);
    }

    @Test
    public void testAddCustomerConflict() throws URISyntaxException {
        CustomerEntity savedRecord = CustomerEntity.builder()
                .firstName("test")
                .lastName("user")
                .email("test.user@test.com")
                .phoneNumber("(123)654-7890")
                .build();
        customerDAO.save(savedRecord);

        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + randomServerPort + customerPath;
        URI uri = new URI(baseUrl);
        CustomerDTO customerDTO = CustomerDTO.builder()
                .firstName(savedRecord.getFirstName())
                .lastName(savedRecord.getLastName())
                .email(savedRecord.getEmail())
                .phoneNumber(savedRecord.getPhoneNumber())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<CustomerDTO> request = new HttpEntity<>(customerDTO, headers);

        try {
            ResponseEntity<CustomerDTO> result = restTemplate.postForEntity(uri, request, CustomerDTO.class);
            //expect exception
            fail();
        } catch (HttpStatusCodeException e) {
            //Verify request failed
            Assertions.assertEquals(409, e.getRawStatusCode());
        }

        //validate AMQP
        assertThat(messageCollector
                .forChannel(customerCreateBinding.customerCreate())
                .isEmpty()).isTrue();
    }

    @Test
    public void testGetCustomerByIdSuccess() throws URISyntaxException {
        //given
        CustomerEntity savedRecord = CustomerEntity.builder()
                .firstName("test")
                .lastName("user")
                .email("test.user@test.com")
                .phoneNumber("(123)654-7890")
                .build();
        CustomerEntity savedCustomer = customerDAO.save(savedRecord);


        //when
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<CustomerDTO> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + randomServerPort + customerPath + savedCustomer.getCustomerId();
        URI uri = new URI(baseUrl);

        ResponseEntity<CustomerDTO> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                request,
                CustomerDTO.class);

        //then
        CustomerDTO customerDTO = result.getBody();
        //Verify request succeed
        Assertions.assertEquals(200, result.getStatusCodeValue());
        assertEquals(savedRecord.getCustomerId(), customerDTO.getCustomerId());
        assertEquals(savedRecord.getEmail(), customerDTO.getEmail());
        assertEquals(savedRecord.getFirstName(), customerDTO.getFirstName());
        assertEquals(savedRecord.getLastName(), customerDTO.getLastName());
        assertEquals(savedRecord.getPhoneNumber(), customerDTO.getPhoneNumber());
    }

    @Test
    public void testGetCustomerByIdNonExistant() throws URISyntaxException {
        //given

        //when
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<CustomerDTO> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + randomServerPort + customerPath + "123";
        URI uri = new URI(baseUrl);

        ResponseEntity<CustomerDTO> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                request,
                CustomerDTO.class);

        //then
        CustomerDTO customerDTO = result.getBody();
        //Verify request succeed
        Assertions.assertEquals(200, result.getStatusCodeValue());
        assertNull(customerDTO);
    }

    @Test
    public void testGetAllCustomerSuccess() throws URISyntaxException {
        //given
        CustomerEntity savedRecord = CustomerEntity.builder()
                .firstName("test")
                .lastName("user")
                .email("test.user@test.com")
                .phoneNumber("(123)654-7890")
                .build();
        CustomerEntity savedCustomer = customerDAO.save(savedRecord);
        CustomerEntity savedRecord1 = CustomerEntity.builder()
                .firstName("test1")
                .lastName("user1")
                .email("test1.user1@test.com")
                .phoneNumber("(123)654-7890")
                .build();
        CustomerEntity savedCustomer1 = customerDAO.save(savedRecord1);


        //when
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<CustomerDTO> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + randomServerPort + customerPath + "all";
        URI uri = new URI(baseUrl);

        ResponseEntity<List<CustomerDTO>> result = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<List<CustomerDTO>>() {
                });

        //then
        List<CustomerDTO> customerDTOList = result.getBody();
        //Verify request succeed
        Assertions.assertEquals(200, result.getStatusCodeValue());
        assertEquals(2, customerDTOList.size());
        assertEquals(savedRecord.getCustomerId(), customerDTOList.get(0).getCustomerId());
        assertEquals(savedRecord.getEmail(), customerDTOList.get(0).getEmail());
        assertEquals(savedRecord.getFirstName(), customerDTOList.get(0).getFirstName());
        assertEquals(savedRecord.getLastName(), customerDTOList.get(0).getLastName());
        assertEquals(savedRecord.getPhoneNumber(), customerDTOList.get(0).getPhoneNumber());

        assertEquals(savedRecord1.getCustomerId(), customerDTOList.get(1).getCustomerId());
        assertEquals(savedRecord1.getEmail(), customerDTOList.get(1).getEmail());
        assertEquals(savedRecord1.getFirstName(), customerDTOList.get(1).getFirstName());
        assertEquals(savedRecord1.getLastName(), customerDTOList.get(1).getLastName());
        assertEquals(savedRecord1.getPhoneNumber(), customerDTOList.get(1).getPhoneNumber());
    }
}
