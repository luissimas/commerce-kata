package io.github.luissimas.commercekata

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<CommerceKataApplication>().with(TestcontainersConfiguration::class).run(*args)
}
