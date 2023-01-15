package com.soma.app.backendrepo.config

import com.soma.app.backendrepo.security.JwtProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class JwtPropertiesConfig



