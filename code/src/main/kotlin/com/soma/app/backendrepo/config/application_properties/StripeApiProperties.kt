package com.soma.app.backendrepo.config.application_properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "app.stripe")
class StripeApiProperties {
    var apiSecretKey: String = ""
}

@Configuration
@EnableConfigurationProperties(StripeApiProperties::class)
class StripeApiPropertiesConfig