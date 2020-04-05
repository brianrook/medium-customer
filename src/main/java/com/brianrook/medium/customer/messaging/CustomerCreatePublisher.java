package com.brianrook.medium.customer.messaging;

import com.brianrook.medium.customer.messaging.mapper.CustomerMessageMapper;
import com.brianrook.medium.customer.messaging.message.CustomerCreatedMessage;
import com.brianrook.medium.customer.service.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(CustomerCreateBinding.class)
public class CustomerCreatePublisher {

    @Autowired
    CustomerCreateBinding customerCreateBinding;

    public void publishCustomerCreate(Customer customer) {
        CustomerCreatedMessage customerMessage = CustomerMessageMapper.INSTANCE.customerToCustomerCreatedMessage(customer);
        Message<CustomerCreatedMessage> msg = MessageBuilder.withPayload(customerMessage).build();

        customerCreateBinding.customerCreate().send(msg);
    }
}
