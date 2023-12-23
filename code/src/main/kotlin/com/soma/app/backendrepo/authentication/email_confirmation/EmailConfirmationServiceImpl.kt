package com.soma.app.backendrepo.authentication.email_confirmation

import com.soma.app.backendrepo.utils.Logger
import org.springframework.stereotype.Service
import java.util.Date
import java.util.Optional
import java.util.UUID


interface EmailConfirmationService {
    fun getToken(token: String): Optional<EmailConfirmationTokenEntity>
    fun findByUserId(userId: UUID?): Optional<EmailConfirmationTokenEntity>
    fun findByVerificationCode(verificationCode: String): Optional<EmailConfirmationTokenEntity>
}


@Service
class EmailConfirmationServiceImpl(
    private val emailConfirmationRepository: EmailConfirmationRepository
): EmailConfirmationService {
    val logger = Logger.getLogger<EmailConfirmationServiceImpl>()

    fun saveToken(token: EmailConfirmationTokenEntity) {
        emailConfirmationRepository.save(token)
    }

    fun saveEmailConfirmationToken(emailConfirmationTokenEntity: EmailConfirmationTokenEntity) {
        emailConfirmationRepository.save(emailConfirmationTokenEntity)
    }

    override
    fun getToken(token: String): Optional<EmailConfirmationTokenEntity> {
        return emailConfirmationRepository.findByToken(token)
    }
override
    fun findByUserId(userId: UUID?): Optional<EmailConfirmationTokenEntity> {
        return emailConfirmationRepository.findByUserId(userId)
    }

    override fun findByVerificationCode(verificationCode: String): Optional<EmailConfirmationTokenEntity> {
        return emailConfirmationRepository.findByVerificationCode(verificationCode)
    }
}