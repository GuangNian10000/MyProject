package com.guangnian.mvvm.callback.unlive.mvi.domain.result

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

/**
 * Create by KunMinX at 2022/8/16
 * Converted to Kotlin
 */
class OneTimeMessage<T>(value: T) {

    private val mObservers = SafeIterableMap<Observer<in T>, ObserverWrapper>()
    private var mActiveCount = 0
    private var mChangingActiveState = false

    @Volatile
    private var mData: Any? = NOT_SET

    private var mVersion: Int
    private var mCurrentVersion = START_VERSION

    private var mDispatchingValue = false
    private var mDispatchInvalidated = false

    init {
        mData = value
        mVersion = START_VERSION + 1
    }

    @Suppress("UNCHECKED_CAST")
    private fun considerNotify(observer: ObserverWrapper) {
        if (!observer.mActive) return
        if (!observer.shouldBeActive()) {
            observer.activeStateChanged(false)
            return
        }
        if (observer.mLastVersion >= mVersion) return
        observer.mLastVersion = mVersion
        observer.mObserver.onChanged(mData as T)
    }

    private fun dispatchingValue(initiator: ObserverWrapper?) {
        var currentInitiator = initiator
        if (mDispatchingValue) {
            mDispatchInvalidated = true
            return
        }
        mDispatchingValue = true
        do {
            mDispatchInvalidated = false
            if (currentInitiator != null) {
                considerNotify(currentInitiator)
                currentInitiator = null
            } else {
                val iterator = mObservers.iteratorWithAdditions()
                while (iterator.hasNext()) {
                    considerNotify(iterator.next().value)
                    if (mDispatchInvalidated) break
                }
            }
        } while (mDispatchInvalidated)
        mDispatchingValue = false
    }

    @MainThread
    fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED) return
        val wrapper = LifecycleBoundObserver(owner, observer)
        val existing = mObservers.putIfAbsent(observer, wrapper)
        if (existing != null && !existing.isAttachedTo(owner)) {
            throw IllegalArgumentException("Cannot add the same observer with different lifecycles")
        }
        if (existing != null) return
        // 关键逻辑：OneTimeMessage 特有，防止注册时立刻分发旧数据
        mCurrentVersion = mVersion
        owner.lifecycle.addObserver(wrapper)
    }

    @MainThread
    fun removeObserver(observer: Observer<in T>) {
        val removed = mObservers.remove(observer) ?: return
        removed.detachObserver()
        removed.activeStateChanged(false)
    }

    @MainThread
    fun set(value: T) {
        mVersion++
        mData = value
        dispatchingValue(null)
    }

    @Suppress("UNCHECKED_CAST")
    fun get(): T? {
        val data = mData
        if (data !== NOT_SET) {
            return data as T
        }
        return null
    }

    @MainThread
    internal fun changeActiveCounter(change: Int) {
        var previousActiveCount = mActiveCount
        mActiveCount += change
        if (mChangingActiveState) return
        mChangingActiveState = true
        try {
            while (previousActiveCount != mActiveCount) {
                previousActiveCount = mActiveCount
            }
        } finally {
            mChangingActiveState = false
        }
    }

    private inner class LifecycleBoundObserver(
        val mOwner: LifecycleOwner,
        observer: Observer<in T>
    ) : ObserverWrapper(observer), LifecycleEventObserver {

        override fun shouldBeActive(): Boolean {
            return mOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        }

        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            var currentState = mOwner.lifecycle.currentState
            if (currentState == Lifecycle.State.DESTROYED) {
                removeObserver(mObserver)
                return
            }
            var prevState: Lifecycle.State? = null
            while (prevState != currentState) {
                prevState = currentState
                activeStateChanged(shouldBeActive())
                currentState = mOwner.lifecycle.currentState
            }
        }

        override fun isAttachedTo(owner: LifecycleOwner): Boolean {
            return mOwner === owner
        }

        override fun detachObserver() {
            mOwner.lifecycle.removeObserver(this)
        }
    }

    private abstract inner class ObserverWrapper(
        val mObserver: Observer<in T>
    ) {
        var mActive = false
        var mLastVersion = START_VERSION

        abstract fun shouldBeActive(): Boolean

        open fun isAttachedTo(owner: LifecycleOwner): Boolean {
            return false
        }

        open fun detachObserver() {}

        fun activeStateChanged(newActive: Boolean) {
            if (newActive == mActive) return
            mActive = newActive
            changeActiveCounter(if (mActive) 1 else -1)
            if (mActive && mVersion > mCurrentVersion) {
                dispatchingValue(this)
            }
        }
    }

    companion object {
        private const val START_VERSION = -1
        private val NOT_SET = Any()
    }
}