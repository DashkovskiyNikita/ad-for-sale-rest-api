package com.example.utils

import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object CryptUtils {

    fun createKey(secret: String, salt: String): SecretKey {
        val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keySpec = PBEKeySpec(secret.toCharArray(), salt.toByteArray(), 65536, 256)
        return SecretKeySpec(keyFactory.generateSecret(keySpec).encoded, "AEC")
    }

    fun decrypt(data: String, key: SecretKey): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decodedData: ByteArray = Base64.getDecoder().decode(data)
        return String(cipher.doFinal(decodedData))
    }

    fun encrypt(data: String, key: SecretKey): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encryptedData = cipher.doFinal(data.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedData)
    }

}