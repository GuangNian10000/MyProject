package com.guangnian.mvvm.callback.unlive.mvi.scope

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

/**
 * Create by KunMinX at 2022/7/6
 */
class ApplicationInstance private constructor() : ViewModelStoreOwner {
    private val mAppViewModelStore: ViewModelStore by lazy { ViewModelStore() }

    override fun getViewModelStore(): ViewModelStore {
        return mAppViewModelStore
    }

    companion object {
        private val sInstance = ApplicationInstance()
        @JvmStatic
        fun getInstance(): ApplicationInstance {
            return sInstance
        }
    }
}
