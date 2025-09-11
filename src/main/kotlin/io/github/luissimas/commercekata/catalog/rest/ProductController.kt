package io.github.luissimas.commercekata.catalog.rest

import io.github.luissimas.commercekata.catalog.domain.Product
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.core.JdbcAggregateTemplate
import org.springframework.data.jdbc.core.findAll
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product Catalog", description = "Operations for product catalog")
class ProductController(
    val repository: JdbcAggregateTemplate,
) {
    private val logger = KotlinLogging.logger {}

    @Operation(
        summary = "List products",
        description = "Retrieve a paginated list of all products in the catalog",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Retrieved products"),
        ],
    )
    @GetMapping
    fun listProducts(pageable: Pageable): Page<ProductDTO> =
        repository.findAll<Product>(pageable).map(ProductDTO::fromDomain)

    @Operation(
        summary = "Create product",
        description = "Add a new product to the catalog",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Product created successfully",
                content = [Content(schema = Schema(implementation = ProductDTO::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error or invalid request body",
                content = [Content(schema = Schema(implementation = ProblemDetail::class))],
            ),
        ],
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createProduct(
        @RequestBody @Valid request: CreateProductRequestDTO,
    ): ProductDTO =
        repository.insert(request.toDomain()).let(ProductDTO::fromDomain).also {
            logger.atInfo {
                message = "Product created"
                payload =
                    buildMap {
                        put("productId", it.id)
                        put("productName", it.name)
                    }
            }
        }
}
