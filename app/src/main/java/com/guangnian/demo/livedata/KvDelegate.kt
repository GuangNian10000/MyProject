package com.guangnian.demo.livedata

import com.guangnian.demo.livedata.StateLiveData.getEntity
import com.guangnian.mvvm.callback.unlive.keyvalue.domain.dispatch.UnliveData.mKeyValueDispatcher
import kotlin.reflect.KProperty

/**
* @author 光年
* @since 2025/12/31
* @summary 
*/
class KvDelegate<T>(
    private val default: T
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getEntity(property.name) ?: default
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        mKeyValueDispatcher.put(property.name, value)
    }
}
