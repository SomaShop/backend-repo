package com.soma.app.backendrepo.profile.merchant

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.soma.app.backendrepo.address.entity.AddressEntity
import com.soma.app.backendrepo.model.app_user.UserEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
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
data class MerchantProfileEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val merchantId: UUID? = null,
    val businessName: String? = null,
    val businessAddress: String? = null,
    val businessPhoneNumber: String? = null,
    val businessDescription: String? = null,
    val businessType: String? = null,
    val businessCategory: String? = null,
    val businessSubCategory: String? = null,
    val businessWebsite: String? = null,
    val businessEmail: String? = null,
    val businessLogo: String? = null,
    val businessCoverPhoto: String? = null,
) {

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_address_id")
    private var addresses: MutableList<AddressEntity> = mutableListOf()

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private lateinit var user: UserEntity

    fun assignUser(userEntity: UserEntity) {
        this.user = userEntity
    }

    fun getUser() = user

    fun addAddress(address: AddressEntity) {
        addresses.add(address)
    }

    fun removeMerchantAddress(addressId: UUID) {
        addresses.removeIf { it.addressID == addressId }
    }

    fun updateMerchantAddresses(address: AddressEntity) {
        val index = addresses.indexOfFirst { it.addressID == address.addressID }
        addresses[index] = address
    }

    fun getAddresses() = addresses
}
