package com.soma.app.backendrepo.address.pojo

data class AddressData(
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: CountryData
)

data class CountryData(
    val countryName: String,
)
