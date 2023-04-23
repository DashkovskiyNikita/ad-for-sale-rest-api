package com.example.tables

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object ImageTable : IntIdTable() {
    val ad = reference("ad", AdTable)
    val fileName = varchar("fileName", 128)
}

class ImageEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ImageEntity>(ImageTable)

    var ad by AdEntity referencedOn ImageTable.ad
    var fileName by ImageTable.fileName
}