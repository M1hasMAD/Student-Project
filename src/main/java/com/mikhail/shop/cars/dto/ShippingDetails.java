package com.mikhail.shop.cars.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ShippingDetails(
        @NotBlank String fullName,
        @NotBlank String address,
        @NotBlank String province,
        @NotBlank String phone,
        @NotBlank String city,
        @NotBlank String postalCode
) {}