package com.brianrook.medium.customer.config;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CustomerCreateBinding {

    @Output("customerCreateChannel")
    MessageChannel customerCreate();
}
