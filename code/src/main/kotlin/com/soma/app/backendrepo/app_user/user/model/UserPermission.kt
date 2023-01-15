package com.soma.app.backendrepo.app_user.user.model

enum class UserPermission {
    VIEW_PROFILE,
    UPDATE_PROFILE,
    CREATE_PRODUCT,
    UPDATE_PRODUCT,
    DELETE_PRODUCT,
    USER_READ,
    USER_WRITE,
    PRODUCT_READ,
    PRODUCT_WRITE,
    ORDER_READ,
    ORDER_WRITE,
    CATEGORY_READ,
    CATEGORY_WRITE,
    ADMIN
}

sealed class ClaimRoles(val role: String) {
    object Role : ClaimRoles("role")
    object Admin : ClaimRoles("admin")
    object Permission : ClaimRoles("permissions")
}