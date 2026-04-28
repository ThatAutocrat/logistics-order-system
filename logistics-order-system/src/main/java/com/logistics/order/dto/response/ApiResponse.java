package com.logistics.order.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {

    @Schema(description = "Whether the request was successful")
    private final boolean success;

    @Schema(description = "Human-readable message")
    private final String message;

    @Schema(description = "Response payload")
    private final T data;

    @Schema(description = "Error details, present only on failure")
    private final Object errors;

    @Schema(description = "Response timestamp")
    private final LocalDateTime timestamp;

    private ApiResponse(boolean success, String message, T data, Object errors) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operation successful", data, null);
    }

    public static <T> ApiResponse<T> failure(String message, Object errors) {
        return new ApiResponse<>(false, message, null, errors);
    }

    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(false, message, null, null);
    }
}
