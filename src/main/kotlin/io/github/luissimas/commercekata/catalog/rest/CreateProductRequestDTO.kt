package io.github.luissimas.commercekata.catalog.rest

import io.github.luissimas.commercekata.catalog.Money
import io.github.luissimas.commercekata.catalog.Product
import java.util.UUID

data class CreateProductRequestDTO(
    val name: String,
    val description: String,
    val price: Money,
) {
    fun toDomain() =
        Product(
            id = UUID.randomUUID(),
            name = name,
            description = description,
            price = price,
        )
}
