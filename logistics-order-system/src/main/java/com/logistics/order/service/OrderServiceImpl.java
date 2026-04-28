package com.logistics.order.service;

import com.logistics.order.dto.request.CreateOrderRequest;
import com.logistics.order.dto.request.UpdateStatusRequest;
import com.logistics.order.dto.response.OrderResponse;
import com.logistics.order.entity.Order;
import com.logistics.order.entity.StatusHistory;
import com.logistics.order.enums.OrderStatus;
import com.logistics.order.exception.DuplicateOrderException;
import com.logistics.order.exception.InvalidStatusTransitionException;
import com.logistics.order.exception.OrderAlreadyDeliveredException;
import com.logistics.order.exception.OrderNotFoundException;
import com.logistics.order.mapper.OrderMapper;
import com.logistics.order.repository.OrderRepository;
import com.logistics.order.repository.StatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.debug("Creating order for customer: {}", request.getCustomerEmail());

        // Duplicate check
        if (orderRepository.existsDuplicateOrder(
                request.getCustomerEmail(),
                request.getPickupAddress(),
                request.getDeliveryAddress())) {
            throw new DuplicateOrderException(request.getCustomerEmail());
        }

        // Build and persist the order
        Order order = Order.builder()
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .customerPhone(request.getCustomerPhone())
                .pickupAddress(request.getPickupAddress())
                .deliveryAddress(request.getDeliveryAddress())
                .priority(request.getPriority())
                .weightKg(request.getWeightKg())
                .notes(request.getNotes())
                .status(OrderStatus.CREATED)
                .build();

        order = orderRepository.save(order);

        // Record initial status history
        recordStatusHistory(order, null, OrderStatus.CREATED, "Order created");

        log.info("Order created successfully with ID: {}", order.getId());
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String orderId) {
        Order order = findOrderById(orderId);
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders(OrderStatus status) {
        List<Order> orders;
        if (status != null) {
            log.debug("Fetching orders filtered by status: {}", status);
            orders = orderRepository.findByStatusOrderByCreatedAtDesc(status);
        } else {
            log.debug("Fetching all orders");
            orders = orderRepository.findAllByOrderByCreatedAtDesc();
        }
        return orders.stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse updateOrderStatus(String orderId, UpdateStatusRequest request) {
        log.debug("Updating status for order: {} → {}", orderId, request.getNewStatus());

        Order order = findOrderById(orderId);
        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = request.getNewStatus();

        // Terminal state check
        if (currentStatus == OrderStatus.DELIVERED) {
            throw new OrderAlreadyDeliveredException(orderId);
        }

        // Valid transition check
        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(currentStatus.name(), newStatus.name());
        }

        // Apply transition
        order.setStatus(newStatus);
        order = orderRepository.save(order);

        // Record history
        String remarks = (request.getRemarks() != null && !request.getRemarks().isBlank())
                ? request.getRemarks()
                : "Status updated to " + newStatus.name();

        recordStatusHistory(order, currentStatus, newStatus, remarks);

        log.info("Order {} status updated: {} → {}", orderId, currentStatus, newStatus);
        return orderMapper.toResponse(order);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Order findOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    private void recordStatusHistory(Order order, OrderStatus from, OrderStatus to, String remarks) {
        StatusHistory history = StatusHistory.builder()
                .order(order)
                .fromStatus(from)
                .toStatus(to)
                .changedAt(LocalDateTime.now())
                .remarks(remarks)
                .build();
        statusHistoryRepository.save(history);
        order.getStatusHistories().add(history);
    }
}
