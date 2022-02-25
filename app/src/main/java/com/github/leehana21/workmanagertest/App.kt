package com.github.leehana21.workmanagertest

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import timber.log.Timber
import java.util.*

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        prefs = SharedPreference(applicationContext)
        Timber.plant(Timber.DebugTree())
    }

    companion object {
        const val CHANNEL_ID = "CHANNEL_ID"
        const val TAG = "debug"
        private const val CHANNEL_NAME = "CHANNEL_NAME"
        private const val NOTIFICATION_CHANNEL_DESCRIPTION = "NOTIFICATION_CHANNEL_DESCRIPTION"
        lateinit var prefs: SharedPreference

        fun setNotificationChannel(context: Context) {
            val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            //오레오 이상에서만 노티채널 생성
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelMessage = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                channelMessage.description = NOTIFICATION_CHANNEL_DESCRIPTION
                channelMessage.enableLights(true)
                channelMessage.lightColor = Color.RED
                channelMessage.enableVibration(true)
                channelMessage.vibrationPattern = longArrayOf(100, 200, 100, 200)
                channelMessage.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                channelMessage.setShowBadge(true)
                manager.createNotificationChannel(channelMessage)
            }
        }

        fun getStringDate(): String {
            //현재 날짜를 세팅한다.
            val lmODate = Date()
            val lmOGCal = GregorianCalendar()
            lmOGCal.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            lmOGCal.time = lmODate
            val year = lmOGCal[Calendar.YEAR].toString() //년
            var month = (lmOGCal[Calendar.MONTH] + 1).toString() //월
            var day = (lmOGCal[Calendar.DATE]).toString() //일
            var hour = (lmOGCal[Calendar.HOUR_OF_DAY]).toString() //시
            var minute = (lmOGCal[Calendar.MINUTE]).toString() //분
            var second = (lmOGCal[Calendar.SECOND]).toString() //초

            //월일시분초가 1자리로 나올 경우 2자리로 맞춘다.
            if (month.length == 1) {
                month = "0$month"
            }
            if (day.length == 1) {
                day = "0$day"
            }
            if (hour.length == 1) {
                hour = "0$hour"
            }
            if (minute.length == 1) {
                minute = "0$minute"
            }
            if (second.length == 1) {
                second = "0$second"
            }
            return "$year.$month.$day $hour:$minute:$second"
        }
    }
}