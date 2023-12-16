package com.soma.app.backendrepo.email_service

import com.soma.app.backendrepo.model.app_user.UserEntity
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
    fun sendPasswordResetEmail(email: String, token: String) {
        val message = SimpleMailMessage()
        message.setTo(email)
        message.subject = "Password reset request"
        //TODO: update the password reset url in production
        message.text = "To reset your password, please click the link below:\n" +
                "http://localhost:8080/password/reset_password?token=$token"

        javaMailSender.send(message)

        logger.info("TAG: EmailService: sendPasswordResetEmail: message: mail sent successfully")
    }

    fun sendEmailConfirmationEmail(userEntity: UserEntity, token: String) {
        val message = SimpleMailMessage()
        message.setTo(userEntity.email)
        message.subject = "Email confirmation request"

        //TODO: update the password confirmation url in production
        message.text = "To confirm your email, please click the link below:\n" +
                "http://localhost:8080/confirm-password?token=$token"

        javaMailSender.send(message)
        logger.info("TAG: EmailService: sendEmailConfirmationEmail: message: mail sent successfully")
    }
}