package com.guangnian.demo.livedata

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.google.gson.JsonSyntaxException
import com.guangnian.demo.livedata.StateLiveData.getEntity
import com.guangnian.mvvm.callback.unlive.keyvalue.domain.dispatch.UnliveData
import com.guangnian.mvvm.callback.unlive.keyvalue.domain.event.KeyValueMsg
import com.hjq.gson.factory.GsonFactory

/**
 * @author 光年
 * @since 2025/12/30
 * @summary
 */
object StateLiveData {

    fun <T> observe(content: LifecycleOwner, key: String, type: Class<T>, callback: (T?) -> Unit) {
        when(content){
            is AppCompatActivity -> UnliveData.output(content,key) { msg ->
                val value = getEntity(key, type)
                callback(value)
            }
            is Fragment -> UnliveData.output(content,key) { msg ->
                val value = getEntity(key, type)
                callback(value)
            }
        }
    }

    fun <T> observe(content: LifecycleOwner, callback: (KeyValueMsg) -> Unit) {
        when(content){
            is AppCompatActivity -> UnliveData.output(content,callback)
            is Fragment ->  UnliveData.output(content,callback)
        }
    }

    fun <T> getEntity(key: String, type: Class<T>): T? {
        val cached = UnliveData.mKeyValueDispatcher.keyValues[key]
        if (cached != null && type.isInstance(cached)) {
            return cached as T
        }

        val json = UnliveData.mKeyValueDispatcher.mSPUtils.getString(key, "")

        if (type == String::class.java) return UnliveData.mKeyValueDispatcher.mSPUtils.getString(key, "") as T?
        if (type == Int::class.javaObjectType || type == Int::class.java) return UnliveData.mKeyValueDispatcher.mSPUtils.getInt(key, 0) as T?
        if (type == Boolean::class.javaObjectType || type == Boolean::class.java) return UnliveData.mKeyValueDispatcher.mSPUtils.getBoolean(key, false) as T?
        if (type == Float::class.javaObjectType || type == Float::class.java) return UnliveData.mKeyValueDispatcher.mSPUtils.getFloat(key, 0f) as T?
        if (type == Long::class.javaObjectType || type == Long::class.java) return UnliveData.mKeyValueDispatcher.mSPUtils.getLong(key, 0L) as T?

        // 剩下的认为是实体类，走 Gson 解析
        if (json.isNotEmpty()) {
            try {
                val obj = GsonFactory.getSingletonGson().fromJson(json,type)//gson.fromJson(json, type)
                if (obj != null) {
                    UnliveData.mKeyValueDispatcher.keyValues[key] = obj
                    return obj
                }
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
            }
        }
        return null
    }

}