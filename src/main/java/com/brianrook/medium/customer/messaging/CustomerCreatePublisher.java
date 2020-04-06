package com.brianrook.medium.customer.messaging;

import com.brianrook.medium.customer.config.CustomerCreateBinding;
import com.brianrook.medium.customer.config.LoggingEnabled;
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
@LoggingEnabled
public class CustomerCreatePublisher {

    @Autowired
    CustomerCreateBinding customerCreateBinding;

    public void publishCustomerCreate(Customer customer) {
        CustomerCreatedMessage customerMessage = CustomerMessageMapper.INSTANCE.customerToCustomerCreatedMessage(customer);
        Message<CustomerCreatedMessage> msg = MessageBuilder.withPayload(customerMessage).build();

        customerCreateBinding.customerCreate().send(msg);
    }
}
