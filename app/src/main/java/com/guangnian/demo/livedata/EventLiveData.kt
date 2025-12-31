package com.guangnian.demo.livedata

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.guangnian.mvvm.callback.unlive.mvi.domain.dispatch.GlobalEvents

/**
 * @author 光年
 * @since 2025/12/31
 * @summary
 */
object EventLiveData {
    inline fun <reified T> observe(content: LifecycleOwner, crossinline callback: (T) -> Unit) {
        when(content){
            is AppCompatActivity -> GlobalEvents.observe<T>(content,callback)
            is Fragment -> GlobalEvents.observe<T>(content,callback)
        }
    }
}