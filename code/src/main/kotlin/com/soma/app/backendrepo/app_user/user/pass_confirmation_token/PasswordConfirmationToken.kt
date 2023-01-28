package com.soma.app.backendrepo.app_user.user.pass_confirmation_token

import com.fasterxml.jackson.annotation.JsonIgnore
import com.soma.app.backendrepo.app_user.user.model.User
import jakarta.persistence.Table
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Column
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn
import jakarta.persistence.GenerationType
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "password_confirmation_token")
data class PasswordConfirmationToken(
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    @Column(nullable = false)
    val token: String,
    @JsonIgnore
    @ManyToOne
    @JoinColumn(
        name = "user_id",
    )
     val user: User,
    @Column(nullable = true)
    var tokenExpiresAt: Date? = null,
    @Column(nullable = true)
    var createdAt: Date? = null,
    @Column(nullable = true)
    var confirmedAt: Date? = null,
)

