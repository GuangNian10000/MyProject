package com.guangnian.mvvm.util

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
/**
 * @author 光年
 * @since 2025/12/30
 * @summary
 */
// 放在 View 内部或者工具类中
private fun getActivityFromContext(context: Context?): AppCompatActivity? {
    if (context == null) return null

    // 1. 如果直接就是 Activity，返回
    if (context is AppCompatActivity) {
        return context
    }

    // 2. 如果是包装类 (ContextThemeWrapper, TintContextWrapper 等都继承自 ContextWrapper)
    // 则剥开一层，递归查找 baseContext
    if (context is ContextWrapper) {
        return getActivityFromContext(context.baseContext)
    }

    // 3. 实在找不到 (可能是 Application Context)，返回 null
    return null
}