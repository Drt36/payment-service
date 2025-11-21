package com.xuno.payment.exchangeconfig.mapper;

import com.xuno.payment.exchangeconfig.model.dto.ExchangeConfigRequest;
import com.xuno.payment.exchangeconfig.model.dto.ExchangeConfigResponse;
import com.xuno.payment.exchangeconfig.model.entity.ExchangeRateConfiguration;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExchangeConfigMapper {

    ExchangeRateConfiguration toEntity(ExchangeConfigRequest request);

    ExchangeConfigResponse toResponse(ExchangeRateConfiguration entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRequestToEntity(@MappingTarget ExchangeRateConfiguration entity, ExchangeConfigRequest request);
}
