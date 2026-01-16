package com.guangnian.mvvm.callback.unlive.keyvalue.utils

import android.content.Context
import com.tencent.mmkv.MMKV
import java.util.concurrent.ConcurrentHashMap

/**
 * @author 光年
 * @since 2026/1/12
 * @summary MMKV 工具类
 */
class MKUtils private constructor(spName: String, mode: Int) {

    // 直接持有引用，减少每次调用时的判空层级（如果初始化失败，mmkv 为 null，后续调用均安全忽略）
    private val mmkv: MMKV = MMKV.mmkvWithID(spName, mode)

    companion object {
        // 使用 ConcurrentHashMap 保证多线程下的 Map 读写安全
        private val INSTANCE_MAP = ConcurrentHashMap<String, MKUtils>()

        init {
            // 静态初始化，确保 MMKV 就绪
            try {
                MMKV.initialize(AppUtils.app)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * 获取实例
         * @param spName 存储名称，空字符串将默认使用 "mkUtils"
         * @param mode 模式，自动将 Context.MODE_PRIVATE 转换为 MMKV.SINGLE_PROCESS_MODE
         */
        @JvmOverloads
        fun getInstance(spName: String = "", mode: Int = MMKV.SINGLE_PROCESS_MODE): MKUtils {
            // 1. 处理名称：利用 Kotlin 扩展函数 isBlank() 替代原本的手动循环判断
            val key = spName.ifBlank { "mkUtils" }

            // 2. 处理模式：兼容旧代码传进来的 Context.MODE_PRIVATE (0)
            val finalMode = if (mode == Context.MODE_PRIVATE) MMKV.SINGLE_PROCESS_MODE else mode

            // 3. 线程安全的单例获取：使用 ConcurrentHashMap 的原子操作
            return INSTANCE_MAP.getOrPut(key) {
                MKUtils(key, finalMode)
            }
        }
    }

    // ================== 写操作 (Set/Put) ==================
    // isCommit 参数保留以兼容旧接口，但在 MMKV 中无实际意义，使用下划线变量名 _ 表示未被使用

    fun put(key: String, value: String, isCommit: Boolean = false) {
        mmkv.encode(key, value)
    }

    fun put(key: String, value: Int, isCommit: Boolean = false) {
        mmkv.encode(key, value)
    }

    fun put(key: String, value: Long, isCommit: Boolean = false) {
        mmkv.encode(key, value)
    }

    fun put(key: String, value: Float, isCommit: Boolean = false) {
        mmkv.encode(key, value)
    }

    fun put(key: String, value: Boolean, isCommit: Boolean = false) {
        mmkv.encode(key, value)
    }

    fun put(key: String, value: Set<String>, isCommit: Boolean = false) {
        mmkv.encode(key, value)
    }

    // ================== 读操作 (Get) ==================

    fun getString(key: String, defaultValue: String = ""): String =
        mmkv.decodeString(key, defaultValue) ?: defaultValue

    fun getInt(key: String, defaultValue: Int = -1): Int =
        mmkv.decodeInt(key, defaultValue) ?: defaultValue

    fun getLong(key: String, defaultValue: Long = -1L): Long =
        mmkv.decodeLong(key, defaultValue) ?: defaultValue

    fun getFloat(key: String, defaultValue: Float = -1f): Float =
        mmkv.decodeFloat(key, defaultValue) ?: defaultValue

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean =
        mmkv.decodeBool(key, defaultValue) ?: defaultValue

    fun getStringSet(key: String, defaultValue: Set<String> = emptySet()): Set<String> =
        mmkv.decodeStringSet(key, defaultValue) ?: defaultValue

    // ================== 其他操作 ==================

    /**
     * @deprecated MMKV 不支持高效获取所有键值对。此方法始终返回空 Map，请勿依赖此逻辑。
     */
    @Deprecated("MMKV does not support getAll efficiently. Do not use.", ReplaceWith("emptyMap()"))
    val all: Map<String, *>
        get() = emptyMap<String, Any>()

    fun contains(key: String): Boolean = mmkv.containsKey(key) == true

    fun remove(key: String, isCommit: Boolean = false) {
        mmkv.removeValueForKey(key)
    }

    fun clear(isCommit: Boolean = false) {
        mmkv.clearAll()
    }
}