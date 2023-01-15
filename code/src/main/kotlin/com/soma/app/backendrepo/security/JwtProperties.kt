package com.soma.app.backendrepo.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.jwt")
class JwtProperties {
    lateinit var secret: String
    var expirationTime: Long = 0
    var refreshExpirationTime: Long = 0
}
