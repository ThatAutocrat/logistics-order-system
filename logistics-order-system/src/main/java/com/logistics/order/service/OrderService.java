package com.logistics.order.service;

import com.logistics.order.dto.request.CreateOrderRequest;
import com.logistics.order.dto.request.UpdateStatusRequest;
import com.logistics.order.dto.response.OrderResponse;
import com.logistics.order.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    /**
     * Create a new logistics order.
     * Throws DuplicateOrderException if same customer+pickup+delivery exists.
     */
    OrderResponse createOrder(CreateOrderRequest request);

    /**
     * Retrieve a single order by its ID.
     * Throws OrderNotFoundException if not found.
     */
    OrderResponse getOrderById(String orderId);

    /**
     * List all orders, optionally filtered by status.
     */
    List<OrderResponse> getAllOrders(OrderStatus status);

    /**
     * Advance an order's status following the valid lifecycle.
     * Throws InvalidStatusTransitionException for illegal transitions.
     * Throws OrderAlreadyDeliveredException if order is already DELIVERED.
     */
    OrderResponse updateOrderStatus(String orderId, UpdateStatusRequest request);
}
