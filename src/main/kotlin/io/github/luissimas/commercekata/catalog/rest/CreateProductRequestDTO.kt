package io.github.luissimas.commercekata.catalog.rest

import io.github.luissimas.commercekata.catalog.domain.Currency
import io.github.luissimas.commercekata.catalog.domain.Money
import io.github.luissimas.commercekata.catalog.domain.Product
import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.util.UUID

data class CreateProductRequestDTO(
    @field:NotBlank
    @field:Size(max = 300)
    val name: String,
    @field:NotBlank
    @field:Size(max = 1000)
    val description: String,
    @field:Valid
    val price: MoneyDTO,
) {
    fun toDomain() =
        Product(
            id = UUID.randomUUID(),
            name = name,
            description = description,
            price = price.toDomain(),
        )
}

data class MoneyDTO(
    @field:DecimalMin("0.01", message = "Price must be greater than 0.")
    val amount: BigDecimal,
    val currency: String,
) {
    fun toDomain() =
        Money(
            amount = amount,
            currency = Currency.fromString(currency),
        )
}
