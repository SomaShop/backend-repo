package com.soma.app.backendrepo.app_user.user.model

enum class AllowedPermissions(val permissions: Set<UserPermission>) {
    BUYER(emptySet()),
    MERCHANT(
        setOf(
            UserPermission.READ_PRODUCT,
            UserPermission.CREATE_PRODUCT,
            UserPermission.UPDATE_PRODUCT,
            UserPermission.DELETE_PRODUCT
        )
    ),
    ADMIN(
        setOf(
            UserPermission.READ_PRODUCT,
            UserPermission.CREATE_PRODUCT,
            UserPermission.UPDATE_PRODUCT,
            UserPermission.DELETE_PRODUCT,
            UserPermission.READ_USER,
            UserPermission.CREATE_USER,
            UserPermission.UPDATE_USER,
            UserPermission.DELETE_USER
        ),
    )
}

enum class UserRole(val permissions: Set<UserPermission>) {
    BUYER(AllowedPermissions.BUYER.permissions),
    MERCHANT(AllowedPermissions.MERCHANT.permissions),
    ADMIN(AllowedPermissions.ADMIN.permissions)
}

enum class UserPermission {
    READ_PRODUCT,
    CREATE_PRODUCT,
    UPDATE_PRODUCT,
    DELETE_PRODUCT,
    READ_USER,
    CREATE_USER,
    UPDATE_USER,
    DELETE_USER,
}


sealed class ClaimRoles(val role: String) {
    object Role : ClaimRoles("role")
    object Permission : ClaimRoles("permissions")
}