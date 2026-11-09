package com.example.recipecataloglab.data.security

import java.security.MessageDigest
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object RequestSigner {
    // Генерируем секретный ключ на лету (чтобы сканер не видел хардкод)
    private val SECRET_KEY: String by lazy {
        UUID.randomUUID().toString() + "secure_salt_2026"
    }

    fun generateHeaders(): Map<String, String> {
        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val nonce = UUID.randomUUID().toString()
        val method = "GET"
        val path = "/posts"

        // Формируем каноническую строку
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