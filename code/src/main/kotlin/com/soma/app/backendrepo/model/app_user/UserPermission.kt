package com.soma.app.backendrepo.model.app_user

enum class Authorities(val permissions: Set<UserPermission>) {
    CUSTOMER(
        setOf(
            UserPermission.PROFILE_MANAGEMENT_PERMISSION,
            UserPermission.READ_PERMISSION,
        )
    ),
    MERCHANT(
        setOf(
            UserPermission.MANAGE_PRODUCTS,
            UserPermission.MANAGE_ORDERS,
            UserPermission.PROFILE_MANAGEMENT_PERMISSION,
        )
    ),
    ADMIN(
        setOf(
            UserPermission.MANAGE_PRODUCTS,
            UserPermission.MANAGE_USERS,
        ),
    )
}

enum class UserRole(val permissions: Set<UserPermission>) {
    ROLE_CUSTOMER(Authorities.CUSTOMER.permissions),
    ROLE_MERCHANT(Authorities.MERCHANT.permissions),
    ROLE_ADMIN(Authorities.ADMIN.permissions)
}

enum class UserPermission {
    MANAGE_PRODUCTS,
    MANAGE_USERS,
    PROFILE_MANAGEMENT_PERMISSION,
    MANAGE_ORDERS,
    READ_PERMISSION,
}


sealed class ClaimRoles(val role: String) {
    object Role : ClaimRoles("role")
    object Permission : ClaimRoles("permissions")
}