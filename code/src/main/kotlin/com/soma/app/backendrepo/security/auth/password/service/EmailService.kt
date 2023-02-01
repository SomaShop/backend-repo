package com.soma.app.backendrepo.security.auth.password.service

import com.soma.app.backendrepo.app_user.user.model.User
import com.soma.app.backendrepo.utils.Logger
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val javaMailSender: JavaMailSender
) {
    val logger = Logger<EmailService>().getLogger()
    fun sendPasswordResetEmail(user: User, token: String) {
        val message = SimpleMailMessage()
        message.setTo(user.username)
        message.subject = "Password reset request"
        //TODO: update the password reset url in production
        message.text = "To reset your password, please click the link below:\n" +
                "http://localhost:8080/reset-password/$token"

        javaMailSender.send(message)

        logger.info("TAG: EmailService: sendPasswordResetEmail: message: mail sent successfully")
    }

    fun sendPasswordConfirmationEmail(user: User, token: String) {
        val message = SimpleMailMessage()
        message.setTo(user.username)
        message.subject = "Password confirmation request"

        //TODO: update the password confirmation url in production
        message.text = "To confirm your password, please click the link below:\n" +
                "http://localhost:8080/confirm-password/$token"

        javaMailSender.send(message)
        logger.info("TAG: EmailService: sendPasswordConfirmationEmail: message: mail sent successfully")
    }
}