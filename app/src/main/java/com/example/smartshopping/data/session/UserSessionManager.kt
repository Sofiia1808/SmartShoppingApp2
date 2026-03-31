package com.example.smartshopping.data.session

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserSessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("smart_shopping_session", Context.MODE_PRIVATE)

    private val _currentUserId = MutableStateFlow(prefs.getInt(KEY_USER_ID, -1))
    val currentUserId: StateFlow<Int> = _currentUserId.asStateFlow()

    fun setLoggedInUser(userId: Int) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
        _currentUserId.value = userId
    }

    fun logout() {
        prefs.edit().remove(KEY_USER_ID).apply()
        _currentUserId.value = -1
    }

    companion object {
        private const val KEY_USER_ID = "current_user_id"
    }
}
