package com.example.smartshopping.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smartshopping.util.NotificationHelper

class ReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        NotificationHelper.showReminder(
            context = applicationContext,
            title = "Smart Shopping",
            message = "Перевірте рекомендації та заплануйте наступні покупки."
        )
        return Result.success()
    }
}
