package com.logistics.order.exception;

public class OrderAlreadyDeliveredException extends RuntimeException {
    public OrderAlreadyDeliveredException(String orderId) {
        super("Order '" + orderId + "' has already been DELIVERED and cannot be updated further.");
    }
}
