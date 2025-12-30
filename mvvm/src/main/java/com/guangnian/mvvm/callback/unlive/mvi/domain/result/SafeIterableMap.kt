package com.guangnian.mvvm.callback.unlive.mvi.domain.result

import androidx.annotation.RestrictTo
import java.util.WeakHashMap

/**
 * LinkedList, which pretends to be a map and supports modifications during iterations.
 * It is NOT thread safe.
 *
 * @param <K> Key type
 * @param <V> Value type
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
open class SafeIterableMap<K, V> : Iterable<Map.Entry<K, V>> {

    internal var mStart: Entry<K, V>? = null
    private var mEnd: Entry<K, V>? = null

    // using WeakHashMap over List<WeakReference>, so we don't have to manually remove
    // WeakReferences that have null in them.
    private val mIterators = WeakHashMap<SupportRemove<K, V>, Boolean>()
    private var mSize = 0

    protected open fun get(k: K): Entry<K, V>? {
        var currentNode = mStart
        while (currentNode != null) {
            if (currentNode.key == k) {
                break
            }
            currentNode = currentNode.mNext
        }
        return currentNode
    }

    /**
     * If the specified key is not already associated
     * with a value, associates it with the given value.
     *
     * @param key key with which the specified value is to be associated
     * @param v   value to be associated with the specified key
     * @return the previous value associated with the specified key,
     * or `null` if there was no mapping for the key
     */
    open fun putIfAbsent(key: K, v: V): V? {
        val entry = get(key)
        if (entry != null) {
            return entry.value
        }
        put(key, v)
        return null
    }

    protected open fun put(key: K, v: V): Entry<K, V> {
        val newEntry = Entry(key, v)
        mSize++
        if (mEnd == null) {
            mStart = newEntry
            mEnd = mStart
            return newEntry
        }

        mEnd!!.mNext = newEntry
        newEntry.mPrevious = mEnd
        mEnd = newEntry
        return newEntry
    }

    /**
     * Removes the mapping for a key from this map if it is present.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with the specified key,
     * or `null` if there was no mapping for the key
     */
    open fun remove(key: K): V? {
        val toRemove = get(key) ?: return null
        mSize--
        if (!mIterators.isEmpty()) {
            for (iter in mIterators.keys) {
                iter.supportRemove(toRemove)
            }
        }

        if (toRemove.mPrevious != null) {
            toRemove.mPrevious!!.mNext = toRemove.mNext
        } else {
            mStart = toRemove.mNext
        }

        if (toRemove.mNext != null) {
            toRemove.mNext!!.mPrevious = toRemove.mPrevious
        } else {
            mEnd = toRemove.mPrevious
        }

        toRemove.mNext = null
        toRemove.mPrevious = null
        return toRemove.value
    }

    /**
     * @return the number of elements in this map
     */
    open fun size(): Int {
        return mSize
    }

    /**
     * @return an ascending iterator, which doesn't include new elements added during an
     * iteration.
     */
    override fun iterator(): MutableIterator<Map.Entry<K, V>> {
        val iterator = AscendingIterator(mStart, mEnd)
        mIterators[iterator] = false
        return iterator
    }

    /**
     * @return an descending iterator, which doesn't include new elements added during an
     * iteration.
     */
    fun descendingIterator(): Iterator<Map.Entry<K, V>> {
        val iterator = DescendingIterator(mEnd, mStart)
        mIterators[iterator] = false
        return iterator
    }

    /**
     * return an iterator with additions.
     */
    fun iteratorWithAdditions(): IteratorWithAdditions {
        val iterator = IteratorWithAdditions()
        mIterators[iterator] = false
        return iterator
    }

    /**
     * @return eldest added entry or null
     */
    fun eldest(): Map.Entry<K, V>? {
        return mStart
    }

    /**
     * @return newest added entry or null
     */
    fun newest(): Map.Entry<K, V>? {
        return mEnd
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }
        // Modified from original: check against SafeIterableMap instead of androidx...SafeIterableMap
        if (other !is SafeIterableMap<*, *>) {
            return false
        }
        val map = other as SafeIterableMap<*, *>
        if (this.size() != map.size()) {
            return false
        }
        val iterator1 = this.iterator()
        val iterator2 = map.iterator()
        while (iterator1.hasNext() && iterator2.hasNext()) {
            val next1 = iterator1.next()
            val next2 = iterator2.next()
            if (next1 != next2) {
                return false
            }
        }
        return !iterator1.hasNext() && !iterator2.hasNext()
    }

    override fun hashCode(): Int {
        var h = 0
        for (entry in this) {
            h += entry.hashCode()
        }
        return h
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("[")
        val iterator = iterator()
        while (iterator.hasNext()) {
            builder.append(iterator.next().toString())
            if (iterator.hasNext()) {
                builder.append(", ")
            }
        }
        builder.append("]")
        return builder.toString()
    }

    internal abstract class ListIterator<K, V>(
        var mExpectedEnd: Entry<K, V>?,
        var mNext: Entry<K, V>?
    ) : MutableIterator<Map.Entry<K, V>>, SupportRemove<K, V> {

        override fun hasNext(): Boolean {
            return mNext != null
        }

        // Iterator.remove() is not supported by default in the original implementation logic
        // (it only supported supportRemove calls from the map itself)
        override fun remove() {
            throw UnsupportedOperationException("remove() is not supported via Iterator. Use map.remove()")
        }

        override fun supportRemove(entry: Entry<K, V>) {
            if (mExpectedEnd === entry && entry === mNext) {
                mNext = null
                mExpectedEnd = null
            }

            if (mExpectedEnd === entry) {
                mExpectedEnd = backward(mExpectedEnd!!)
            }

            if (mNext === entry) {
                mNext = nextNode()
            }
        }

        private fun nextNode(): Entry<K, V>? {
            if (mNext === mExpectedEnd || mExpectedEnd == null) {
                return null
            }
            return forward(mNext!!)
        }

        override fun next(): Map.Entry<K, V> {
            val result = mNext
            mNext = nextNode()
            return result!!
        }

        abstract fun forward(entry: Entry<K, V>): Entry<K, V>?

        abstract fun backward(entry: Entry<K, V>): Entry<K, V>?
    }

    internal class AscendingIterator<K, V>(
        start: Entry<K, V>?,
        expectedEnd: Entry<K, V>?
    ) : ListIterator<K, V>(start, expectedEnd) {

        override fun forward(entry: Entry<K, V>): Entry<K, V>? {
            return entry.mNext
        }

        override fun backward(entry: Entry<K, V>): Entry<K, V>? {
            return entry.mPrevious
        }
    }

    private class DescendingIterator<K, V>(
        start: Entry<K, V>?,
        expectedEnd: Entry<K, V>?
    ) : ListIterator<K, V>(start, expectedEnd) {

        override fun forward(entry: Entry<K, V>): Entry<K, V>? {
            return entry.mPrevious
        }

        override fun backward(entry: Entry<K, V>): Entry<K, V>? {
            return entry.mNext
        }
    }

    inner class IteratorWithAdditions : MutableIterator<Map.Entry<K, V>>, SupportRemove<K, V> {
        private var mCurrent: Entry<K, V>? = null
        private var mBeforeStart = true

        override fun supportRemove(entry: Entry<K, V>) {
            if (entry === mCurrent) {
                mCurrent = mCurrent!!.mPrevious
                mBeforeStart = mCurrent == null
            }
        }

        override fun hasNext(): Boolean {
            if (mBeforeStart) {
                return mStart != null
            }
            return mCurrent != null && mCurrent!!.mNext != null
        }

        override fun next(): Map.Entry<K, V> {
            if (mBeforeStart) {
                mBeforeStart = false
                mCurrent = mStart
            } else {
                mCurrent = if (mCurrent != null) mCurrent!!.mNext else null
            }
            return mCurrent!!
        }

        override fun remove() {
            throw UnsupportedOperationException("remove() is not supported via Iterator. Use map.remove()")
        }
    }

    interface SupportRemove<K, V> {
        fun supportRemove(entry: Entry<K, V>)
    }

    class Entry<K, V>(
        override val key: K,
        override val value: V
    ) : Map.Entry<K, V> {

        var mNext: Entry<K, V>? = null
        var mPrevious: Entry<K, V>? = null

        override fun toString(): String {
            return "$key=$value"
        }

        override fun equals(other: Any?): Boolean {
            if (other === this) {
                return true
            }
            if (other !is Entry<*, *>) {
                return false
            }
            return key == other.key && value == other.value
        }

        override fun hashCode(): Int {
            return key.hashCode() xor value.hashCode()
        }
    }
}