package com.guangnian.demo.livedata

/**
 * @author 光年
 * @since 2026/1/12
 * @summary 全局事件状态
 * 使用：
 *  GlobalEvents.observe<ScanResultEvent>(this) { event ->
 *
 *  }
 */
data class ScanResultEvent(val code: String)
data class ScanResultEvent1(val code: String)
data class ScanResultEvent2(val code: String)
