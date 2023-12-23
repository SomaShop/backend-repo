package com.soma.app.backendrepo.email_service

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

    fun sendEmail(email: String, subject: String, text: String) {
        val message = SimpleMailMessage()
        message.setTo(email)
        message.subject = subject
        message.text = text

        javaMailSender.send(message)
        logger.info("TAG: EmailService: sendEmail: message: mail sent successfully")
    }
}