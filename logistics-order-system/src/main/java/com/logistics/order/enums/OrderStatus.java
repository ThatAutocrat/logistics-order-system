package com.logistics.order.enums;

import java.util.List;
import java.util.Map;

public enum OrderStatus {

    CREATED,
    PICKED_UP,
    IN_TRANSIT,
    DELIVERED;

    // Defines valid forward transitions for each status
    private static final Map<OrderStatus, List<OrderStatus>> VALID_TRANSITIONS = Map.of(
            CREATED,    List.of(PICKED_UP),
            PICKED_UP,  List.of(IN_TRANSIT),
            IN_TRANSIT, List.of(DELIVERED),
            DELIVERED,  List.of()
    );

    /**
     * Returns true if transitioning from this status to the given next status is allowed.
     */
    public boolean canTransitionTo(OrderStatus next) {
        return VALID_TRANSITIONS.getOrDefault(this, List.of()).contains(next);
    }

    /**
     * Returns the next valid status in the lifecycle, or null if terminal.
     */
    public OrderStatus nextStatus() {
        List<OrderStatus> next = VALID_TRANSITIONS.getOrDefault(this, List.of());
        return next.isEmpty() ? null : next.get(0);
    }
}
