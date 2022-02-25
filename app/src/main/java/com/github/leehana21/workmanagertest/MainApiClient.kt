package com.github.leehana21.workmanagertest

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.orhanobut.logger.Logger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

object MainApiClient {
    private lateinit var mainApiInterface: MainApiInterface
    fun getInstance(): MainApiInterface {
        val builder =
            Retrofit.Builder().baseUrl("https://hacker-news.firebaseio.com/").addConverterFactory(
                GsonConverterFactory.create(GsonBuilder().setLenient().create())
            )

        val httpClient = OkHttpClient.Builder().addInterceptor(httpLoggingInterceptorWithTimber())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        val retrofit = builder.client(httpClient.build()).build()

        mainApiInterface = retrofit.create(MainApiInterface::class.java)

        return mainApiInterface
    }

    private fun httpLoggingInterceptorWithTimber(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                if (!message.startsWith("{") && !message.startsWith("[")) {
                    Timber.tag("OkHttp").d(message)
                    return
                }
                try {
                    Timber.tag("OkHttp").d(
                        GsonBuilder().setPrettyPrinting().create().toJson(
                            JsonParser().parse(message)
                        )
                    )
                } catch (m: JsonSyntaxException) {
                    Logger.json(m.message)
                }
            }
        }).apply { level = HttpLoggingInterceptor.Level.BODY }
        return interceptor
    }
}