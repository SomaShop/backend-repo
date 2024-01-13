package com.soma.app.backendrepo.error_handling

enum class ErrorCode {
    USER_NOT_FOUND,
    USER_ALREADY_EXISTS,
    USER_NOT_VERIFIED,
    BAD_CREDENTIALS,
    BAD_REQUEST,
    INTERNAL_SERVER_ERROR,
    INVALID_TOKEN,
    TOKEN_EXPIRED,
    JWT_TOKEN_ERROR,
    OLD_PASSWORD_DETECTED,
    TOKEN_NOT_FOUND,
    FORBIDDEN,
    UNAUTHORIZED,
    EMAIL_NOT_FOUND,
    ADDRESS_ALREADY_EXISTS,
    ADDRESS_NOT_FOUND,
    VERIFICATION_CODE_NOT_FOUND,
    VERIFICATION_CODE_EXPIRED,
    ACCOUNT_LOCKED,
    ACCOUNT_EXPIRED,
    CREDENTIALS_EXPIRED,
    UNKNOWN_CREDENTIALS_ERROR,
    USER_ALREADY_VERIFIED,
    MERCHANT_NOT_FOUND,
    CUSTOMER_NOT_FOUND,
}