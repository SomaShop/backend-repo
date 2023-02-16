package com.soma.app.backendrepo.app_user.user.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * AuthenticatedUser class is created from AuthenticatedUserDetailsService service.
 * It used to hold the user details information.
 * It implements UserDetails interface to provide the user details in security context.
 */

data class AuthenticatedUser(
    val userEntity: UserEntity
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority>? {
        return mutableListOf(SimpleGrantedAuthority(userEntity.role.name))
    }

    override fun getPassword() = userEntity.getPassword()
    override fun getUsername() = userEntity.email
    override fun isAccountNonExpired() = userEntity.isAccountNonExpired()
    override fun isAccountNonLocked() = userEntity.isAccountNonLocked()
    override fun isCredentialsNonExpired() = userEntity.isCredentialsNonExpired()
    override fun isEnabled() = userEntity.isEnabled()
}
