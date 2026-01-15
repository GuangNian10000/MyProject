package com.guangnian.demo.app.ext

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable

/**
 * @author GuangNian
 * @description:
 * @date : 2023/12/26 16:35
 */
// 从 Drawable 中提取颜色值
fun extractColorFromBackgroundDrawable(drawable: Drawable?): Int {
    if (drawable is ColorDrawable) {
        return drawable.color
    }
    // 如果背景不是颜色，可以在这里处理其他类型的 Drawable
    // 如果背景是图片等其他类型，可以根据需要进行处理
    return 0 // 默认返回0或者其他你认为合适的颜色值
}