package com.guangnian.demo.livedata

import com.guangnian.demo.data.model.bean.LoginBean
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
    var loginBean: LoginBean by unlive(LoginBean()) //登录信息
}