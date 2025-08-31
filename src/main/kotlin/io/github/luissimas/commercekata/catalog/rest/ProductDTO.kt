package io.github.luissimas.commercekata.catalog.rest

import io.github.luissimas.commercekata.catalog.domain.Money
import io.github.luissimas.commercekata.catalog.domain.Product
import java.util.UUID

data class ProductDTO(
    val id: UUID,
    val name: String,
    val description: String,
    val price: Money,
) {
    companion object {
        fun fromDomain(product: Product) =
            ProductDTO(
                id = product.id,
                name = product.name,
                description = product.description,
                price = product.price,
            )
    }
}
