package com.soma.app.backendrepo

import com.soma.app.backendrepo.security.JwtProperties
import com.soma.app.backendrepo.utils.Logger
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration

@SpringBootApplication
@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class BackendRepoApplication

fun main(args: Array<String>) {
    runApplication<BackendRepoApplication>(*args)
}
