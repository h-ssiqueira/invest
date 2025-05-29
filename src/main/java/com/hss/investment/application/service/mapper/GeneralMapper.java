package com.hss.investment.application.service.mapper;

import com.hss.investment.application.dto.RateQueryResultDTO;
import com.hss.openapi.model.RateResponseWrapperDataItemsInner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GeneralMapper {

    @Mapping(target = "rate", expression = "java(item.getRate().floatValue())")
    List<RateResponseWrapperDataItemsInner> toRateResponseWrapperDataItemsInner(List<RateQueryResultDTO> dto);
}