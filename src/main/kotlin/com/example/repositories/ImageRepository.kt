package com.example.repositories

import com.example.tables.AdEntity
import com.example.tables.ImageEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

interface ImageRepository {
    suspend fun insertImage(adId: Int, fileName: String): Int
    suspend fun deleteImageById(id: Int)
}

class ImageRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ImageRepository {

    override suspend fun insertImage(adId: Int, fileName: String) =
        newSuspendedTransaction(dispatcher) {
            ImageEntity.new {
                ad = AdEntity.findById(adId) ?: throw Exception()
                this.fileName = fileName
            }.id.value
        }

    override suspend fun deleteImageById(id: Int) =
        newSuspendedTransaction(dispatcher) {
            ImageEntity.findById(id)?.let(ImageEntity::delete) ?: throw Exception()
        }

}