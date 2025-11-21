package com.xuno.payment.common.annotation;


import com.xuno.payment.common.dto.GlobalApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
@ApiResponses({
        @ApiResponse(
                responseCode = "400",
                description = "Validation Error",
                content = @Content(
                        mediaType="application/json",
                        schema = @Schema(implementation = GlobalApiResponse.class),
                        examples = @ExampleObject(
                                name = "Validation Error",
                                value = "{\"success\": false, \"message\": \"Validation failed\", \"status\": 400, \"data\": [{\"field\": \"field\", \"message\": \"This field is invalid\"}], \"timestamp\": \"2025-05-22T18:22:44.568Z\"}"
                        )
                )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "Not found",
                content = @Content(
                        mediaType="application/json",
                        schema = @Schema(implementation = GlobalApiResponse.class),
                        examples = @ExampleObject(
                                name = "Not Found",
                                value = "{\"success\": false, \"message\": \"Resource not found\", \"status\": 404, \"data\": null, \"timestamp\": \"2025-05-22T18:22:44.571Z\"}"
                        )
                )
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Server Error",
                content = @Content(
                        mediaType="application/json",
                        schema = @Schema(implementation = GlobalApiResponse.class),
                        examples = @ExampleObject(
                                name = "Server Error",
                                value = "{\"success\": false, \"message\": \"Internal server error\", \"status\": 500, \"data\": null, \"timestamp\": \"2025-05-22T18:22:44.573Z\"}"
                        )
                )
        )
})

public @interface ApiDefaultErrors {
}
