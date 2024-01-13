package com.soma.app.backendrepo.utils.constants

val countryCode = mapOf(
    "Somalia" to "064",
    "Ethiopia" to "231",
    "Kenya" to "404",
    "Egypt" to "818",
)

val countryToCurrencies = mapOf(
    "Somalia" to listOf("Somali shilling", "USD"),
    "Ethiopia" to listOf("Ethiopian birr", "USD"),
    "Kenya" to listOf("Kenyan shilling", "USD"),
    "Egypt" to listOf("Egyptian pound", "USD"),
)

internal fun getCurrenciesForCountry(countryName: String): List<String> = countryToCurrencies[countryName] ?: emptyList()
internal fun getCountryCode(countryName: String): String = countryCode[countryName] ?: ""

