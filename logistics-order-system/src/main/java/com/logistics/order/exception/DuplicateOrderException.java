package com.logistics.order.exception;

public class DuplicateOrderException extends RuntimeException {
    public DuplicateOrderException(String customerEmail) {
        super(String.format(
            "A duplicate order already exists for customer '%s' with the same pickup and delivery addresses.",
            customerEmail
        ));
    }
}
