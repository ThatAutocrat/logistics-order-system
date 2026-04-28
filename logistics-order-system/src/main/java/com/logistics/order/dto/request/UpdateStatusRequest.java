package com.logistics.order.dto.request;

import com.logistics.order.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request payload for updating an order's status")
public class UpdateStatusRequest {

    @NotNull(message = "New status is required")
    @Schema(description = "The new status to transition the order to", example = "PICKED_UP")
    private OrderStatus newStatus;

    @Schema(description = "Optional remarks about the status change", example = "Package collected from sender")
    private String remarks;
}
