package com.example.utils

object PwdUtils {

    fun encrypt(pwd: String): String {
        val key = CryptUtils.createKey(secret = Env.PWD_SECRET, salt = Env.PWD_SALT)
        val salt = CryptUtils.generateSalt()
        return CryptUtils.encrypt(data = "$pwd$salt", key = key)
    }

    fun decrypt(pwd: String): String {
        val key = CryptUtils.createKey(secret = Env.PWD_SECRET, salt = Env.PWD_SALT)
        val encryptedPwd = CryptUtils.decrypt(data = pwd, key = key)
        return encryptedPwd.dropLast(24)
    }
}