package com.soma.app.backendrepo

import com.soma.app.backendrepo.security.JwtProperties
import com.soma.app.backendrepo.utils.Logger
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties::class)
class BackendRepoApplication

fun main(args: Array<String>) {
    val logger = Logger<BackendRepoApplication>().getLogger()
    logger.info("Starting application...")
    runApplication<BackendRepoApplication>(*args)
}
