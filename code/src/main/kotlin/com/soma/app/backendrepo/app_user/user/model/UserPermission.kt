package com.soma.app.backendrepo.app_user.user.model

enum class AllowedPermissions(val permissions: Set<UserPermission>) {
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
    ROLE_CUSTOMER(AllowedPermissions.CUSTOMER.permissions),
    ROLE_MERCHANT(AllowedPermissions.MERCHANT.permissions),
    ROLE_ADMIN(AllowedPermissions.ADMIN.permissions)
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