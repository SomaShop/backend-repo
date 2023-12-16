package com.soma.app.backendrepo.authentication.email_confirmation

import com.soma.app.backendrepo.utils.Logger
import org.springframework.stereotype.Service
import java.util.Date
import java.util.Optional
import java.util.UUID

@Service
class EmailConfirmationService(
    private val emailConfirmationRepository: EmailConfirmationRepository
) {
    val logger = Logger.getLogger<EmailConfirmationService>()

    fun saveToken(token: EmailConfirmationTokenEntity) {
        emailConfirmationRepository.save(token)
    }

    fun getToken(token: String): Optional<EmailConfirmationTokenEntity> {
        return emailConfirmationRepository.findByToken(token)
    }

    fun findByUserId(userId: UUID?): Optional<EmailConfirmationTokenEntity> {
        return emailConfirmationRepository.findByUserId(userId)
    }

    fun setConfirmedAt(token: String) {
        logger.info("TAG: PasswordConfirmationService: setConfirmedAt: token: $token")
        val passTokenExists = emailConfirmationRepository.findByToken(token)

        when {
            passTokenExists.isPresent -> {
                val passToken = passTokenExists.get()
                emailConfirmationRepository.save(passToken.copy(confirmedAt = Date()))
            }
            else -> {
                logger.info("TAG: PasswordConfirmationService: setConfirmedAt: token not found")
            }
        }
    }
}