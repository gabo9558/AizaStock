package com.quantum.aizastock.common

import android.content.Context
import android.content.SharedPreferences

object PreferenceUtils {

    const val SHARED_PREFERENCES = "SharedPreferences"

    const val EMAIL = "email"
    const val SESSION = "session"
    const val USER_UD = "user_id"
    const val USER_ROLE = "user_role"
    const val TOTAL_AMOUNT = "total_amount"
    const val CURRENT_AMOUNT_GROUP = "current_amount"
    const val CURRENT_NAME = "current_name"
    const val GROUP_ID = "group_id"

    fun saveTotalAmount(context: Context, amount: Int){
        val sp = context.getPreferences()
        val editor = sp.edit()
        editor.putInt(TOTAL_AMOUNT, amount).apply()
    }

    fun getTotalAmount(context: Context): Int{
        val sp = context.getPreferences()
        return sp.getInt(TOTAL_AMOUNT, 0)
    }

    fun saveCurrentAmount(context: Context, amount: Int){
        val sp = context.getPreferences()
        val editor = sp.edit()
        editor.putInt(CURRENT_AMOUNT_GROUP, amount).apply()
    }

    fun getCurrentAmount(context: Context): Int {
        val sp = context.getPreferences()
        return sp.getInt(CURRENT_AMOUNT_GROUP, 0)
    }

    fun saveCurrentID(context: Context, group_id: Int){
        val sp = context.getPreferences()
        val editor = sp.edit()
        editor.putInt(GROUP_ID, group_id).apply()
    }

    fun getCurrentID(context: Context): Int{
        val sp = context.getPreferences()
        return sp.getInt(GROUP_ID, 0)
    }

    fun saveCurrentName(context: Context, name: String){
        val sp = context.getPreferences()
        val editor = sp.edit()
        editor.putString(CURRENT_NAME, name).apply()
    }

    fun getCurrentName(context: Context): String?{
        val sp = context.getPreferences()
        return sp.getString(CURRENT_NAME, "")
    }

    fun saveUserRole(context: Context, user_role: String){
        val sp = context.getPreferences()
        val editor = sp.edit()
        editor.putString(USER_ROLE, user_role).apply()
    }

    fun getUserRole(context: Context): String?{
        val sp = context.getPreferences()
        return sp.getString(USER_ROLE, "")
    }

    fun saveUserID(context: Context, user_id: String){
        val sp = context.getPreferences()
        val editor = sp.edit()
        editor.putString(USER_UD, user_id).apply()
    }

    fun getUserID(context: Context): String? {
        val sp = context.getPreferences()
        return sp.getString(USER_UD, "")
    }

    fun saveEmail(context: Context, email: String){
        val sp = context.getPreferences()
        val editor = sp.edit()
        editor.putString(EMAIL, email).apply()
    }

    fun getEmail(context: Context): String?{
        val sp = context.getPreferences()
        return sp.getString(EMAIL, "")
    }

    fun saveSession(context: Context, session: String){
        val sp = context.getPreferences()
        val editor = sp.edit()
        editor.putString(SESSION, session).apply()
    }

    fun getSession(context: Context): String?{
        val sp = context.getPreferences()
        return sp.getString(SESSION, "")
    }

    fun deleteAll(context: Context){
        val sp = context.getPreferences()
        sp.edit().clear().apply()
    }

    private fun Context.getPreferences(): SharedPreferences {
        return getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }
}