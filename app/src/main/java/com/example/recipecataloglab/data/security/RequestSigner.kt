package com.example.recipecataloglab.data.security

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.util.UUID

object RequestSigner {
    // Секретный ключ (в реальном приложении должен быть в KeyStore или BuildConfig)
    private const val SECRET_KEY = "my_super_secret_key_for_hmac"

    fun generateHeaders(): Map<String, String> {
        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val nonce = UUID.randomUUID().toString()
        val method = "GET"
        val path = "/posts"

        // Формируем каноническую строку: method + path + timestamp + nonce
        val canonicalString = "$method\n$path\n$timestamp\n$nonce"

        // Генерируем HMAC-SHA256
        val signature = hmacSha256(canonicalString, SECRET_KEY)

        return mapOf(
            "X-Timestamp" to timestamp,
            "X-Nonce" to nonce,
            "X-Signature" to signature
        )
    }

    private fun hmacSha256(data: String, key: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        val secretKeySpec = SecretKeySpec(key.toByteArray(), "HmacSHA256")
        mac.init(secretKeySpec)
        val hash = mac.doFinal(data.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }
}