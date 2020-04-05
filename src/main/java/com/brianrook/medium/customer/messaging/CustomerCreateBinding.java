package com.brianrook.medium.customer.messaging;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CustomerCreateBinding {

    @Output("customerCreateChannel")
    MessageChannel customerCreate();
}
