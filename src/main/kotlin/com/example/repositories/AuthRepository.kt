package com.example.repositories

import com.example.routes.RegisterRequest
import com.example.routes.UserInfoResponse
import com.example.tables.UserEntity
import com.example.tables.UserTable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

interface AuthRepository {
    suspend fun getUserByLogin(login: String): UserEntity?
    suspend fun getUserByPhoneOrEmail(phone: String, email: String): UserEntity?
    suspend fun insertUser(request: RegisterRequest): Int
    suspend fun getUserById(id: Int): UserInfoResponse
    suspend fun updateUser(id: Int, request: RegisterRequest)
    suspend fun deleteAccount(id: Int)
}

class AuthRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthRepository {
    override suspend fun getUserByLogin(login: String) =
        newSuspendedTransaction(dispatcher) {
            val user = UserEntity.find {
                (UserTable.email eq login) or (UserTable.phone eq login)
            }
            user.firstOrNull()
        }

    override suspend fun getUserByPhoneOrEmail(phone: String, email: String) =
        newSuspendedTransaction(dispatcher) {
            val user = UserEntity.find {
                (UserTable.email eq email) or (UserTable.phone eq phone)
            }
            user.firstOrNull()
        }

    override suspend fun insertUser(request: RegisterRequest) =
        newSuspendedTransaction(dispatcher) {

            val newUser = UserEntity.new {
                name = request.name
                surname = request.surname
                phone = request.phone
                email = request.email
                password = request.password
            }

            newUser.id.value

        }

    override suspend fun getUserById(id: Int) =
        newSuspendedTransaction(dispatcher) {
            val user = UserEntity.findById(id)
                ?: throw Exception("User with id : $id not found")

            UserInfoResponse(
                name = user.name,
                surname = user.surname,
                phone = user.phone,
                email = user.email
            )
        }


    override suspend fun updateUser(id: Int, request: RegisterRequest) =
        newSuspendedTransaction(dispatcher) {
            val user = UserEntity.findById(id)
                ?: throw Exception("User with id : $id not found")

            with(user) {
                name = request.name
                surname = request.surname
                email = request.email
                phone = request.phone
                password = request.password
            }

        }

    override suspend fun deleteAccount(id: Int) =
        newSuspendedTransaction(dispatcher) {
            UserEntity.findById(id)?.let(UserEntity::delete)
                ?: throw Exception("User with id : $id not found")
        }
}