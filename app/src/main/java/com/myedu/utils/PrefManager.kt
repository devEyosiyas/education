package com.myedu.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.myedu.utils.Constant.PREF_FILE
import java.io.IOException
import java.security.GeneralSecurityException

class PrefManager(context: Context) {
    private lateinit var preferences: SharedPreferences

    var isFirsTimer: Boolean
        get() = preferences.getBoolean(Constant.FIRST_TIMER, true)
        set(b) {
            preferences.edit().putBoolean(Constant.FIRST_TIMER, b).apply()
        }

    var name: String
        get() = preferences.getString(Constant.PREF_NAME, "").toString()
        set(name) {
            preferences.edit().putString(Constant.PREF_NAME, name).apply()
        }

    var email: String
        get() = preferences.getString(Constant.PREF_EMAIL, "").toString()
        set(email) {
            preferences.edit().putString(Constant.PREF_EMAIL, email).apply()
        }

    init {
        try {
            val masterKey: MasterKey =
                MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
            preferences = EncryptedSharedPreferences.create(
                context,
                PREF_FILE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}