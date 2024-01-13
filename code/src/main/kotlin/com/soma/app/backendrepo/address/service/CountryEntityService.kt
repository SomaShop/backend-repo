package com.soma.app.backendrepo.address.service

import com.soma.app.backendrepo.address.entity.CountryEntity
import com.soma.app.backendrepo.address.pojo.CountryData
import com.soma.app.backendrepo.address.repository.CountryEntityRepository
import com.soma.app.backendrepo.utils.constants.getCountryCode
import com.soma.app.backendrepo.utils.constants.getCurrenciesForCountry
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CountryEntityService(
    private val countryEntityRepository: CountryEntityRepository
) {
    fun createCountry(countryData: CountryData): CountryEntity {
        val countryEntity = CountryEntity(
            countryName = countryData.countryName,
            countryNumericCode = getCountryCode(countryData.countryName),
            currency = getCurrenciesForCountry(countryData.countryName)
        )
        return saveCountryEntity(countryEntity)
    }

    fun getCountryByCountryName(countryName: String): CountryEntity? {
        return countryEntityRepository.findByCountryName(countryName)
    }

    fun getCountryByCountryCode(countryCode: String): CountryEntity? {
        return countryEntityRepository.findByCountryNumericCode(countryCode)
    }

    fun findCountryById(countryId: UUID?): CountryEntity? {
        return countryEntityRepository.findByCountryId(countryId)
    }

    fun saveCountryEntity(countryEntity: CountryEntity) = countryEntityRepository.save(countryEntity)
}