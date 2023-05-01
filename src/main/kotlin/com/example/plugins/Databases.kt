package com.example.plugins

import com.example.tables.AdTable
import com.example.tables.FavoriteTable
import com.example.tables.ImageTable
import com.example.tables.UserTable
import com.example.utils.Env
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun configureDatabase() {

    val database = Database.connect(
        url = Env.DB_URL,
        user = Env.DB_USER,
        driver = Env.DB_DRIVER,
        password = Env.DB_PASSWORD
    )

    transaction(database) {
        SchemaUtils.createMissingTablesAndColumns(
            UserTable, AdTable, FavoriteTable, ImageTable
        )
    }

}

