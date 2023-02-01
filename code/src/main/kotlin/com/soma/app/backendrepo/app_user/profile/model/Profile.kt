package com.soma.app.backendrepo.app_user.profile.model

import com.soma.app.backendrepo.app_user.user.model.User
import jakarta.persistence.Id
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Column
import jakarta.persistence.OneToOne
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "profiles")
data class Profile(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false)
    val name: String,

    val address: String,

    val phone: String,

    val description: String
) {
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null

    fun assignUser(user: User) {
        this.user = user
    }
}

data class ProfileResponse(
    val id: Long,
    val name: String,
    val address: String,
    val phone: String,
    val description: String
)
