package com.xuno.payment.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
public class SanitizedValidator implements ConstraintValidator<Sanitized, String> {
    
    private static final Pattern DANGEROUS_CHARS = Pattern.compile("[<>\"'&]");
    private static final Pattern HTML_TAGS = Pattern.compile("<[^>]*>");
    
    private boolean allowHtml;
    private int maxLength;

    @Override
    public void initialize(Sanitized constraintAnnotation) {
        this.allowHtml = constraintAnnotation.allowHtml();
        this.maxLength = constraintAnnotation.maxLength();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (value.length() > maxLength) {
            log.warn("Input validation failed: String length {} exceeds maximum {}", value.length(), maxLength);
            return false;
        }

        if (DANGEROUS_CHARS.matcher(value).find()) {
            log.warn("Input validation failed: String contains dangerous characters: {}", value);
            return false;
        }

        if (!allowHtml && HTML_TAGS.matcher(value).find()) {
            log.warn("Input validation failed: String contains HTML tags: {}", value);
            return false;
        }

        return true;
    }
}