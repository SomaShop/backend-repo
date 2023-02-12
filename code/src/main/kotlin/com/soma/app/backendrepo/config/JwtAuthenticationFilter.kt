package com.soma.app.backendrepo.config

import com.soma.app.backendrepo.security.JwtTokenProvider
import com.soma.app.backendrepo.security.auth.service.AuthenticatedUserDetailsService
import com.soma.app.backendrepo.utils.Logger
import io.jsonwebtoken.io.IOException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * JwtAuthenticationFilter class is used to filter the request and validate the token.
 * It extends OncePerRequestFilter class to ensure that the filter is only executed once per request.
 * It implements doFilterInternal method to filter the request.
 * It uses JwtTokenProvider to validate the token.
 * It uses AuthenticatedUserDetailsService to load the user details.
 */

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticatedUserDetailsService: AuthenticatedUserDetailsService
) : OncePerRequestFilter() {
    private val log = Logger.getLogger<JwtAuthenticationFilter>()
    lateinit var jwt: String

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        try {
            val authHeader = request.getHeader("Authorization")
            log.info("Tag: JwtAuthenticationFilter, token: $authHeader")
            val username: String? = if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7)
                jwtTokenProvider.getEmailFromToken(jwt)
            } else {
                null
            }

            log.info("Tag: JwtAuthenticationFilter, email: $username")

            if (username != null && SecurityContextHolder.getContext().authentication == null) {
                val userDetails = authenticatedUserDetailsService.loadUserByUsername(username)
                if (jwtTokenProvider.validRequest(jwt, userDetails)) {
                    val authentication = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities
                    )
                    log.info("Tag: JwtAuthenticationFilter, is user Authenticated : ${authentication.isAuthenticated}")
                    log.info("Tag: JwtAuthenticationFilter, user has role: ${userDetails.userEntity.role.name}")
                    log.info("Tag: JwtAuthenticationFilter, Authenticated user has authorities : ${userDetails.authorities}")
                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        } catch (ex: Exception) {
            SecurityContextHolder.clearContext()
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            val error = Error("error occurred while trying to authenticate user. message: ${ex.message}")
            response.writer.write(error.message)
            return
        }
        filterChain.doFilter(request, response)
    }
}
