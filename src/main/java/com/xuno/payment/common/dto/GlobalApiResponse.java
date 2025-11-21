package com.xuno.payment.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Global API response wrapper for standardizing response structure.
 * Provides a consistent format for all API responses.
 *
 * @param <T> The type of data being returned
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper")
public class GlobalApiResponse<T> {

    @Schema(description = "Indicates if the request was successful", 
            example = "true", 
            allowableValues = {"true", "false"}, 
            required = true)
    private boolean success;

    @Schema(description = "Response message", example = "Retrieved successfully")
    private String message;

    @Schema(description = "HTTP status code", example = "200")
    private int status;

    @Schema(description = "Response data (varies by endpoint)")
    private T data;

    @Schema(description = "Timestamp of the response", 
            example = "2025-11-21T00:43:51.105083",
            format = "date-time")
    private LocalDateTime timestamp;

    public static <T> GlobalApiResponse<T> success(T data, HttpStatus status) {
        GlobalApiResponse<T> response = new GlobalApiResponse<>();
        response.setSuccess(true);
        response.setStatus(status.value());
        response.setMessage("Success");
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    public static <T> GlobalApiResponse<T> success(T data) {
        return success(data, HttpStatus.OK);
    }

    public static <T> GlobalApiResponse<T> error(T data, HttpStatus status, String message) {
        GlobalApiResponse<T> response = new GlobalApiResponse<>();
        response.setSuccess(false);
        response.setStatus(status.value());
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    public static <T> GlobalApiResponse<T> error(HttpStatus status, String message) {
        return error(null, status, message);
    }
}
