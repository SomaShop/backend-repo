package com.soma.app.backendrepo.address.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

/**
 * Address Entity to be saved in the database. This entity is a one-to-one relationship with the User Entity.
 * This entity is used to store customer-specific information such as payment method, address, phone, etc.
 * and merchant-specific information such as address, phone, etc.
 *
 */

@Entity
@Table(name = "address")
data class AddressEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val addressID: UUID? = null,
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val isDefault : Boolean = false,
    val countryId: UUID? = null
)
