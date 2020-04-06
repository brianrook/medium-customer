package com.brianrook.medium.customer.messaging.message;

import lombok.Data;

@Data
public class CustomerCreatedMessage {
    private Long customerId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
}
