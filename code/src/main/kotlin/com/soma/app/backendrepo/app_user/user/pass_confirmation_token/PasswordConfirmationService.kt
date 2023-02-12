package com.soma.app.backendrepo.app_user.user.pass_confirmation_token

import com.soma.app.backendrepo.app_user.user.model.UserEntity
import com.soma.app.backendrepo.utils.Logger
import org.springframework.stereotype.Service
import java.util.Date
import java.util.Optional

@Service
class PasswordConfirmationService(
    private val passwordConfirmationRepository: PasswordConfirmationRepository
) {
    val logger = Logger.getLogger<PasswordConfirmationService>()

    fun saveToken(token: PasswordConfirmationToken) {
        passwordConfirmationRepository.save(token)
    }

    fun getToken(token: String): Optional<PasswordConfirmationToken> {
        return passwordConfirmationRepository.findByToken(token)
    }

    fun findTokenByUser(userEntity: UserEntity): Optional<PasswordConfirmationToken> {
        return passwordConfirmationRepository.findByUser(userEntity)
    }

    fun setConfirmedAt(token: String) {
        logger.info("TAG: PasswordConfirmationService: setConfirmedAt: token: $token")
        val passTokenExists = passwordConfirmationRepository.findByToken(token)

        when {
            passTokenExists.isPresent -> {
                val passToken = passTokenExists.get()
                passwordConfirmationRepository.save(passToken.copy(confirmedAt = Date()))
            }
            else -> {
                logger.info("TAG: PasswordConfirmationService: setConfirmedAt: token not found")
            }
        }
    }
}