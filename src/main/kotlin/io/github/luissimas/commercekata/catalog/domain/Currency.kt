package io.github.luissimas.commercekata.catalog.domain

enum class Currency {
    BRL, ;

    companion object {
        fun fromString(currency: String) =
            when (currency) {
                "BRL" -> BRL
                else -> throw IllegalArgumentException("Invalid currency code: $currency")
            }
    }
}
