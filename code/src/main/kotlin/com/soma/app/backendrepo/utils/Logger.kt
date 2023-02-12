package com.soma.app.backendrepo.utils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Logger {
    companion object {
        inline fun <reified T> getLogger(): Logger {
            return LoggerFactory.getLogger(T::class.java)
        }
    }
}