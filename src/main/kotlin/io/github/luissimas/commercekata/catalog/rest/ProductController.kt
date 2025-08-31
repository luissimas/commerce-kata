package io.github.luissimas.commercekata.catalog.rest

import io.github.luissimas.commercekata.catalog.domain.Product
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.jdbc.core.findAll
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductController(
    val repository: JdbcAggregateTemplate,
) {
    @GetMapping
    fun listProducts(pageable: Pageable): Page<ProductDTO> =
        repository.findAll<Product>(pageable).map(ProductDTO::fromDomain)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createProduct(
        @RequestBody @Valid request: CreateProductRequestDTO,
    ): ProductDTO = repository.insert(request.toDomain()).let(ProductDTO::fromDomain)
}
