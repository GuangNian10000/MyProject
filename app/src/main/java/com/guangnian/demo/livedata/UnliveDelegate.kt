package com.guangnian.demo.livedata

import com.guangnian.mvvm.callback.unlive.keyvalue.domain.dispatch.UnliveData
import com.hjq.gson.factory.GsonFactory
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
/**
 * @author 光年
 * @since 2026/1/12
 * @summary 属性委托类：将属性读写桥接到 UnliveData
 */
class UnliveDelegate<T>(
    private val defaultValue: T,
    private val type: Class<T>
) : ReadWriteProperty<Any?, T> {

    /**
     * 当 SP 中没有值时，不仅使用默认值，还把这个默认值写入到 SP 中（这样下次读取更快，且能在文件中看到默认配置）
     * */
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val key = property.name
        val cached = StateLiveData.getEntity(key, type)

        if (cached == null) {
            // 如果没值，不仅返回默认值，还顺手存进去（初始化）
            setValue(thisRef, property, defaultValue)
            return defaultValue
        }
        return cached
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        val key = property.name
        // 根据类型调用对应的 UnliveData.put 方法
        // 这里利用了 Kotlin 的智能转换和原有 UnliveData 的重载方法
        when (value) {
            is String -> UnliveData.put(key, value)
            is Int -> UnliveData.put(key, value)
            is Boolean -> UnliveData.put(key, value)
            is Float -> UnliveData.put(key, value)
            is Long -> UnliveData.put(key, value)
            else -> {
                // 如果是对象，序列化成 JSON 存入 (复用 String 存储逻辑)
                val json = GsonFactory.getSingletonGson().toJson(value)
                UnliveData.put(key, json)
            }
        }
    }
}

// 扩展函数：简化委托的创建语法
inline fun <reified T> unlive(defaultValue: T): UnliveDelegate<T> {
    return UnliveDelegate(defaultValue, T::class.java)
}