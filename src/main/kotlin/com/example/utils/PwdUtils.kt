package com.example.utils

object PwdUtils {

    fun encrypt(pwd: String): String {
        val key = CryptUtils.createKey(secret = Env.PWD_SECRET, salt = "")
        val salt = CryptUtils.generateSalt()
        return CryptUtils.decrypt(data = "$pwd$salt", key = key)

    }

    fun decrypt(pwd: String): String {
        val key = CryptUtils.createKey(secret = Env.PWD_SECRET, salt = "")
        val encryptedPwd = CryptUtils.encrypt(data = pwd, key = key)
        return encryptedPwd.dropLast(24)
    }
}