package com.logistics.order.exception;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(String fromStatus, String toStatus) {
        super(String.format(
            "Invalid status transition from '%s' to '%s'. " +
            "Allowed transitions: CREATED → PICKED_UP → IN_TRANSIT → DELIVERED.",
            fromStatus, toStatus
        ));
    }
}
