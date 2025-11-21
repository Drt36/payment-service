package com.xuno.payment.payment.controller;

import com.xuno.payment.common.annotation.ApiDefaultErrors;
import com.xuno.payment.common.dto.GlobalApiResponse;
import com.xuno.payment.payment.model.dto.PaymentDetailResponse;
import com.xuno.payment.payment.model.dto.PaymentRequest;
import com.xuno.payment.payment.model.dto.PaymentResponse;
import com.xuno.payment.payment.model.dto.StatusUpdateRequest;
import com.xuno.payment.payment.model.enums.PaymentStatus;
import com.xuno.payment.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment", description = "API for managing payment requests")
@ApiDefaultErrors
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @PostMapping
    @Operation(summary = "Create payment request", 
               description = "Create a new payment request. System will verify and calculate fees automatically.")
    @ApiResponse(responseCode = "201", description = "Payment request created successfully")
    public ResponseEntity<GlobalApiResponse<PaymentResponse>> create(
            @Valid @RequestBody PaymentRequest request,
            @RequestHeader(value = "X-Admin-Id", required = false) String adminId) {
        
        PaymentResponse response = service.create(request, adminId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalApiResponse.success(response, HttpStatus.CREATED));
    }

    @PatchMapping("/{id}/validate")
    @Operation(summary = "Update payment status", 
               description = "Admin approves or rejects a payment request")
    @ApiResponse(responseCode = "200", description = "Payment status updated successfully")
    public ResponseEntity<GlobalApiResponse<PaymentResponse>> validate(
            @PathVariable String id,
            @Valid @RequestBody StatusUpdateRequest request,
            @RequestHeader(value = "X-Admin-Id", required = false) String adminId) {
        
        PaymentResponse response = service.validate(id, request, adminId);
        
        return ResponseEntity.ok(GlobalApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "List payments", 
               description = "Retrieve payments with optional filters (status, date range, sender reference)")
    @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    public ResponseEntity<GlobalApiResponse<Page<PaymentResponse>>> findAll(
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(required = false) String senderReference,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentResponse> responses = service.findAll(status, dateFrom, dateTo, senderReference, pageable);
        
        return ResponseEntity.ok(GlobalApiResponse.success(responses));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", 
               description = "Retrieve a specific payment by its ID with status history")
    @ApiResponse(responseCode = "200", description = "Payment retrieved successfully")
    public ResponseEntity<GlobalApiResponse<PaymentDetailResponse>> find(@PathVariable String id) {
        
        PaymentDetailResponse response = service.find(id);
        
        return ResponseEntity.ok(GlobalApiResponse.success(response));
    }
}
