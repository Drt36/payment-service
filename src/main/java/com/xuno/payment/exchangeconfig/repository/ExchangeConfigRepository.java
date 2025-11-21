package com.xuno.payment.exchangeconfig.repository;

import com.xuno.payment.exchangeconfig.model.entity.ExchangeRateConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeConfigRepository extends MongoRepository<ExchangeRateConfiguration, String> {

    @Query("{ 'deleted': false }")
    java.util.List<ExchangeRateConfiguration> findAll();

    @Query("{ '_id': ?0, 'deleted': false }")
    Optional<ExchangeRateConfiguration> findById(String id);

    @Query(value = "{ 'sourceCurrency': ?0, 'targetCurrency': ?1, 'minAmount': { $lte: ?2 }, 'maxAmount': { $gte: ?2 }, 'deleted': false }", 
           sort = "{ 'createdAt': -1 }")
    List<ExchangeRateConfiguration> findMatchingConfig(
            String sourceCurrency,
            String targetCurrency,
            BigDecimal amount
    );
}
