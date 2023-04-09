package com.example.plugins

import com.example.utils.Env
import org.jetbrains.exposed.sql.Database

fun configureDatabase() {

    val database = Database.connect(
        url = Env.DB_URL,
        user = Env.DB_USER,
        driver = Env.DB_DRIVER,
        password = Env.DB_PASSWORD
    )

}

