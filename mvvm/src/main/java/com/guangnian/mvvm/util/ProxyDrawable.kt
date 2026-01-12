package com.guangnian.mvvm.util

import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable

/**
 * GuangNian
 * 时间　: 2020/4/10
 * 描述　:使用DataBinding时使用该库 https://github.com/whataa/noDrawable
 * 只需要复制核心类 ProxyDrawable，Drawables至项目中即可
 * 可以减少大量的drawable.xml文件
 */
class ProxyDrawable : StateListDrawable() {

    var originDrawable: Drawable? = null
        private set

    override fun addState(stateSet: IntArray, drawable: Drawable) {
        if (stateSet.size == 1 && stateSet[0] == 0) {
            originDrawable = drawable
        }
        super.addState(stateSet, drawable)
    }
}
