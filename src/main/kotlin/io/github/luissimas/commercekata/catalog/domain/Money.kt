package io.github.luissimas.commercekata.catalog.domain

import java.math.BigDecimal

data class Money(
    val amount: BigDecimal,
    val currency: Currency,
)
