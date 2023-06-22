package com.example.tables

import com.example.routes.AdResponse
import com.example.routes.Author
import com.example.routes.UserAdPhoto
import com.example.routes.UserAdResponse
import com.example.utils.Constants
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

object AdTable : IntIdTable() {
    val author = reference("author", UserTable)
    val title = varchar("title", 64)
    val description = varchar("description", 5000)
    val createdAt = datetime("created_at")
    val price = integer("price")
    val currency = varchar("currency", 16)
}

class AdEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AdEntity>(AdTable)

    var author by UserEntity referencedOn AdTable.author
    var title by AdTable.title
    var description by AdTable.description
    var createdAt by AdTable.createdAt
    var price by AdTable.price
    var currency by AdTable.currency
    val photos by ImageEntity referrersOn ImageTable.ad
}

fun AdEntity.mapToUserAdResponse() =
    UserAdResponse(
        id = id.value,
        title = title,
        description = description,
        createdAt = createdAt.toString(),
        price = price,
        currency = currency,
        photos = transaction {
            photos.map {
                UserAdPhoto(
                    id = it.id.value,
                    imageUrl = "${Constants.BASE_URL}/image/${it.fileName}"
                )
            }
        }
    )

fun AdEntity.mapToAdResponse() =
    AdResponse(
        id = id.value,
        author = transaction {
            Author(
                id = author.id.value,
                name = author.name,
                surname = author.surname,
                phone = author.phone

            )
        } ,
        title = title,
        description = description,
        createdAt = createdAt.toString(),
        price = price,
        currency = currency,
        photos = transaction {
            photos.map {
                UserAdPhoto(
                    id = it.id.value,
                    imageUrl = "${Constants.BASE_URL}/image/${it.fileName}"
                )
            }
        }
    )

