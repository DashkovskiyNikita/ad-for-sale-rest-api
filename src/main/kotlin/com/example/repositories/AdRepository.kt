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
    suspend fun getAdsByPage(page: Int): List<AdEntity>
    suspend fun deleteAdById(id: Int)
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

    override suspend fun getAdsByPage(page: Int) =
        newSuspendedTransaction(dispatcher) {
            AdEntity
                .all()
                .chunked(25)
                .getOrElse(page) { emptyList() }
        }

    override suspend fun deleteAdById(id: Int) =
        newSuspendedTransaction(dispatcher) {
            AdEntity.findById(id)?.let(AdEntity::delete) ?: throw Exception()
        }

}