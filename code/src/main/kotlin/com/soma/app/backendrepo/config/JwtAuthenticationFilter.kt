package com.soma.app.backendrepo.config

import com.soma.app.backendrepo.app_user.user.model.User
import com.soma.app.backendrepo.security.JwtTokenProvider
import com.soma.app.backendrepo.utils.Logger
import io.jsonwebtoken.io.IOException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {
    private val logger = Logger<JwtAuthenticationFilter>().getLogger()
    lateinit var jwt : String
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val authHeader = request.getHeader("Authorization")
        logger.info("Tag: JwtAuthenticationFilter, token: $authHeader")
        val username: String? = if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7)
            jwtTokenProvider.getEmailFromToken(jwt)
        } else {
            null
        }

        logger.info("Tag: JwtAuthenticationFilter, email: $username")

        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails: UserDetails = this.userDetailsService.loadUserByUsername(username)
            if (jwtTokenProvider.isTokenValid(jwt, userDetails)) {
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                val user = userDetails as User
                logger.info("Tag: JwtAuthenticationFilter, is user Authenticated : ${authentication.isAuthenticated}")
                logger.info("Tag: JwtAuthenticationFilter, user has role: ${user.role.name}")
                logger.info("Tag: JwtAuthenticationFilter, Authenticated user has authorities : ${userDetails.authorities}")
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }
        filterChain.doFilter(request, response)
    }
}
