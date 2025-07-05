package ru.amplicode.orders.rest.dto;

import jakarta.validation.constraints.PositiveOrZero;

public record ProductAndAmount(Long productId, @PositiveOrZero Long amount) {
}
