package ba.pascal.weissleder.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity


@SpringBootApplication
@EnableWebSecurity
class ServerApplication

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}

