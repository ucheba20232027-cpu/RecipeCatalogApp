package com.example.recipecataloglab.data

enum class UserRole {
    USER,
    ADMIN
}

data class UserAccount(
    val id: Int,
    val name: String,
    val role: UserRole,
    val avatar: String,
    val login: String
)

object UserDatabase {
    private val defaultUsers = listOf(
        UserAccount(1, "Иван Кулинаров", UserRole.USER, "👨‍🍳", "user"),
        UserAccount(2, "Мария Админова", UserRole.ADMIN, "👩‍💼", "admin")
    )

    // Метод поиска пользователя с учетом сохраненных изменений
    fun findByLogin(login: String, prefs: PreferencesManager): UserAccount? {
        val baseUser = defaultUsers.find { it.login == login } ?: return null

        // Если логин совпадает с сохраненным кастомным логином, подменяем имя и аватар
        if (prefs.savedUserLogin == login) {
            val customName = prefs.customUserName
            val customAvatar = prefs.customUserAvatar
            if (customName != null && customAvatar != null) {
                return baseUser.copy(name = customName, avatar = customAvatar)
            }
        }
        return baseUser
    }
}

object SessionManager {
    var currentUser: UserAccount? = null
}