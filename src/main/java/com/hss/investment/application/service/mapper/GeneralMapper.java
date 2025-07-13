package com.hss.investment.application.service.mapper;

import com.hss.investment.application.dto.RateQueryResultDTO;
import com.hss.openapi.model.RateResponseWrapperDataItemsInner;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GeneralMapper {

    List<RateResponseWrapperDataItemsInner> toRateResponseWrapperDataItemsInner(List<RateQueryResultDTO> dto);

    @Mapping(expression = "java(dto.rate().floatValue())", target = "rate")
    @Mapping(expression = "java(dto.initialDate())", target = "initialDate")
    @Mapping(expression = "java(dto.finalDate())", target = "finalDate")
    RateResponseWrapperDataItemsInner toRateResponseWrapperDataItem(RateQueryResultDTO dto);
}