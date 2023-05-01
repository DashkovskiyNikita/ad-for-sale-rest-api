package com.example.tables

import com.example.routes.Favorite
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object FavoriteTable : IntIdTable() {
    val user = reference("user", UserTable)
    val ad = reference("ad", AdTable)
}

class FavoriteEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<FavoriteEntity>(FavoriteTable)

    var user by UserEntity referencedOn FavoriteTable.user
    var ad by AdEntity referencedOn FavoriteTable.ad
}

fun FavoriteEntity.mapToResponse() =
    Favorite(
        id = id.value,
        ad = ad.mapToResponse()
    )