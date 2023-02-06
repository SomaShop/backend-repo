package com.soma.app.backendrepo.app_user.profile.merchant

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
 * MerchantProfile Entity to be saved in the database.
 * This entity is a one-to-one relationship with the User Entity.
 * This entity is used to store merchant-specific information such as business name, address, phone, etc.
 */

@Entity
@Table(name = "merchants_profile")
data class MerchantProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val merchantId: UUID? = null,
    val businessName: String? = null,
    val businessAddress: String? = null,
    val businessPhone: String? = null,
    val businessDescription: String? = null,
    val businessType: String? = null,
    val businessCategory: String? = null,
    val businessSubCategory: String? = null,
    val businessWebsite: String? = null,
    val businessEmail: String? = null,
    val businessLogo: String? = null,
    val businessCoverPhoto: String? = null,
) {
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private lateinit var user: User

    fun assignUser(user: User) {
        this.user = user
    }

    fun getUser() = user
}
