package io.github.luissimas.commercekata.catalog

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.luissimas.commercekata.ApiIntegrationTest
import io.github.luissimas.commercekata.catalog.rest.CreateProductRequestDTO
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
                price = Money(BigDecimal("19.99"), Currency.BRL),
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
                    price = Money(BigDecimal("${10 + index}.99"), Currency.BRL),
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
}
