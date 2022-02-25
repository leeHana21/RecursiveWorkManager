package com.github.leehana21.workmanagertest

import retrofit2.http.*

interface MainApiInterface {
    @Headers("Content-Type: application/json")
    @GET("v0/newstories.json?print=pretty")
    suspend fun rqList(): TestResponse
}