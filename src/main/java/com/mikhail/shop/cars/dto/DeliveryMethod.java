package com.mikhail.shop.cars.dto;

import jakarta.validation.constraints.NotBlank;

public record DeliveryMethod(
        @NotBlank String method
) {}