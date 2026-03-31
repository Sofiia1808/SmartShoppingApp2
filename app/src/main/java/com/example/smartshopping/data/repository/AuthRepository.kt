package com.example.smartshopping.data.repository

import com.example.smartshopping.data.local.dao.UserDao
import com.example.smartshopping.data.local.entity.UserEntity

class AuthRepository(
    private val userDao: UserDao
) {
    suspend fun register(name: String, email: String, password: String): Result<UserEntity> {
        val normalizedEmail = email.trim().lowercase()
        if (userDao.findByEmail(normalizedEmail) != null) {
            return Result.failure(IllegalArgumentException("Користувач з таким email вже існує"))
        }
        userDao.insertUser(
            UserEntity(
                name = name.trim(),
                email = normalizedEmail,
                password = password
            )
        )
        val saved = userDao.findByEmail(normalizedEmail)
            ?: return Result.failure(IllegalStateException("Не вдалося створити акаунт"))
        return Result.success(saved)
    }

    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.login(email.trim().lowercase(), password)
            ?: return Result.failure(IllegalArgumentException("Неправильний email або пароль"))
        return Result.success(user)
    }

    suspend fun getUserById(id: Int): UserEntity? = userDao.findById(id)
}
