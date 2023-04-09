package com.example.tables

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object Ad : IntIdTable() {
    val author = reference("author", UserTable)
    val title = varchar("title", 64)
    val description = varchar("description", 5000)
    val createdAt = datetime("created_at")
    val price = integer("price")
    val currency = varchar("currency", 16)
}

class AdEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AdEntity>(Ad)

    var author by UserEntity referencedOn Ad.author
    var title by Ad.title
    var description by Ad.description
    var createdAt by Ad.createdAt
    var price by Ad.price
    var currency by Ad.currency
}
