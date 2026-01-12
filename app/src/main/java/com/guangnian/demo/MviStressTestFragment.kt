package com.guangnian.demo

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.guangnian.mvvm.callback.unlive.keyvalue.domain.dispatch.UnliveData
import com.guangnian.mvvm.callback.unlive.mvi.domain.dispatch.GlobalEvents
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * MVI 框架一键综合测试页 (修复版)
 */
class MviStressTestFragment : Fragment() {

    private lateinit var logContainer: LinearLayout
    private lateinit var scrollView: ScrollView
    private val dateFormatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    private val mainHandler = Handler(Looper.getMainLooper())

    // 定义测试用的事件实体
    data class TestEvent(val id: Int, val message: String)
    data class StickinessVerifyEvent(val timestamp: Long)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
        }

        // 按钮区
        val btnContainer = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 20)
        }

        val btnMixTest = createButton("1. 混合压力测试") { runMixStressTest() }
        val btnAntiShake = createButton("2. 防倒灌验证") { runAntiStickinessTest() }
        val btnClear = createButton("清空日志") { logContainer.removeAllViews() }

        btnContainer.addView(btnMixTest)
        btnContainer.addView(btnAntiShake)
        btnContainer.addView(btnClear)
        root.addView(btnContainer)

        // 日志显示区
        scrollView = ScrollView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(20, 20, 20, 20)
        }
        logContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
        }
        scrollView.addView(logContainer)
        root.addView(scrollView)

        initObservers()
        return root
    }

    private fun createButton(text: String, onClick: () -> Unit): Button {
        return Button(context).apply {
            this.text = text
            setOnClickListener { onClick() }
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
    }

    private fun addLog(tag: String, msg: String, color: Int = Color.BLACK) {
        val textView = TextView(context).apply {
            text = "[${dateFormatter.format(Date())}] $tag: $msg"
            setTextColor(color)
            textSize = 12f
        }
        mainHandler.post {
            logContainer.addView(textView)
            scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
        }
    }

    // ================= 初始化监听 =================

    private fun initObservers() {
        // 1. 监听全局事件 (内存) - 正常业务逻辑
        GlobalEvents.observe<TestEvent>(this) { event ->
            addLog("GlobalEvent", "收到事件 -> ID:${event.id} 内容:${event.message}", Color.BLUE)
        }

        // 2. 监听 KeyValue 变化
        UnliveData.output(this, "robot_speed") { msg ->
            val value = UnliveData.getInt("robot_speed")
            addLog("KeyValue", "收到配置变更 -> Key:${msg.currentKey} Value:$value", Color.parseColor("#006400"))
        }

        // 【重要修改】删除了这里原本对 StickinessVerifyEvent 的监听
        // 因为如果在这里监听，它作为常驻观察者，收到消息是符合预期的，不应报错。
    }

    // ================= 测试逻辑 =================

    private fun runMixStressTest() {
        addLog("TEST", "=== 开始混合压力测试 (20次循环) ===", Color.MAGENTA)
        Thread {
            for (i in 1..20) {
                GlobalEvents.send(TestEvent(i, "Event-$i"))
                UnliveData.put("robot_speed", i * 100)
                Thread.sleep(20)
            }
            mainHandler.post { addLog("TEST", "=== 发送完毕 ===", Color.MAGENTA) }
        }.start()
    }

    /**
     * 防倒灌测试逻辑：
     * 1. 发送一个事件。
     * 2. 然后注册一个新的观察者。
     * 3. 预期：新的观察者不应该收到刚才发的事件。
     */
    @SuppressLint("CommitTransaction")
    private fun runAntiStickinessTest() {
        addLog("TEST", "=== 开始防倒灌测试 ===", Color.MAGENTA)

        val eventTime = System.currentTimeMillis()
        val eventObj = StickinessVerifyEvent(eventTime)

        // 1. 发送事件 (此时没有任何针对该事件的监听者)
        addLog("Step1", "发送事件: $eventTime")
        GlobalEvents.send(eventObj)

        // 2. 延迟 200ms 后，模拟进入新页面或动态注册新观察者
        mainHandler.postDelayed({
            addLog("Step2", "动态注册新观察者 (预期: 保持沉默，不应收到消息)")

            var receivedOldData = false

            // 临时注册一个观察者
            val tempObserver = Observer<Any> { event ->
                if (event is StickinessVerifyEvent && event.timestamp == eventTime) {
                    receivedOldData = true
                    addLog("RESULT", "【失败】发生了倒灌！新观察者收到了旧消息。", Color.RED)
                }
            }

            // 手动调用底层 output 注册
            GlobalEvents.output(this, tempObserver)

            // 3. 再延迟一点检查结果
            mainHandler.postDelayed({
                if (!receivedOldData) {
                    addLog("RESULT", "【通过】测试成功！新观察者未收到旧消息。", Color.parseColor("#FF8C00")) // Orange
                }
                // (可选) 移除这个临时观察者，防止污染后续测试，但在 Fragment 销毁时会自动移除
            }, 500)

        }, 200)
    }
}