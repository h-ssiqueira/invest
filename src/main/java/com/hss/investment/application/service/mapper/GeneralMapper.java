package com.hss.investment.application.service.mapper;

import com.hss.investment.application.dto.RateQueryResultDTO;
import com.hss.openapi.model.RateResponseWrapperDataItemsInner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GeneralMapper {

    List<RateResponseWrapperDataItemsInner> toRateResponseWrapperDataItemsInner(List<RateQueryResultDTO> dto);

    @Mapping(expression = "java(dto.rate().floatValue())", target = "rate")
    @Mapping(expression = "java(dto.initialDate())", target = "initialDate")
    @Mapping(expression = "java(dto.finalDate())", target = "finalDate")
    RateResponseWrapperDataItemsInner toRateResponseWrapperDataItem(RateQueryResultDTO dto);

    SelicTimeline toSelicTimeline(RateQueryResultDTO dto);

    IPCATimeline toIpcaTimeline(RateQueryResultDTO dto);
}