package io.github.luissimas.commercekata.catalog

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.luissimas.commercekata.ApiIntegrationTest
import io.github.luissimas.commercekata.catalog.rest.CreateProductRequestDTO
import io.github.luissimas.commercekata.catalog.rest.MoneyDTO
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@ApiIntegrationTest
class ProductControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @Transactional
    fun `should create and retrieve a product`() {
        // Create a product
        val createRequest =
            CreateProductRequestDTO(
                name = "Test Product",
                description = "A test product",
                price = MoneyDTO(BigDecimal("19.99"), "BRL"),
            )
        mockMvc
            .perform(
                post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Test Product"))
            .andExpect(jsonPath("$.description").value("A test product"))
            .andExpect(jsonPath("$.price.amount").value(19.99))
            .andExpect(jsonPath("$.price.currency").value("BRL"))

        // Verify it's retrievable
        mockMvc
            .perform(get("/api/v1/products?page=0&size=10"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.numberOfElements").value(1))
            .andExpect(jsonPath("$.content[0].name").value("Test Product"))
            .andExpect(jsonPath("$.content[0].description").value("A test product"))
    }

    @Test
    fun `should return paginated products`() {
        // Create some products
        repeat(15) { index ->
            val product =
                CreateProductRequestDTO(
                    name = "Product $index",
                    description = "Description $index",
                    price = MoneyDTO(BigDecimal("${10 + index}.99"), "BRL"),
                )
            mockMvc
                .perform(
                    post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)),
                ).andExpect(status().isCreated)
        }

        // Test first page
        mockMvc
            .perform(get("/api/v1/products?page=0&size=10&sort=name,asc"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(10))
            .andExpect(jsonPath("$.totalElements").value(15))
            .andExpect(jsonPath("$.numberOfElements").value(10))
            .andExpect(jsonPath("$.first").value(true))
            .andExpect(jsonPath("$.last").value(false))

        // Test second page
        mockMvc
            .perform(get("/api/v1/products?page=1&size=10&sort=name,asc"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(5))
            .andExpect(jsonPath("$.totalElements").value(15))
            .andExpect(jsonPath("$.numberOfElements").value(5))
            .andExpect(jsonPath("$.first").value(false))
            .andExpect(jsonPath("$.last").value(true))

        // Test the empty third page
        mockMvc
            .perform(get("/api/v1/products?page=2&size=10&sort=name,asc"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(0))
            .andExpect(jsonPath("$.totalElements").value(15))
            .andExpect(jsonPath("$.numberOfElements").value(0))
    }

    @Test
    fun `should return validation error for blank product name`() {
        val invalidRequest =
            CreateProductRequestDTO(
                name = "",
                description = "Valid description",
                price = MoneyDTO(BigDecimal("19.99"), "BRL"),
            )

        mockMvc
            .perform(
                post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.type").value("urn:error:validation-failed"))
            .andExpect(jsonPath("$.title").value("Validation Failed"))
            .andExpect(jsonPath("$.detail").value("One or more fields have an invalid value."))
            .andExpect(jsonPath("$.invalid_params").isArray)
            .andExpect(jsonPath("$.invalid_params[0].field").value("name"))
            .andExpect(jsonPath("$.invalid_params[0].message").exists())
    }

    @Test
    fun `should return validation error for oversized product name`() {
        val longName = "a".repeat(301)
        val invalidRequest =
            CreateProductRequestDTO(
                name = longName,
                description = "Valid description",
                price = MoneyDTO(BigDecimal("19.99"), "BRL"),
            )

        mockMvc
            .perform(
                post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.type").value("urn:error:validation-failed"))
            .andExpect(jsonPath("$.title").value("Validation Failed"))
            .andExpect(jsonPath("$.invalid_params[0].field").value("name"))
    }

    @Test
    fun `should return validation error for negative price`() {
        val invalidRequest =
            CreateProductRequestDTO(
                name = "Valid Name",
                description = "Valid description",
                price = MoneyDTO(BigDecimal("-10.00"), "BRL"),
            )

        mockMvc
            .perform(
                post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.type").value("urn:error:validation-failed"))
            .andExpect(jsonPath("$.title").value("Validation Failed"))
            .andExpect(jsonPath("$.invalid_params[0].field").value("price.amount"))
            .andExpect(jsonPath("$.invalid_params[0].message").value("Price must be greater than 0."))
    }

    @Test
    fun `should return validation error for multiple invalid fields`() {
        val invalidRequest =
            CreateProductRequestDTO(
                name = "",
                description = "",
                price = MoneyDTO(BigDecimal("0.00"), "BRL"),
            )

        mockMvc
            .perform(
                post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.type").value("urn:error:validation-failed"))
            .andExpect(jsonPath("$.invalid_params").isArray)
            .andExpect(jsonPath("$.invalid_params.length()").value(3))
    }

    @Test
    fun `should return missing field error when description is omitted`() {
        val jsonWithoutDescription =
            """
            {
                "name": "Valid Product",
                "price": {
                    "amount": 19.99,
                    "currency": "BRL"
                }
            }
            """.trimIndent()

        mockMvc
            .perform(
                post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonWithoutDescription),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.type").value("urn:error:missing-required-field"))
            .andExpect(jsonPath("$.title").value("Missing required field"))
            .andExpect(jsonPath("$.detail").value("Required field 'description' is missing or null."))
            .andExpect(jsonPath("$.missing_field").value("description"))
    }

    @Test
    fun `should return invalid format error when price amount is not a number`() {
        val jsonWithInvalidPrice =
            """
            {
                "name": "Valid Product",
                "description": "Valid description",
                "price": {
                    "amount": "not-a-number",
                    "currency": "BRL"
                }
            }
            """.trimIndent()

        mockMvc
            .perform(
                post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonWithInvalidPrice),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.type").value("urn:error:invalid-field-format"))
            .andExpect(jsonPath("$.title").value("Invalid field format"))
            .andExpect(jsonPath("$.detail").value("Field 'price.amount' has an invalid format or value."))
            .andExpect(jsonPath("$.provided_value").value("not-a-number"))
    }

    @Test
    fun `should return error for malformed JSON`() {
        val malformedJson =
            """
            {
                "name": "Test Product",
                "description": "Test description"
                "price": {
                    "amount": 19.99
                }
            }
            """.trimIndent()

        mockMvc
            .perform(
                post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(malformedJson),
            ).andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.type").value("urn:error:invalid-request-body"))
            .andExpect(jsonPath("$.title").value("Invalid Request Body"))
            .andExpect(jsonPath("$.detail").value("The request body is malformed or cannot be processed."))
    }
}
