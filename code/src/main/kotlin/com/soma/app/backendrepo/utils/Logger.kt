package com.soma.app.backendrepo.utils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Logger<out T> {
    // create static logger
    private val logger: Logger = LoggerFactory.getLogger(Logger::class.java)

    fun getLogger(): Logger {
        return logger
    }
}