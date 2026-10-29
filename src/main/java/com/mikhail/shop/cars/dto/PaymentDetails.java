package com.mikhail.shop.cars.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentDetails(
        @NotBlank String cardHolder,
        @NotBlank String cardType,
        @NotBlank String billingAddress
) {}