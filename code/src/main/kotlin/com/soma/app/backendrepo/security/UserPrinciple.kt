package com.soma.app.backendrepo.app_user.user

import com.soma.app.backendrepo.app_user.user.model.UserPermission
import com.soma.app.backendrepo.app_user.user.model.UserRole
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserPrincipal(
    val email: String,
    val role: UserRole,
    val permissions: List<UserPermission>,
    val authorities: Collection<GrantedAuthority>
)
//    : UserDetails {
//    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
//        TODO("Not yet implemented")
//    }
//
//    override fun getPassword(): String {
//    }
//
//    override fun getUsername(): String {
//        TODO("Not yet implemented")
//    }
//
//    override fun isAccountNonExpired(): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun isAccountNonLocked(): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun isCredentialsNonExpired(): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun isEnabled(): Boolean {
//        TODO("Not yet implemented")
//    }
//}
