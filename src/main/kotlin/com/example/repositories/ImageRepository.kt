package com.example.repositories

import com.example.tables.AdEntity
import com.example.tables.ImageEntity
import com.example.tables.ImageTable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

interface ImageRepository {
    suspend fun insertImage(adId: Int, fileName: String): Int
    suspend fun getImageById(id: Int): ImageEntity
    suspend fun deleteImageById(id: Int)
    suspend fun getAllImagesByAd(adId: Int): List<ImageEntity>
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

    override suspend fun getImageById(id: Int) =
        newSuspendedTransaction(dispatcher) {
            ImageEntity.findById(id) ?: throw Exception()
        }

    override suspend fun deleteImageById(id: Int) =
        newSuspendedTransaction(dispatcher) {
            ImageEntity.findById(id)?.let(ImageEntity::delete) ?: throw Exception()
        }

    override suspend fun getAllImagesByAd(adId: Int) =
        newSuspendedTransaction {
            val ad = AdEntity.findById(adId) ?: throw Exception()
            ImageEntity.find { ImageTable.ad eq ad.id }.toList()
        }

}