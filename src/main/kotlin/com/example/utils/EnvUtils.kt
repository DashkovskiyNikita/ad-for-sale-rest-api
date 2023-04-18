package com.example.utils

object Env {

    val HOST: String = System.getenv("HOST")
    val PORT = System.getenv("PORT").toInt()

    val DB_URL: String = System.getenv("DB_URL")
    val DB_DRIVER: String = System.getenv("DB_DRIVER")
    val DB_USER: String = System.getenv("DB_USER")
    val DB_PASSWORD: String = System.getenv("DB_PASSWORD")

    val JWT_AUDIENCE: String = System.getenv("JWT_AUDIENCE")
    val JWT_ISSUER: String = System.getenv("JWT_ISSUER")
    val JWT_SECRET: String = System.getenv("JWT_SECRET")

    val PWD_SECRET: String = System.getenv("PWD_SECRET")
    val PWD_SALT: String = System.getenv("PWD_SALT")

}