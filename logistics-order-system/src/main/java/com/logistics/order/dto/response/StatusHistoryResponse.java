package com.logistics.order.dto.response;

import com.logistics.order.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Represents a single status change in the order lifecycle")
public class StatusHistoryResponse {

    @Schema(description = "Previous status (null for initial creation)")
    private OrderStatus fromStatus;

    @Schema(description = "New status after the transition")
    private OrderStatus toStatus;

    @Schema(description = "Timestamp of the status change")
    private LocalDateTime changedAt;

    @Schema(description = "Optional remarks about the transition")
    private String remarks;
}
