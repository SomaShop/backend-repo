package com.soma.app.backendrepo.config.email

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "app.email")
class EmailProperty {
    var email: String = ""
    var password: String = ""
    var host: String = ""
    var port: Int = 0
}

@Configuration
@EnableConfigurationProperties(EmailProperty::class)
class EmailPropertyConfig