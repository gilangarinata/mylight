package com.codaholic.mylight.manage.cache

import android.content.Context
import android.content.SharedPreferences
import com.codaholic.mylight.manage.cache.PrefData.Companion.EMAIL
import com.codaholic.mylight.manage.cache.PrefData.Companion.FIREBASE_TOKEN
import com.codaholic.mylight.manage.cache.PrefData.Companion.POSITION
import com.codaholic.mylight.manage.cache.PrefData.Companion.PREF_APP
import com.codaholic.mylight.manage.cache.PrefData.Companion.TOKEN
import com.codaholic.mylight.manage.cache.PrefData.Companion.USERNAME
import com.codaholic.mylight.manage.cache.PrefData.Companion.USER_ID

class PrefManager(private val _context: Context) : PrefData {
    private val pref: SharedPreferences
    private val editor: SharedPreferences.Editor

    fun setLoginPref(token: String?, username:String?, email: String?, position:String?, id : String?, referal : String?) {
        editor.putString(USERNAME, username)
        editor.putString(TOKEN, token)
        editor.putString(EMAIL,email)
        editor.putString(POSITION,position)
        editor.putString(USER_ID,id)
        editor.putString("REFERAL", referal)
        editor.commit()
    }

    fun setFirebaseNotifToken(token: String?){
        editor.putString(FIREBASE_TOKEN,token)
        editor.commit()
    }

    fun logout(){
        editor.clear()
        editor.commit()
    }

    val firebaseToken: String get() = pref.getString(FIREBASE_TOKEN,"");

    val email: String
        get() = pref.getString(EMAIL, "")

    val username: String
        get() = pref.getString(USERNAME, "")

    val token: String
        get() = pref.getString(TOKEN, "")

    val position: String
        get() = pref.getString(POSITION, "")

    val userId: String
        get() = pref.getString(USER_ID, "")

    val referal: String
        get() = pref.getString("REFERAL", "")

    init {
        pref = _context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE)
        editor = pref.edit()
    }
}