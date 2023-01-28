package com.soma.app.backendrepo.app_user.user.model

import com.soma.app.backendrepo.app_user.profile.model.Profile
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.CollectionTable
import jakarta.persistence.CascadeType
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import jakarta.persistence.GenerationType
import java.util.UUID

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private val id: UUID? = null,
    @Column(nullable = false)
    val firstName: String,

    @Column(nullable = false)
    val lastName: String,

    @Column(nullable = false, unique = true)
    private val email: String,

    @Column(nullable = false)
    private val password: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val role: UserRole,

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_permissions", joinColumns = [JoinColumn(name = "user_id")])
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

    ) : UserDetails {

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var profile: Profile? = null

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return when (role) {
            UserRole.ADMIN -> permissions.map { permission -> SimpleGrantedAuthority(permission.name) }.toMutableList()
            UserRole.BUYER -> permissions.map { permission -> SimpleGrantedAuthority(permission.name) }.toMutableList()
            UserRole.MERCHANT -> permissions.map { permission -> SimpleGrantedAuthority(permission.name) }
                .toMutableList()
        }
    }

    override fun getPassword(): String {
        return this.password
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return isAccountNonExpired
    }

    override fun isAccountNonLocked(): Boolean {
        return isCredentialNonExpired
    }

    override fun isCredentialsNonExpired(): Boolean {
        return isAccountNonLocked
    }

    override fun isEnabled(): Boolean {
        return enabled
    }

    fun assignProfile(profile: Profile) {
        this.profile = profile
    }

    fun getId(): UUID? {
        return id
    }

}

