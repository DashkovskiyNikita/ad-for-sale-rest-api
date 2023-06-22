package com.example.repositories

import com.example.routes.AdForm
import com.example.routes.AdRequest
import com.example.tables.AdEntity
import com.example.tables.AdTable
import com.example.tables.UserEntity
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime

interface AdRepository {
    suspend fun insertAd(userId: Int, adForm: AdForm): Int
    suspend fun getAdByID(id: Int): AdEntity
    suspend fun getAllAdsByUserId(userId: Int): List<AdEntity>
    suspend fun getAds(page: Int?): List<AdEntity>
    suspend fun deleteAdById(id: Int)
    suspend fun searchAd(pattern: String): List<AdEntity>
    suspend fun updateAd(id: Int, adRequest: AdRequest)
}

class AdRepositoryImpl : AdRepository {

    override suspend fun insertAd(userId: Int, adForm: AdForm) =
        newSuspendedTransaction {
            AdEntity.new {
                author = UserEntity.findById(userId) ?: throw Exception()
                title = adForm.title
                description = adForm.description
                createdAt = LocalDateTime.now()
                price = adForm.price
                currency = adForm.currency
            }
        }.id.value

    override suspend fun getAdByID(id: Int) = newSuspendedTransaction {
        AdEntity.findById(id) ?: throw Exception()
    }

    override suspend fun getAllAdsByUserId(userId: Int): List<AdEntity> =
        newSuspendedTransaction {
            val user = UserEntity.findById(userId) ?: throw Exception()
            AdEntity.find { AdTable.author eq user.id }.toList()
        }

    override suspend fun getAds(page: Int?) =
        newSuspendedTransaction {
            if (page != null) {
                AdEntity
                    .all()
                    .chunked(25)
                    .getOrElse(page - 1) { emptyList() }
            } else {
                AdEntity.all().toList()
            }
        }

    override suspend fun deleteAdById(id: Int) =
        newSuspendedTransaction {
            AdEntity.findById(id)?.let(AdEntity::delete) ?: throw Exception()
        }

    override suspend fun searchAd(pattern: String) =
        newSuspendedTransaction {
            AdEntity.find { AdTable.title.lowerCase() like "%${pattern.lowercase()}%" }.toList()
        }

    override suspend fun updateAd(id: Int, adRequest: AdRequest) =
        newSuspendedTransaction {
            val ad = AdEntity.findById(id = id) ?: throw Exception()
            with(ad) {
                title = adRequest.title
                description = adRequest.description
                price = adRequest.price
                currency = adRequest.currency
            }
        }

}