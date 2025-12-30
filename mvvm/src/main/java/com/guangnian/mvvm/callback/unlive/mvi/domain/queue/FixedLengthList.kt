package com.guangnian.mvvm.callback.unlive.mvi.domain.queue

import java.util.LinkedList

/**
 * Create by KunMinX at 2022/7/5
 */
class FixedLengthList<T> : LinkedList<T>() {
    private var maxLength = 0
    private var hasBeenInit = false
    private var queueCallback: QueueCallback<T>? = null

    fun init(maxLength: Int, queueCallback: QueueCallback<T>?) {
        if (!hasBeenInit) {
            this.maxLength = maxLength
            this.queueCallback = queueCallback
            hasBeenInit = true
        }
    }

    override fun add(element: T): Boolean {
        if (size + 1 > maxLength) {
            val t1 = super.removeFirst()
            queueCallback?.onRemoveFirst(t1)
        }
        return super.add(element)
    }

    fun interface QueueCallback<T> {
        fun onRemoveFirst(t: T)
    }
}