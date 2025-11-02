package com.hss.investment.application.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ErrorMessages {

    public static final String INV_001 = "Idempotency value must be informed";
    public static final String INV_002 = "FinalDate must not be before the initialDate";
    public static final String INV_003 = "Rate must not be lower than zero";
    public static final String INV_004 = "No items were processable";
    public static final String INV_005 = "Unexpected error: %s";
    public static final String INV_006 = "%s not found";
    public static final String INV_007 = "Investment already completed";

}