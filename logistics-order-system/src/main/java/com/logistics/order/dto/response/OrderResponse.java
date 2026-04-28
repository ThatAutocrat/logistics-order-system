package com.logistics.order.dto.response;

import com.logistics.order.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "Full order details including status history")
public class OrderResponse {

    @Schema(description = "Unique order identifier")
    private String id;

    // Customer
    @Schema(description = "Customer's full name")
    private String customerName;

    @Schema(description = "Customer's email address")
    private String customerEmail;

    @Schema(description = "Customer's phone number")
    private String customerPhone;

    // Addresses
    @Schema(description = "Pickup address")
    private String pickupAddress;

    @Schema(description = "Delivery address")
    private String deliveryAddress;

    // Metadata
    @Schema(description = "Order priority level")
    private String priority;

    @Schema(description = "Package weight in kg")
    private Double weightKg;

    @Schema(description = "Additional notes")
    private String notes;

    // Status
    @Schema(description = "Current order status")
    private OrderStatus status;

    @Schema(description = "Next valid status in the lifecycle (null if delivered)")
    private OrderStatus nextStatus;

    // Timestamps
    @Schema(description = "Order creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    // History
    @Schema(description = "Full status change history")
    private List<StatusHistoryResponse> statusHistory;
}
