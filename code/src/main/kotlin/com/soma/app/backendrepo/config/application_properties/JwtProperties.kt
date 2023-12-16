package com.soma.app.backendrepo.config.application_properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "app.jwt")
class JwtProperties {
    var secret: String = ""
    var expirationTime: Long = 0
    var refreshExpirationTime: Long = 0
}

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class JwtPropertiesConfig
