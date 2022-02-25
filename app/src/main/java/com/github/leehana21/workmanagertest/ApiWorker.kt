package com.github.leehana21.workmanagertest

import android.annotation.SuppressLint
import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.github.leehana21.workmanagertest.App.Companion.TAG
import com.github.leehana21.workmanagertest.App.Companion.getStringDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit


@Suppress("UNREACHABLE_CODE")
class ApiWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = coroutineScope {
        val response = withContext(Dispatchers.IO) {
            MainApiClient.getInstance().rqList()
        }

        if(App.prefs.testCount <= 9) {
            App.prefs.testCount = App.prefs.testCount + 1
            val altValue = response[(0 until response.size).random()]
            sendNotification("메세지 타이틀 - $altValue", getStringDate())
            val apiWorkerRequest = OneTimeWorkRequestBuilder<ApiWorker>()
                .setInitialDelay(60000, TimeUnit.MILLISECONDS)
                .addTag("api_worker")
                .build()
            WorkManager.getInstance(applicationContext).enqueue(apiWorkerRequest)
            Timber.d("api work enqueue - ${App.prefs.testCount}")
        } else {
            App.prefs.testCount = 0
            WorkManager.getInstance(applicationContext).cancelAllWorkByTag("api_worker")
        }

        Result.success()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun sendNotification(messageTitle: String?, messageBody: String?) {
        val manager =
            applicationContext.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent: PendingIntent
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                applicationContext, 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            // PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.getActivity(
                applicationContext, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        //PUSH 오레오 이상 버전 대응 코드 추가
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val builder: Notification.Builder =
                Notification.Builder(applicationContext, App.CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
            builder.build().flags = Notification.FLAG_AUTO_CANCEL
            manager.notify(App.prefs.testNumber /* ID of notification */, builder.build())
        } else {
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(applicationContext)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
            notificationBuilder.build().flags = Notification.FLAG_AUTO_CANCEL
            manager.notify(2 /* ID of notification */, notificationBuilder.build())
        }
    }
}
