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

@Entity
@Table(name = "profiles")
data class Profile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val name: String,

    val address: String,

    val phone: String,

    val description: String
)