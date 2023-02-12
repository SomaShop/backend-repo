package com.soma.app.backendrepo.app_user.user.model

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import java.util.UUID

/**
 * User Entity to be saved in the database.
 * This entity is used to store user information such as first name, last name, email, password, role, etc.
 */


@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val userID: UUID? = null,
    @Column(nullable = false)
    val firstName: String,

    @Column(nullable = false)
    val lastName: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    private val password: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val role: UserRole,

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = "user_permissions",
        joinColumns = [JoinColumn(name = "user_id")]
    )
    @Column(name = "permission")
    val permissions: Set<UserPermission>,

    @Column(nullable = false)
    private var enabled: Boolean = false,

    @Column(nullable = false)
    private var isAccountNonExpired: Boolean = true,

    @Column(nullable = false)
    private var isAccountNonLocked: Boolean = true,

    @Column(nullable = false)
    private var isCredentialNonExpired: Boolean = true,
) {
    fun getPassword() = password
    fun isAccountNonExpired() = isAccountNonExpired
    fun isAccountNonLocked() = isCredentialNonExpired
    fun isCredentialsNonExpired() = isCredentialNonExpired
    fun isEnabled() = enabled

}


