package com.xuno.payment.health;

import com.xuno.payment.common.annotation.ApiDefaultErrors;
import com.xuno.payment.common.dto.GlobalApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Health check endpoints")
public class HealthController {

    @GetMapping
    @ApiDefaultErrors
    @Operation(summary = "Check server health", description = "Returns server status to verify if the service is up and running")
    public ResponseEntity<GlobalApiResponse<Map<String, String>>> health() {
        Map<String, String> healthData = new HashMap<>();
        healthData.put("status", "UP");
        healthData.put("message", "Server is running");
        
        GlobalApiResponse<Map<String, String>> response = GlobalApiResponse.success(healthData);
        
        return ResponseEntity.ok(response);
    }
}

