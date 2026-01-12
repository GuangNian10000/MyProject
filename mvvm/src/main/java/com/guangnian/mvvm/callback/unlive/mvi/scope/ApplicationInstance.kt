package com.guangnian.mvvm.callback.unlive.mvi.scope

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

/**
 * Create by KunMinX at 2022/7/6
 */
class ApplicationInstance private constructor() : ViewModelStoreOwner {

    private val mAppViewModelStore: ViewModelStore by lazy { ViewModelStore() }

    // 【修改点】将 fun 改为 val，并使用 get()
    override val viewModelStore: ViewModelStore
        get() = mAppViewModelStore

    companion object {
        private val sInstance = ApplicationInstance()
        @JvmStatic
        fun getInstance(): ApplicationInstance {
            return sInstance
        }
    }
}
