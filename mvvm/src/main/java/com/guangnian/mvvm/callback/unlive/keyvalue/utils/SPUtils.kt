package com.guangnian.mvvm.callback.unlive.keyvalue.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import java.util.Collections
import java.util.HashMap

/**
 * Converted to Kotlin
 */
@SuppressLint("ApplySharedPref")
class SPUtils private constructor(spName: String, mode: Int) {

    private val sp: SharedPreferences = AppUtils.app.getSharedPreferences(spName, mode)

    companion object {
        private val SP_UTILS_MAP: MutableMap<String, SPUtils> = HashMap()

        /**
         * Return the single [SPUtils] instance
         */
        @JvmOverloads
        fun getInstance(spName: String = "", mode: Int = Context.MODE_PRIVATE): SPUtils {
            var name = spName
            if (isSpace(name)) {
                name = "spUtils"
            }
            var spUtils = SP_UTILS_MAP[name]
            if (spUtils == null) {
                synchronized(SPUtils::class.java) {
                    spUtils = SP_UTILS_MAP[name]
                    if (spUtils == null) {
                        spUtils = SPUtils(name, mode)
                        SP_UTILS_MAP[name] = spUtils!!
                    }
                }
            }
            return spUtils!!
        }

        private fun isSpace(s: String?): Boolean {
            if (s == null) {
                return true
            }
            for (i in s.indices) {
                if (!Character.isWhitespace(s[i])) {
                    return false
                }
            }
            return true
        }
    }

    fun put(key: String, value: String, isCommit: Boolean = false) {
        if (isCommit) {
            sp.edit().putString(key, value).commit()
        } else {
            sp.edit().putString(key, value).apply()
        }
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return sp.getString(key, defaultValue) ?: defaultValue
    }

    fun put(key: String, value: Int, isCommit: Boolean = false) {
        if (isCommit) {
            sp.edit().putInt(key, value).commit()
        } else {
            sp.edit().putInt(key, value).apply()
        }
    }

    fun getInt(key: String, defaultValue: Int = -1): Int {
        return sp.getInt(key, defaultValue)
    }

    fun put(key: String, value: Long, isCommit: Boolean = false) {
        if (isCommit) {
            sp.edit().putLong(key, value).commit()
        } else {
            sp.edit().putLong(key, value).apply()
        }
    }

    fun getLong(key: String, defaultValue: Long = -1L): Long {
        return sp.getLong(key, defaultValue)
    }

    fun put(key: String, value: Float, isCommit: Boolean = false) {
        if (isCommit) {
            sp.edit().putFloat(key, value).commit()
        } else {
            sp.edit().putFloat(key, value).apply()
        }
    }

    fun getFloat(key: String, defaultValue: Float = -1f): Float {
        return sp.getFloat(key, defaultValue)
    }

    fun put(key: String, value: Boolean, isCommit: Boolean = false) {
        if (isCommit) {
            sp.edit().putBoolean(key, value).commit()
        } else {
            sp.edit().putBoolean(key, value).apply()
        }
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sp.getBoolean(key, defaultValue)
    }

    fun put(key: String, value: Set<String>, isCommit: Boolean = false) {
        if (isCommit) {
            sp.edit().putStringSet(key, value).commit()
        } else {
            sp.edit().putStringSet(key, value).apply()
        }
    }

    fun getStringSet(key: String, defaultValue: Set<String> = Collections.emptySet()): Set<String> {
        return sp.getStringSet(key, defaultValue) ?: defaultValue
    }

    val all: Map<String, *>
        get() = sp.all

    fun contains(key: String): Boolean {
        return sp.contains(key)
    }

    fun remove(key: String, isCommit: Boolean = false) {
        if (isCommit) {
            sp.edit().remove(key).commit()
        } else {
            sp.edit().remove(key).apply()
        }
    }

    fun clear(isCommit: Boolean = false) {
        if (isCommit) {
            sp.edit().clear().commit()
        } else {
            sp.edit().clear().apply()
        }
    }
}