package io.github.luissimas.commercekata

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.modulith.Modulithic

@Modulithic
@SpringBootApplication
class CommerceKataApplication

fun main(args: Array<String>) {
    runApplication<CommerceKataApplication>(*args)
}
