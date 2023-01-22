package com.soma.app.backendrepo.security

import com.soma.app.backendrepo.app_user.user.model.ClaimRoles
import com.soma.app.backendrepo.app_user.user.model.User
import com.soma.app.backendrepo.utils.Logger
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.security.Key
import java.security.SignatureException
import java.util.Date

@Service
class JwtTokenProvider(
    private val jwtProperties: JwtProperties,
) {
    private val log = Logger<JwtTokenProvider>().getLogger()
    fun createToken(user: User): String {
        val claims = Jwts.claims().setSubject(user.username)
        claims[ClaimRoles.Role.role] = user.role
        claims[ClaimRoles.Permission.role] = user.permissions

        val now = Date()
        val expiryDate = Date(now.time + jwtProperties.expirationTime)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .signWith(getSignWithKey(), SignatureAlgorithm.HS256)
            .setExpiration(expiryDate)
            .compact()
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val userEmail = getEmailFromToken(token)
        if(userEmail == userDetails.username && validateToken(token) && !isTokenExpired(token)) {
            return true
        }
        return false
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
}
