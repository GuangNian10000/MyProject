package com.guangnian.mvvm.callback.unlive.keyvalue.domain.dispatch

import com.guangnian.mvvm.callback.unlive.keyvalue.domain.event.KeyValueMsg
import com.guangnian.mvvm.callback.unlive.keyvalue.utils.MKUtils
import com.guangnian.mvvm.callback.unlive.mvi.domain.dispatch.MviDispatcher
import java.util.HashMap

/**
 * TODO tip：通过内聚，消除 Key Value Getter Putter init 样板代码，
 * 开发者只需声明 Key 列表即可使用 get() put()，
 * 且顺带可从唯一出口 output 处响应配置变化完成 UI 刷新
 *
 * Create by KunMinX at 2022/8/15
 * Converted to Kotlin
 */
open class KeyValueDispatcher : MviDispatcher<KeyValueMsg>() {
    val keyValues = HashMap<String, Any>()
    val mMKUtils: MKUtils = MKUtils.getInstance(moduleName())

    open fun moduleName(): String {
        return "GlobalConfigs"
    }

    override fun onHandle(intent: KeyValueMsg) {
        sendResult(intent)
    }

    fun put(key: String, value: Any) {
        keyValues[key] = value
        mMKUtils.put(key, value)
        input(KeyValueMsg(key))
    }

    fun put(key: String, value: String) {
        keyValues[key] = value
        mMKUtils.put(key, value)
        input(KeyValueMsg(key))
    }

    fun put(key: String, value: Int) {
        keyValues[key] = value
        mMKUtils.put(key, value)
        input(KeyValueMsg(key))
    }

    fun put(key: String, value: Long) {
        keyValues[key] = value
        mMKUtils.put(key, value)
        input(KeyValueMsg(key))
    }

    fun put(key: String, value: Float) {
        keyValues[key] = value
        mMKUtils.put(key, value)
        input(KeyValueMsg(key))
    }

    fun put(key: String, value: Boolean) {
        keyValues[key] = value
        mMKUtils.put(key, value)
        input(KeyValueMsg(key))
    }

    fun getString(key: String): String {
        if (keyValues[key] == null) {
            keyValues[key] = mMKUtils.getString(key, "")
        }
        return keyValues[key] as String
    }

    fun getInt(key: String): Int {
        if (keyValues[key] == null) {
            keyValues[key] = mMKUtils.getInt(key, 0)
        }
        return keyValues[key] as Int
    }

    fun getLong(key: String): Long {
        if (keyValues[key] == null) {
            keyValues[key] = mMKUtils.getLong(key, 0L)
        }
        return keyValues[key] as Long
    }

    fun getFloat(key: String): Float {
        if (keyValues[key] == null) {
            keyValues[key] = mMKUtils.getFloat(key, 0f)
        }
        return keyValues[key] as Float
    }

    fun getBoolean(key: String): Boolean {
        if (keyValues[key] == null) {
            keyValues[key] = mMKUtils.getBoolean(key, false)
        }
        return keyValues[key] as Boolean
    }
}