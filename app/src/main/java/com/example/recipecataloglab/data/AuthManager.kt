package com.example.recipecataloglab.data

object AuthManager {
    var sessionToken: String? = null
        private set

    fun startSession(token: String) {
        sessionToken = token
    }

    fun clearSession() {
        sessionToken = null
    }
}