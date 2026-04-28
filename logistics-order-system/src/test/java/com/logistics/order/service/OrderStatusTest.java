package com.logistics.order.service;

import com.logistics.order.enums.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrderStatus Lifecycle Tests")
class OrderStatusTest {

    @Test
    @DisplayName("CREATED can only transition to PICKED_UP")
    void createdCanTransitionToPickedUp() {
        assertThat(OrderStatus.CREATED.canTransitionTo(OrderStatus.PICKED_UP)).isTrue();
        assertThat(OrderStatus.CREATED.canTransitionTo(OrderStatus.IN_TRANSIT)).isFalse();
        assertThat(OrderStatus.CREATED.canTransitionTo(OrderStatus.DELIVERED)).isFalse();
    }

    @Test
    @DisplayName("PICKED_UP can only transition to IN_TRANSIT")
    void pickedUpCanTransitionToInTransit() {
        assertThat(OrderStatus.PICKED_UP.canTransitionTo(OrderStatus.IN_TRANSIT)).isTrue();
        assertThat(OrderStatus.PICKED_UP.canTransitionTo(OrderStatus.CREATED)).isFalse();
        assertThat(OrderStatus.PICKED_UP.canTransitionTo(OrderStatus.DELIVERED)).isFalse();
    }

    @Test
    @DisplayName("IN_TRANSIT can only transition to DELIVERED")
    void inTransitCanTransitionToDelivered() {
        assertThat(OrderStatus.IN_TRANSIT.canTransitionTo(OrderStatus.DELIVERED)).isTrue();
        assertThat(OrderStatus.IN_TRANSIT.canTransitionTo(OrderStatus.CREATED)).isFalse();
        assertThat(OrderStatus.IN_TRANSIT.canTransitionTo(OrderStatus.PICKED_UP)).isFalse();
    }

    @Test
    @DisplayName("DELIVERED cannot transition to any status")
    void deliveredHasNoValidTransitions() {
        assertThat(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.CREATED)).isFalse();
        assertThat(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.PICKED_UP)).isFalse();
        assertThat(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.IN_TRANSIT)).isFalse();
        assertThat(OrderStatus.DELIVERED.nextStatus()).isNull();
    }

    @Test
    @DisplayName("nextStatus() follows correct sequence")
    void nextStatusFollowsSequence() {
        assertThat(OrderStatus.CREATED.nextStatus()).isEqualTo(OrderStatus.PICKED_UP);
        assertThat(OrderStatus.PICKED_UP.nextStatus()).isEqualTo(OrderStatus.IN_TRANSIT);
        assertThat(OrderStatus.IN_TRANSIT.nextStatus()).isEqualTo(OrderStatus.DELIVERED);
        assertThat(OrderStatus.DELIVERED.nextStatus()).isNull();
    }
}
