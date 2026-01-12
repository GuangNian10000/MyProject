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

    /**
     * 支持直接传入 KProperty (例如 EventLiveConfig::userName)
     * 自动推导 key (变量名) 和 type (属性类型)
     * 使用 inline + reified 自动获取精准的 Java 类型，解决类型转换崩溃问题
     */
    inline fun <reified T> observe(
        owner: LifecycleOwner,
        prop: kotlin.reflect.KProperty0<T>, // 接收属性引用
        crossinline callback: (T) -> Unit
    ) {
        val key = prop.name
        // 直接通过 reified 泛型获取 Java Class，不再依赖不稳定的反射转换
        val type = T::class.java

        // 调用原有的 observe 方法
        observe(owner, key, type) { value ->
            if (value != null) {
                callback(value)
            }
        }
    }

    //监听指定key
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

    //监听所有key
    fun <T> observe(content: LifecycleOwner, callback: (KeyValueMsg) -> Unit) {
        when(content){
            is AppCompatActivity -> UnliveData.output(content,callback)
            is Fragment ->  UnliveData.output(content,callback)
        }
    }

    /**
     * 先判断 Key 是否存在。如果不存在，返回 null
     * */
    fun <T> getEntity(key: String, type: Class<T>): T? {
        val dispatcher = UnliveData.mKeyValueDispatcher

        // 1. 先查内存缓存
        val cached = dispatcher.keyValues[key]
        if (cached != null && type.isInstance(cached)) {
            return cached as T
        }

        // 2. 【关键修复】查持久化存储中是否存在该 Key
        // 如果不存在，直接返回 null，交给 Delegate 处理默认值
        if (!dispatcher.mSPUtils.contains(key)) {
            return null
        }

        // 3. 如果存在，再按类型读取
        val sp = dispatcher.mSPUtils

        if (type == String::class.java) {
            val value = sp.getString(key, "")
            dispatcher.keyValues[key] = value // 顺便回填缓存
            return value as T?
        }
        if (type == Int::class.javaObjectType || type == Int::class.java) {
            val value = sp.getInt(key, 0)
            dispatcher.keyValues[key] = value
            return value as T?
        }
        if (type == Boolean::class.javaObjectType || type == Boolean::class.java) {
            val value = sp.getBoolean(key, false)
            dispatcher.keyValues[key] = value
            return value as T?
        }
        if (type == Float::class.javaObjectType || type == Float::class.java) {
            val value = sp.getFloat(key, 0f)
            dispatcher.keyValues[key] = value
            return value as T?
        }
        if (type == Long::class.javaObjectType || type == Long::class.java) {
            val value = sp.getLong(key, 0L)
            dispatcher.keyValues[key] = value
            return value as T?
        }

        // 4. 对象类型解析 (JSON)
        val json = sp.getString(key, "")
        if (json.isNotEmpty()) {
            try {
                val obj = GsonFactory.getSingletonGson().fromJson(json, type)
                if (obj != null) {
                    dispatcher.keyValues[key] = obj
                    return obj
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

}