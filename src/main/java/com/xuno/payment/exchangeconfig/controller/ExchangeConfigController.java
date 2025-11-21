package com.xuno.payment.exchangeconfig.controller;

import com.xuno.payment.common.annotation.ApiDefaultErrors;
import com.xuno.payment.common.dto.GlobalApiResponse;
import com.xuno.payment.exchangeconfig.model.dto.ExchangeConfigRequest;
import com.xuno.payment.exchangeconfig.model.dto.ExchangeConfigResponse;
import com.xuno.payment.exchangeconfig.service.ExchangeConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exchange-configs")
@Tag(name = "Exchange Configuration", description = "API for managing exchange rate and fee configurations")
@ApiDefaultErrors
@RequiredArgsConstructor
public class ExchangeConfigController {

    private final ExchangeConfigService service;

    @PostMapping
    @Operation(summary = "Create exchange configuration",
               description = "Create a new exchange rate and fee configuration")
    @ApiResponse(responseCode = "201", description = "Exchange configuration created successfully")
    public ResponseEntity<GlobalApiResponse<ExchangeConfigResponse>> create(
            @Valid @RequestBody ExchangeConfigRequest request) {

        ExchangeConfigResponse response = service.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GlobalApiResponse.success(response, HttpStatus.CREATED));
    }

    @GetMapping
    @Operation(summary = "List all exchange configurations",
               description = "Retrieve all exchange rate configurations")
    @ApiResponse(responseCode = "200", description = "List of exchange configurations retrieved successfully")
    public ResponseEntity<GlobalApiResponse<List<ExchangeConfigResponse>>> findAll() {

        List<ExchangeConfigResponse> responses = service.findAll();

        return ResponseEntity.ok(GlobalApiResponse.success(responses));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get exchange configuration by ID",
               description = "Retrieve a specific exchange configuration")
    @ApiResponse(responseCode = "200", description = "Exchange configuration retrieved successfully")
    public ResponseEntity<GlobalApiResponse<ExchangeConfigResponse>> find(
            @PathVariable String id) {

        ExchangeConfigResponse response = service.find(id);

        return ResponseEntity.ok(GlobalApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update exchange configuration",
               description = "Update an existing exchange rate configuration")
    @ApiResponse(responseCode = "200", description = "Exchange configuration updated successfully")
    public ResponseEntity<GlobalApiResponse<ExchangeConfigResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody ExchangeConfigRequest request) {

        ExchangeConfigResponse response = service.update(id, request);

        return ResponseEntity.ok(GlobalApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete exchange configuration",
               description = "Delete an exchange rate configuration")
    @ApiResponse(responseCode = "204", description = "Exchange configuration deleted successfully")
    public ResponseEntity<Void> delete(@PathVariable String id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
