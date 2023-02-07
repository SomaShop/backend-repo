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
    val user: User
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority>? {
        return mutableListOf(SimpleGrantedAuthority(user.role.name))
    }

    override fun getPassword() = user.getPassword()
    override fun getUsername() = user.email
    override fun isAccountNonExpired() = user.isAccountNonExpired()
    override fun isAccountNonLocked() = user.isAccountNonLocked()
    override fun isCredentialsNonExpired() = user.isCredentialsNonExpired()
    override fun isEnabled() = user.isEnabled()
}
