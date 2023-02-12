package com.soma.app.backendrepo.security.auth.reser_password.service

import com.soma.app.backendrepo.app_user.user.model.UserEntity
import com.soma.app.backendrepo.utils.Logger
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

/**
 * This class is used to send emails to the user
 */

@Service
class EmailService(
    private val javaMailSender: JavaMailSender
) {
    val logger = Logger.getLogger<EmailService>()
    fun sendPasswordResetEmail(userEntity: UserEntity, token: String) {
        val message = SimpleMailMessage()
        message.setTo(userEntity.email)
        message.subject = "Password reset request"
        //TODO: update the password reset url in production
        message.text = "To reset your password, please click the link below:\n" +
                "http://localhost:8080/reset-password?token=$token"

        javaMailSender.send(message)

        logger.info("TAG: EmailService: sendPasswordResetEmail: message: mail sent successfully")
    }

    fun sendPasswordConfirmationEmail(userEntity: UserEntity, token: String) {
        val message = SimpleMailMessage()
        message.setTo(userEntity.email)
        message.subject = "Password confirmation request"

        //TODO: update the password confirmation url in production
        message.text = "To confirm your password, please click the link below:\n" +
                "http://localhost:8080/confirm-password?token=$token"

        javaMailSender.send(message)
        logger.info("TAG: EmailService: sendPasswordConfirmationEmail: message: mail sent successfully")
    }
}