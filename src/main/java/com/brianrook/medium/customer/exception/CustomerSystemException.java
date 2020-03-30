package com.brianrook.medium.customer.exception;

public class CustomerSystemException extends RuntimeException{
    public CustomerSystemException(String message){
        super(message);
    }
    public CustomerSystemException(String message, Exception e){
        super(message,e);
    }
}
