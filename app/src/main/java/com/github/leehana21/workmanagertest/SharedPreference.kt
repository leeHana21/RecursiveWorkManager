package com.github.leehana21.workmanagertest

import android.content.Context
import android.content.SharedPreferences

class SharedPreference(context: Context) {
    companion object {
        const val TEST_NUMBER = "TEST_NUMBER"
        const val TEST_HISTORY = "TEST_HISTORY"
        const val TEST_COUNT = "TEST_COUNT"
        const val TEST_STOP = "TEST_STOP"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    var testNumber : Int
        get() = prefs.getInt(TEST_NUMBER,0)
        set(value) = prefs.edit().putInt(TEST_NUMBER,value).apply()

    var testHistory : String
        get() = prefs.getString(TEST_HISTORY,"") ?: ""
        set(value) = prefs.edit().putString(TEST_HISTORY,value).apply()

    var testCount : Int
        get() = prefs.getInt(TEST_COUNT,0)
        set(value) = prefs.edit().putInt(TEST_COUNT,value).apply()

    var testStop : String
        get() = prefs.getString(TEST_STOP,"") ?: ""
        set(value) = prefs.edit().putString(TEST_STOP,value).apply()
}