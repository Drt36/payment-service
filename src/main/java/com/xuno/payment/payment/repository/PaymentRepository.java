package com.xuno.payment.payment.repository;

import com.xuno.payment.payment.model.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    @Query("{ 'deleted': false }")
    Page<Payment> findAll(Pageable pageable);

    @Query("{ '_id': ?0, 'deleted': false }")
    Optional<Payment> findById(String id);

    @Query("{ 'idempotencyKey': ?0, 'deleted': false }")
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);
}
