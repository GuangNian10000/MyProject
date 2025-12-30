package com.guangnian.mvvm.callback.unlive.mvi.domain.dispatch

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.guangnian.mvvm.callback.unlive.mvi.domain.queue.FixedLengthList
import com.guangnian.mvvm.callback.unlive.mvi.domain.result.OneTimeMessage
import java.util.HashMap

/**
 * Create by KunMinX at 2022/7/3
 * Converted to Kotlin
 */
open class MviDispatcher<T> : ViewModel(), DefaultLifecycleObserver {

    private val mOwner = HashMap<Int, LifecycleOwner>()
    private val mFragmentOwner = HashMap<Int, LifecycleOwner>()
    private val mObservers = HashMap<Int, Observer<T>>()
    private val mResults = FixedLengthList<OneTimeMessage<T>>()

    protected open fun initQueueMaxLength(): Int {
        return DEFAULT_QUEUE_LENGTH
    }

    fun output(activity: AppCompatActivity, observer: Observer<T>) {
        activity.lifecycle.addObserver(this)
        val identityId = System.identityHashCode(activity)
        outputTo(identityId, activity, observer)
    }

    fun output(fragment: Fragment, observer: Observer<T>) {
        fragment.lifecycle.addObserver(this)
        val identityId = System.identityHashCode(fragment)
        this.mFragmentOwner[identityId] = fragment
        outputTo(identityId, fragment.viewLifecycleOwner, observer)
    }

    private fun outputTo(identityId: Int, owner: LifecycleOwner, observer: Observer<T>) {
        this.mOwner[identityId] = owner
        this.mObservers[identityId] = observer
        for (result in mResults) {
            result.observe(owner, observer)
        }
    }

    protected fun sendResult(intent: T) {
        mResults.init(initQueueMaxLength()) { mutableResult ->
            for (entry in mObservers.entries) {
                val observer = entry.value
                mutableResult.removeObserver(observer)
            }
        }

        var eventExist = false
        for (result in mResults) {
            val id1 = System.identityHashCode(result.get())
            val id2 = System.identityHashCode(intent)
            if (id1 == id2) {
                eventExist = true
                break
            }
        }

        if (!eventExist) {
            val result = OneTimeMessage(intent)
            for (entry in mObservers.entries) {
                val key = entry.key
                val observer = entry.value
                val owner = mOwner[key]!!
                result.observe(owner, observer)
            }
            mResults.add(result)
        }

        var result: OneTimeMessage<T>? = null
        for (r in mResults) {
            val id1 = System.identityHashCode(r.get())
            val id2 = System.identityHashCode(intent)
            if (id1 == id2) {
                result = r
                break
            }
        }
        result?.set(intent)
    }

    fun input(intent: T) {
        onHandle(intent)
    }

    protected open fun onHandle(intent: T) {
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        val isFragment = owner is Fragment
        // 根据是否是 Fragment 选择遍历的 Map
        val entrySet = if (isFragment) mFragmentOwner.entries else mOwner.entries

        for (entry in entrySet) {
            val owner1 = entry.value
            if (owner1 == owner) {
                val key = entry.key
                mOwner.remove(key)
                if (isFragment) mFragmentOwner.remove(key)

                // Java代码使用了 Objects.requireNonNull，这里用 !! 或者安全调用
                val observer = mObservers[key]
                if (observer != null) {
                    for (mutableResult in mResults) {
                        mutableResult.removeObserver(observer)
                    }
                }
                mObservers.remove(key)
                break
            }
        }
        if (mObservers.size == 0) mResults.clear()
    }

    companion object {
        private const val DEFAULT_QUEUE_LENGTH = 10
    }
}