package com.soma.app.backendrepo.authentication.email_confirmation

import com.fasterxml.jackson.annotation.JsonIgnore
import com.soma.app.backendrepo.model.app_user.UserEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID
import java.util.Date

/**
 * EmailConfirmationTokenEntity to be saved in the database.
 * This entity is used to store password confirmation token information such as token, user, token expiration date, etc.
 */
@Entity
@Table(name = "email_confirmation")
data class EmailConfirmationTokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    @Column(nullable = false)
    val token: String,
//    @JsonIgnore
//    @ManyToOne
//    @JoinColumn(
//        name = "user_id",
//    )
    @Column(nullable = false, unique = true, name = "user_id")
    val userId: UUID? = null,
    @Column(nullable = true)
    var tokenExpiresAt: Date? = null,
    @Column(nullable = true)
    var createdAt: Date? = null,
    @Column(nullable = true)
    var confirmedAt: Date? = null,
)

