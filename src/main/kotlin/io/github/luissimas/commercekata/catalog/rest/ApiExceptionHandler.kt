package io.github.luissimas.commercekata.catalog.rest

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.URI

@RestControllerAdvice
class ApiExceptionHandler : ResponseEntityExceptionHandler() {
    override fun handleMethodArgumentNotValid(
        exception: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any> {
        val invalidParams =
            exception.bindingResult.fieldErrors.map { fieldError ->
                mapOf(
                    "field" to fieldError.field,
                    "message" to (fieldError.defaultMessage ?: "Invalid value"),
                )
            }
        return ProblemDetail
            .forStatus(HttpStatus.BAD_REQUEST)
            .apply {
                type = URI("urn:error:validation-failed")
                title = "Validation Failed"
                detail = "One or more fields have an invalid value."
                setProperty("invalid_params", invalidParams)
            }.let { ResponseEntity.status(it.status).body(it) }
    }

    override fun handleHttpMessageNotReadable(
        exception: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any> =
        when (val cause = exception.cause) {
            is InvalidFormatException -> {
                val fieldPath = cause.path.joinToString(".") { it.fieldName ?: "[${it.index}]" }
                ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
                    type = URI("urn:error:invalid-field-format")
                    title = "Invalid field format"
                    detail = "Field '$fieldPath' has an invalid format or value."
                    setProperty("provided_value", cause.value?.toString())
                }
            }

            is MismatchedInputException -> {
                val fieldPath = cause.path.joinToString(".") { it.fieldName ?: "[${it.index}]" }
                ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
                    type = URI("urn:error:missing-required-field")
                    title = "Missing required field"
                    detail = "Required field '$fieldPath' is missing or null."
                    setProperty("missing_field", fieldPath)
                }
            }

            is JsonMappingException -> {
                ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
                    type = URI("urn:error:request-mapping-error")
                    title = "Request Mapping Error"
                    detail = "Unable to process the request body structure."
                }
            }

            else -> {
                ProblemDetail.forStatus(HttpStatus.BAD_REQUEST).apply {
                    type = URI("urn:error:invalid-request-body")
                    title = "Invalid Request Body"
                    detail = "The request body is malformed or cannot be processed."
                }
            }
        }.let { ResponseEntity.status(it.status).body(it) }
}
