package com.guangnian.mvvm.callback.unlive.keyvalue.domain.dispatch

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.guangnian.mvvm.callback.unlive.keyvalue.domain.event.KeyValueMsg

/**
 * TODO tip: 此处基于 KeyValueDispatcher 提供全局配置的默认实现，
 * 开发者亦可继承 KeyValueDispatcher 或效仿本类实现局部或全局用 KeyValueStore
 *
 * Create by KunMinX at 2022/8/15
 * Converted to Kotlin
 */
object UnliveData {
    val mKeyValueDispatcher = KeyValueDispatcher()

    fun output(activity: AppCompatActivity, key: String, observer: Observer<KeyValueMsg>) {
        // 调用父类的 output，但在回调里加一层判断
        mKeyValueDispatcher.output(activity) { msg ->
            if (msg.currentKey == key) {
                observer.onChanged(msg)
            }
        }
    }

    fun output(fragment: Fragment, key: String, observer: Observer<KeyValueMsg>) {
        mKeyValueDispatcher.output(fragment) { msg ->
            if (msg.currentKey == key) {
                observer.onChanged(msg)
            }
        }
    }

    fun output(activity: AppCompatActivity, observer: Observer<KeyValueMsg>) {
        mKeyValueDispatcher.output(activity, observer)
    }

    fun output(fragment: Fragment, observer: Observer<KeyValueMsg>) {
        mKeyValueDispatcher.output(fragment, observer)
    }

    fun put(key: String, value: String) {
        mKeyValueDispatcher.put(key, value)
    }

    fun put(key: String, value: Int) {
        mKeyValueDispatcher.put(key, value)
    }

    fun put(key: String, value: Long) {
        mKeyValueDispatcher.put(key, value)
    }

    fun put(key: String, value: Float) {
        mKeyValueDispatcher.put(key, value)
    }

    fun put(key: String, value: Boolean) {
        mKeyValueDispatcher.put(key, value)
    }

    fun getString(key: String): String {
        return mKeyValueDispatcher.getString(key)
    }

    fun getInt(key: String): Int {
        return mKeyValueDispatcher.getInt(key)
    }

    fun getLong(key: String): Long {
        return mKeyValueDispatcher.getLong(key)
    }

    fun getFloat(key: String): Float {
        return mKeyValueDispatcher.getFloat(key)
    }

    fun getBoolean(key: String): Boolean {
        return mKeyValueDispatcher.getBoolean(key)
    }
}