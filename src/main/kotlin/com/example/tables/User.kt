package com.example.tables

import com.example.routes.UserInfoResponse
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object UserTable : IntIdTable() {
    val name = varchar("name", 64)
    val surname = varchar("surname", 64)
    val password = varchar("password", 128)
    val phone = varchar("phone", 16)
    val email = varchar("email", 256).nullable()
}

class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(UserTable)

    var name by UserTable.name
    var surname by UserTable.surname
    var password by UserTable.password
    var phone by UserTable.phone
    var email by UserTable.email
}

fun UserEntity.mapToUserInfo() = UserInfoResponse(
    name = name,
    surname = surname,
    phone = phone,
    email = email
)