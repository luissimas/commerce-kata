package io.github.luissimas.commercekata.catalog.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Embedded
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("product")
data class Product(
    @Id
    val id: UUID,
    val name: String,
    val description: String,
    @Embedded(prefix = "price_", onEmpty = Embedded.OnEmpty.USE_NULL)
    val price: Money,
)
