package com.example.smartshopping

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.smartshopping.worker.ReminderWorker
import java.util.concurrent.TimeUnit

class SmartShoppingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        scheduleReminderWorker()
    }

    private fun scheduleReminderWorker() {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "smart_shopping_daily_reminder",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
