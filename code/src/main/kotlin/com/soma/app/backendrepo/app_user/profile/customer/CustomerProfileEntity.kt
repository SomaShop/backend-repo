package com.soma.app.backendrepo.app_user.profile.customer

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.soma.app.backendrepo.app_user.address.entity.AddressEntity
import com.soma.app.backendrepo.app_user.user.model.UserEntity
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
 * CustomerProfile Entity to be saved in the database.
 * This entity is a one-to-one relationship with the User Entity.
 * This entity is used to store customer-specific information such as payment method, address, phone, etc.
 */

@Entity
@Table(name = "customers_profile")
data class CustomerProfileEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val customerId: UUID? = null,
    val paymentMethod: String? = null,
    val customerPhoneNumber: String? = null,
    // additional fields such as address, phone, etc.
) {

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_address_id")
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
