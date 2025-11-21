package com.xuno.payment.payment.repository;

import com.xuno.payment.payment.model.enums.PaymentStatus;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;

public class PaymentSpecification {

    public static Query buildQuery(
            PaymentStatus status,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            String senderReference) {
        
        Criteria criteria = Criteria.where("deleted").is(false);
        
        if (status != null) {
            criteria = criteria.and("status").is(status);
        }
        
        if (dateFrom != null && dateTo != null) {
            criteria = criteria.and("createdAt").gte(dateFrom).lte(dateTo);
        } else if (dateFrom != null) {
            criteria = criteria.and("createdAt").gte(dateFrom);
        } else if (dateTo != null) {
            criteria = criteria.and("createdAt").lte(dateTo);
        }
        
        if (senderReference != null) {
            criteria = criteria.and("sender.referenceNumber").is(senderReference);
        }
        
        return new Query(criteria);
    }
}

