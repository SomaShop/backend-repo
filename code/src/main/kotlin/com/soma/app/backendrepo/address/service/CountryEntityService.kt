package com.soma.app.backendrepo.address.service

import com.soma.app.backendrepo.address.entity.CountryEntity
import com.soma.app.backendrepo.address.pojo.CountryData
import com.soma.app.backendrepo.address.repository.CountryEntityRepository
import org.springframework.stereotype.Service

@Service
class CountryEntityService(
    private val countryEntityRepository: CountryEntityRepository
) {
    fun createCountry(countryData: CountryData): CountryEntity {
        val countryEntity = CountryEntity(
            countryName = countryData.countryName,
        )
        val country = countryEntity.copy(
            countryNumericCode = countryEntity.getCountryCode(),
            currency = countryEntity.getCurrenciesForCountry()
        )
        return saveCountryEntity(country)
    }

    fun getCountryByCountryName(countryName: String): CountryEntity? {
        return countryEntityRepository.findByCountryName(countryName)
    }

    fun getCountryByCountryCode(countryCode: String): CountryEntity? {
        return countryEntityRepository.findByCountryNumericCode(countryCode)
    }

    fun saveCountryEntity(countryEntity: CountryEntity) = countryEntityRepository.save(countryEntity)
}