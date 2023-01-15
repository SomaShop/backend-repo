package com.soma.app.backendrepo.app_user.user.model

import com.soma.app.backendrepo.app_user.profile.model.Profile
import jakarta.persistence.Id
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Column
import jakarta.persistence.OneToOne
import jakarta.persistence.EnumType
import jakarta.persistence.Table
import jakarta.persistence.Enumerated
import jakarta.persistence.CascadeType
import jakarta.persistence.FetchType

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: UserRole,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val profile: Profile? = null,
)

