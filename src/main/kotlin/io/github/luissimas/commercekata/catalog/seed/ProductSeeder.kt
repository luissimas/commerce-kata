package io.github.luissimas.commercekata.catalog.seed

import io.github.luissimas.commercekata.catalog.domain.Currency
import io.github.luissimas.commercekata.catalog.domain.Money
import io.github.luissimas.commercekata.catalog.domain.Product
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.jdbc.core.count
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.UUID

@Component
@Profile("dev")
class ProductSeeder(
    val jdbcAggregateTemplate: JdbcAggregateTemplate,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val count = jdbcAggregateTemplate.count<Product>()
        if (count > 0) return

        val products =
            listOf(
                Product(
                    id = UUID.randomUUID(),
                    name = "The Ultimate Widget",
                    description = "A high-quality widget that will solve all your problems.",
                    price = Money(BigDecimal("29.99"), Currency.BRL),
                ),
                Product(
                    id = UUID.randomUUID(),
                    name = "The Generic Gizmo",
                    description = "A standard gizmo for all your gizmo needs.",
                    price = Money(BigDecimal("14.50"), Currency.BRL),
                ),
                Product(
                    id = UUID.randomUUID(),
                    name = "The Advanced Gadget",
                    description = "A top-of-the-line gadget with all the bells and whistles.",
                    price = Money(BigDecimal("99.99"), Currency.BRL),
                ),
            )

        jdbcAggregateTemplate.insertAll(products)
    }
}
