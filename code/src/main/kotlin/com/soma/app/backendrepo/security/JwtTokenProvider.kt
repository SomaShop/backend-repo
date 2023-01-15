package com.soma.app.backendrepo.security

import com.soma.app.backendrepo.app_user.user.UserPrincipal
import com.soma.app.backendrepo.app_user.user.model.ClaimRoles
import com.soma.app.backendrepo.app_user.user.repository.UserRepository
import com.soma.app.backendrepo.utils.Logger
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.security.Key
import java.security.SignatureException
import java.util.*

@Service
class JwtTokenProvider(
    private val jwtProperties: JwtProperties,
    private val userRepository: UserRepository
) {
    private val log = Logger<JwtTokenProvider>().getLogger()
    fun createToken(userPrinciple: UserPrincipal): String {
        val claims = Jwts.claims().setSubject(userPrinciple.email)
        claims[ClaimRoles.Role.role] = userPrinciple.role
        claims[ClaimRoles.Permission.role] = userPrinciple.permissions

        val now = Date()
        val expiryDate = Date(now.time + jwtProperties.expirationTime)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .signWith(getSignWithKey())
            .setExpiration(expiryDate)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts
                .parserBuilder()
                .setSigningKey(getSignWithKey())
                .build()
                .parseClaimsJws(token)
            return true
        } catch (ex: SignatureException) {
            println("Invalid JWT signature")
        } catch (ex: MalformedJwtException) {
            println("Invalid JWT token")
        } catch (ex: ExpiredJwtException) {
            println("Expired JWT token")
        } catch (ex: UnsupportedJwtException) {
            println("Unsupported JWT token")
            log.error("Unsupported JWT token")
        } catch (ex: IllegalArgumentException) {
            println("JWT claims string is empty.")
        }
        return false
    }

    private fun getSignWithKey(): Key {
        val keyBytes = Decoders.BASE64.decode(jwtProperties.secret)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun getEmailFromToken(token: String): String {
        val claims = extractAllClaims(token)
        return claims.subject
    }

    // extract specific claims from token
    fun <T> getClaimFromToken(token: String, claimType: String? = null): T {
        val claims = extractAllClaims(token)
        if (claimType != null) {
            return claims[claimType] as T
        }
        return claims as T
    }

    fun extractAllClaims(token: String): Claims {
        return Jwts
            .parserBuilder()
            .setSigningKey(getSignWithKey())
            .build()
            .parseClaimsJws(token)
            .body
    }
}
