package com.example.smartshopping.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    fun formatDate(millis: Long): String = formatter.format(Date(millis))
}
