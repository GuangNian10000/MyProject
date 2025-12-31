package com.guangnian.mvvm.callback.unlive.mvi.domain.dispatch

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

/**
 * 纯事件分发器 (无 Key-Value 存储)
 * 用于发送指令、通知、跨页面跳转信号等。
 * @author 光年
 * @since 2025/12/31
 * @summary
 * 继承自 MviDispatcher<Any>，意味着它可以分发任意类型的对象。
 */
object GlobalEvents : MviDispatcher<Any>() {

    /**
     * 发送事件
     * @param event 任意对象 (建议使用 Sealed Class 或 Data Class)
     */
    fun send(event: Any) {
        input(event)
    }

    /**
     * 在 Activity 中监听特定类型的事件
     *
     * 使用示例:
     * GlobalEvents.observe<LoginSuccessEvent>(this) { event ->
     * // event 自动推导为 LoginSuccessEvent 类型
     * }
     */
    inline fun <reified T> observe(activity: AppCompatActivity, crossinline callback: (T) -> Unit) {
        // 调用父类 MviDispatcher 的 output 方法
        // 这里的 Observer<Any> 会收到所有分发的事件
        output(activity, Observer { event ->
            // 使用 reified 泛型特性进行类型过滤
            if (event is T) {
                callback(event)
            }
        })
    }

    /**
     * 在 Fragment 中监听特定类型的事件
     *
     * 使用示例:
     * GlobalEvents.observe<ShowToastEvent>(this) { event ->
     * Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
     */
    inline fun <reified T> observe(fragment: Fragment, crossinline callback: (T) -> Unit) {
        output(fragment, Observer { event ->
            if (event is T) {
                callback(event)
            }
        })
    }
}