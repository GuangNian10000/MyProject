package com.guangnian.demo.livedata

import com.guangnian.demo.livedata.StateLiveData.unlive

/**
 * @author 光年
 * @since 2025/12/31
 * @summary 全局变量状态
 * 使用：
 *  StateLiveData.observe(this, StateConfig::userName) { userAge ->
 *
 *  }
 */
object StateConfig {
    var userName: String by unlive("default_name")
    var userAge: String by unlive("哈哈")
    // 甚至支持实体对象 (只要能被 Gson 序列化)
    //var userInfo: UserInfo by unlive(UserInfo())
}