package com.example.repositories

import com.example.routes.AdRequest
import com.example.tables.AdEntity
import com.example.tables.AdTable
import com.example.tables.UserEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.LocalDateTime

interface AdRepository {
    suspend fun insertAd(userId: Int, adRequest: AdRequest): Int
    suspend fun getAllAdsByUserId(userId: Int): List<AdEntity>
    suspend fun getAds(page: Int?): List<AdEntity>
    suspend fun deleteAdById(id: Int)
    suspend fun searchAd(pattern: String): List<AdEntity>
    suspend fun updateAd(id : Int,adRequest: AdRequest)
}

class AdRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : AdRepository {

    override suspend fun insertAd(userId: Int, adRequest: AdRequest) =
        newSuspendedTransaction(dispatcher) {
            AdEntity.new {
                author = UserEntity.findById(userId) ?: throw Exception()
                title = adRequest.title
                description = adRequest.description
                createdAt = LocalDateTime.now()
                price = adRequest.price
                currency = adRequest.currency
            }
        }.id.value

    override suspend fun getAllAdsByUserId(userId: Int): List<AdEntity> =
        newSuspendedTransaction(dispatcher) {
            val user = UserEntity.findById(userId) ?: throw Exception()
            AdEntity.find { AdTable.author eq user.id }.toList()
        }

    override suspend fun getAds(page: Int?) =
        newSuspendedTransaction(dispatcher) {
            if (page != null) {
                AdEntity
                    .all()
                    .chunked(25)
                    .getOrElse(page) { emptyList() }
            } else {
                AdEntity.all().toList()
            }
        }

    override suspend fun deleteAdById(id: Int) =
        newSuspendedTransaction(dispatcher) {
            AdEntity.findById(id)?.let(AdEntity::delete) ?: throw Exception()
        }

    override suspend fun searchAd(pattern: String) =
        newSuspendedTransaction(dispatcher) {
            AdEntity.find { AdTable.title eq "$pattern%" }.toList()
        }

    override suspend fun updateAd(id : Int,adRequest: AdRequest) =
        newSuspendedTransaction {
            val ad = AdEntity.findById(id = id) ?: throw Exception()
            with(ad){
                title = adRequest.title
                description = adRequest.description
                price = adRequest.price
                currency = adRequest.currency
            }
        }

}