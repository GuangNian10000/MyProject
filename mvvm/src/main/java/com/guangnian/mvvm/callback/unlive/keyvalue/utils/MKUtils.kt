package com.guangnian.mvvm.callback.unlive.keyvalue.utils

import android.annotation.SuppressLint
import com.tencent.mmkv.MMKV
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

class MKUtils private constructor(
    mmkvId: String,
    multiProcess: Boolean
) {
    private val mmkv: MMKV = if (multiProcess) {
        MMKV.mmkvWithID(mmkvId, MMKV.MULTI_PROCESS_MODE)
    } else {
        MMKV.mmkvWithID(mmkvId, MMKV.SINGLE_PROCESS_MODE)
    }

    companion object {

        private val MK_UTILS_MAP = ConcurrentHashMap<String, MKUtils>()

        @JvmOverloads
        fun getInstance(
            spName: String = "",
            multiProcess: Boolean = false
        ): MKUtils {
            val name = if (isSpace(spName)) "MKUtils" else spName
            return MK_UTILS_MAP[name]
                ?: synchronized(MKUtils::class.java) {
                    MK_UTILS_MAP[name]
                        ?: MKUtils(name, multiProcess).also {
                            MK_UTILS_MAP[name] = it
                        }
                }
        }

        private fun isSpace(s: String?): Boolean {
            if (s == null) return true
            return s.all { it.isWhitespace() }
        }
    }

    /* ===================== put ===================== */

    fun put(key: String, value: Any) {
        mmkv.encode(key, value.toString())
    }

    fun put(key: String, value: String) {
        mmkv.encode(key, value)
    }

    fun put(key: String, value: Int) {
        mmkv.encode(key, value)
    }

    fun put(key: String, value: Long) {
        mmkv.encode(key, value)
    }

    fun put(key: String, value: Float) {
        mmkv.encode(key, value)
    }

    fun put(key: String, value: Boolean) {
        mmkv.encode(key, value)
    }

    fun put(key: String, value: Set<String>) {
        mmkv.encode(key, value)
    }

    /* ===================== get ===================== */

    fun getString(key: String, defaultValue: String = ""): String {
        return mmkv.decodeString(key, defaultValue) ?: defaultValue
    }

    fun getInt(key: String, defaultValue: Int = -1): Int {
        return mmkv.decodeInt(key, defaultValue)
    }

    fun getLong(key: String, defaultValue: Long = -1L): Long {
        return mmkv.decodeLong(key, defaultValue)
    }

    fun getFloat(key: String, defaultValue: Float = -1f): Float {
        return mmkv.decodeFloat(key, defaultValue)
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return mmkv.decodeBool(key, defaultValue)
    }

    fun getStringSet(
        key: String,
        defaultValue: Set<String> = Collections.emptySet()
    ): Set<String> {
        return mmkv.decodeStringSet(key, defaultValue) ?: defaultValue
    }

    /* ===================== other ===================== */

//    val all: Map<String, *>
//        get() = mmkv.allKeys()?.associateWith { mmkv.decodeString(it) }
//            ?: emptyMap()

    fun contains(key: String): Boolean {
        return mmkv.containsKey(key)
    }

    fun remove(key: String) {
        mmkv.removeValueForKey(key)
    }

    fun clear(isCommit: Boolean = false) {
        mmkv.clearAll()
    }
}
