package kr.co.carboncheck.android.carboncheckapp.util

import android.content.Context
import android.content.SharedPreferences

class UserPreference {
    private val PREFERENCES_NAME = "user_preference"

    fun getPreferences(context: Context): SharedPreferences? {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }


}