package com.example.repositories

import com.example.tables.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

interface FavoriteRepository {
    suspend fun insertFavorite(userId: Int, adId: Int): FavoriteEntity
    suspend fun getFavoriteById(id: Int): FavoriteEntity
    suspend fun getAllFavoritesByUser(userId: Int): List<FavoriteEntity>
    suspend fun deleteFavorite(adId: Int)
}

class FavoriteRepositoryImpl : FavoriteRepository {

    override suspend fun insertFavorite(userId: Int, adId: Int) =
        newSuspendedTransaction {
            FavoriteEntity.new {
                user = UserEntity.findById(userId) ?: throw Exception()
                ad = AdEntity.findById(adId) ?: throw Exception()
            }
        }

    override suspend fun getFavoriteById(id: Int) =
        newSuspendedTransaction {
            FavoriteEntity.findById(id = id) ?: throw Exception()
        }

    override suspend fun getAllFavoritesByUser(userId: Int) =
        newSuspendedTransaction {
            val user = UserEntity.findById(userId) ?: throw Exception()
            FavoriteEntity.find { FavoriteTable.user eq user.id }.toList()
        }

    override suspend fun deleteFavorite(adId: Int) =
        newSuspendedTransaction {
            FavoriteEntity.findById(adId)?.let(FavoriteEntity::delete) ?: throw Exception()
        }

}