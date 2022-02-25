package com.github.leehana21.workmanagertest

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import com.github.leehana21.workmanagertest.App.Companion.getStringDate
import com.github.leehana21.workmanagertest.App.Companion.setNotificationChannel
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var resetDayByDayWorkerRequest: OneTimeWorkRequest
    private lateinit var powerManager: PowerManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        powerManager = getSystemService(POWER_SERVICE) as PowerManager
        setNotificationChannel(this)
        //initApiWorker()
    }

    @SuppressLint("BatteryLife")
    override fun onResume() {
        super.onResume()
        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = Uri.parse(String.format("package:%s", packageName))
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        initRecursiveWorker()
        workState()
    }

    private fun initRecursiveWorker() {
        if (App.prefs.testNumber == 0) {
            App.prefs.testNumber = -1
            App.prefs.testHistory = "시작 시간 : ${getStringDate()}"
            Timber.d("preference update first init ${App.prefs.testNumber}")
        }
        // worker request
        resetDayByDayWorkerRequest = OneTimeWorkRequestBuilder<RecursiveWorker>()
            .setInitialDelay(3600000, TimeUnit.MILLISECONDS)
            .addTag("reset_day_by_day")
            .build()

        WorkManager.getInstance(this).beginUniqueWork(
            "recursiveWork",
            ExistingWorkPolicy.REPLACE, resetDayByDayWorkerRequest
        ).enqueue()
        Timber.d("init success recursive worker")
    }

    private fun initApiWorker() {
        val apiWorkerRequest = OneTimeWorkRequestBuilder<ApiWorker>()
            .setInitialDelay(60000, TimeUnit.MILLISECONDS)
            .addTag("api_worker")
            .build()
        WorkManager.getInstance(applicationContext).enqueue(apiWorkerRequest)
        Timber.d("init success api worker")
    }

    private fun workState() {
        WorkManager.getInstance(applicationContext)
            .getWorkInfoByIdLiveData(resetDayByDayWorkerRequest.id)
            .observe(this, Observer { happen ->
                happen?.let {
                    when (it.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            Timber.d("${it.state} ${it.tags}")
                        }
                        WorkInfo.State.RUNNING -> {
                            Timber.d("${it.state} ${it.tags}")

                        }
                        WorkInfo.State.BLOCKED -> {
                            Timber.d("${it.state} ${it.tags}")

                        }
                        WorkInfo.State.CANCELLED -> {
                            Timber.d("${it.state} ${it.tags}")

                        }
                        WorkInfo.State.ENQUEUED -> {
                            Timber.d("${it.state} ${it.tags}")
                        }
                        WorkInfo.State.FAILED -> {
                            Timber.d("${it.state} ${it.tags}")
                        }
                        else -> {
                            Timber.d("else ?? else ?? why ?? ")
                        }
                    }
                }
            })
    }

}