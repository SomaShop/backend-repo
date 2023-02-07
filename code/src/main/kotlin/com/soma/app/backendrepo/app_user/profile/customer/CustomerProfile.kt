package com.soma.app.backendrepo.app_user.profile.customer

import com.soma.app.backendrepo.app_user.user.model.User
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.UUID

/**
 * CustomerProfile Entity to be saved in the database.
 * This entity is a one-to-one relationship with the User Entity.
 * This entity is used to store customer-specific information such as payment method, address, phone, etc.
 */

@Entity
@Table(name = "customers_profile")
data class CustomerProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val customerId: UUID? = null,
    val paymentMethod: String? = null,
    // additional fields such as address, phone, etc.
) {
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private lateinit var user: User

    fun assignUser(user: User) {
        this.user = user
    }

    fun getUser() = user
}
