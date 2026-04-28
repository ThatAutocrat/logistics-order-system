package com.logistics.order.mapper;

import com.logistics.order.dto.response.OrderResponse;
import com.logistics.order.dto.response.StatusHistoryResponse;
import com.logistics.order.entity.Order;
import com.logistics.order.entity.StatusHistory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        List<StatusHistoryResponse> historyResponses = order.getStatusHistories()
                .stream()
                .map(this::toHistoryResponse)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .customerPhone(order.getCustomerPhone())
                .pickupAddress(order.getPickupAddress())
                .deliveryAddress(order.getDeliveryAddress())
                .priority(order.getPriority())
                .weightKg(order.getWeightKg())
                .notes(order.getNotes())
                .status(order.getStatus())
                .nextStatus(order.getStatus().nextStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .statusHistory(historyResponses)
                .build();
    }

    public StatusHistoryResponse toHistoryResponse(StatusHistory history) {
        return StatusHistoryResponse.builder()
                .fromStatus(history.getFromStatus())
                .toStatus(history.getToStatus())
                .changedAt(history.getChangedAt())
                .remarks(history.getRemarks())
                .build();
    }
}
