package com.hss.investment.application.dto.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Sort;

@UtilityClass
public class SortExtractor {

    public static Sort extractSort(String sort) {
        var split = sort.split(",");
        return Sort.by("asc".equals(split[1]) ? Sort.Direction.ASC : Sort.Direction.DESC, split[0]);
    }
}