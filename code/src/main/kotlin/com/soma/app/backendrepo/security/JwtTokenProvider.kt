package com.soma.app.backendrepo.security

import com.soma.app.backendrepo.app_user.user.model.AuthenticatedUser
import com.soma.app.backendrepo.app_user.user.model.ClaimRoles
import com.soma.app.backendrepo.app_user.user.model.UserEntity
import com.soma.app.backendrepo.config.JwtProperties
import com.soma.app.backendrepo.utils.Logger
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.security.Key
import java.security.SignatureException
import java.util.Date

/**
 * This class is used to create and validate the JWT token for the user.
 */

@Service
class JwtTokenProvider(
    private val jwtProperties: JwtProperties
) {
    private val log = Logger.getLogger<JwtTokenProvider>()
    fun createToken(userEntity: UserEntity): String {
        val claims = Jwts.claims().setSubject(userEntity.email)
        claims[ClaimRoles.Role.role] = userEntity.role
        claims[ClaimRoles.Permission.role] = userEntity.permissions

        val now = Date()
        val expiryDate = Date(now.time + jwtProperties.expirationTime)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .signWith(getSignWithKey(), SignatureAlgorithm.HS256)
            .setExpiration(expiryDate)
            .compact()
    }

    fun validRequest(
        token: String,
        authenticatedUser: AuthenticatedUser,
    ): Boolean {
        val userEmail = getEmailFromToken(token)
        if (userEmail == authenticatedUser.username
            && validateToken(token)
            && !isTokenExpired(token)
            && authenticatedUser.isEnabled
        ) {
            return true
        }
        throw Exception("Could not authenticate request")
    }

    fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    fun getExpirationDateFromToken(token: String): Date {
        return getClaimFromToken(token, Claims::getExpiration)
    }

    private fun validateToken(token: String): Boolean {
        try {
            Jwts
                .parserBuilder()
                .setSigningKey(getSignWithKey())
                .build()
                .parseClaimsJws(token)
            return true
        } catch (ex: SignatureException) {
            log.error("Tag: JwtTokenProvider, message: ${ex.message}")
            throw Exception("Invalid JWT signature. message: ${ex.message}")
        } catch (ex: MalformedJwtException) {
            log.error("Tag: JwtTokenProvider, message: ${ex.message}")
            throw Exception("Invalid JWT token: message: ${ex.message}")
        } catch (ex: ExpiredJwtException) {
            log.error("Tag: JwtTokenProvider, message: ${ex.message}")
            throw Exception("Token is expired message: ${ex.message}")
        } catch (ex: UnsupportedJwtException) {
            log.error("Tag: JwtTokenProvider, message: ${ex.message}")
            throw Exception("Unsupported JWT token. message: ${ex.message}")
        } catch (ex: IllegalArgumentException) {
            log.error("Tag: JwtTokenProvider, message: ${ex.message}")
            throw Exception("JWT claims string is empty. message: ${ex.message}")
        }
    }

    private fun getSignWithKey(): Key {
        val keyBytes = Decoders.BASE64.decode(jwtProperties.secret)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun getEmailFromToken(token: String): String {
        return getClaimFromToken(token, Claims::getSubject)
    }

    fun <T> getClaimFromToken(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }


    fun extractAllClaims(token: String): Claims {
        return Jwts
            .parserBuilder()
            .setSigningKey(getSignWithKey())
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun createConfirmPasswordToken(userEntity: UserEntity): String {
        val tokenExpirationTime = 15 * 60 * 1000
        val claims = Jwts
            .claims()
            .setSubject(userEntity.email)

        val now = Date()
        val expiryDate = Date(now.time + tokenExpirationTime)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .signWith(getSignWithKey(), SignatureAlgorithm.HS256)
            .setExpiration(expiryDate)
            .compact()
    }

}
