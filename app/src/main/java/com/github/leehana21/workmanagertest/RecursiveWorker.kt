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
import androidx.core.app.NotificationCompat
import androidx.work.*
import androidx.work.ExistingWorkPolicy.REPLACE
import com.github.leehana21.workmanagertest.App.Companion.getStringDate
import java.util.concurrent.TimeUnit


@Suppress("UNREACHABLE_CODE")
class RecursiveWorker(context: Context, params : WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        if(isStopped){
            App.prefs.testStop = getStringDate()
            App.prefs.testHistory = App.prefs.testHistory + " - stopped[${getStringDate()}]"
            return Result.failure()
        }
        App.prefs.testNumber = App.prefs.testNumber + 1
        sendNotification("테스트 - ${App.prefs.testNumber}", getStringDate())
        val resetDayByDayWorkerRequest = OneTimeWorkRequestBuilder<RecursiveWorker>()
            .setInitialDelay(3600000, TimeUnit.MILLISECONDS)
            .addTag("reset_day_by_day")
            .build()
        //WorkManager.getInstance(applicationContext).enqueue(resetDayByDayWorkerRequest)
        WorkManager.getInstance(applicationContext).beginUniqueWork("recursiveWork", REPLACE,resetDayByDayWorkerRequest).enqueue()
        App.prefs.testHistory = App.prefs.testHistory + " - [${getStringDate()}]"
        return Result.success()
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
            val builder: Notification.Builder = Notification.Builder(applicationContext, App.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
            builder.build().flags = Notification.FLAG_AUTO_CANCEL
            manager.notify(App.prefs.testNumber /* ID of notification */, builder.build())
        } else {
            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext)
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
