package com.guangnian.demo.util.mk

import com.guangnian.demo.app.ext.toJson
import com.guangnian.demo.data.config.UserConfig
import com.guangnian.demo.data.model.bean.UserBean
import com.guangnian.demo.util.json.JsonUtil
import com.guangnian.mvvm.callback.unlive.keyvalue.utils.MKUtils

/**
 * @author GuangNian
 * @description: 用户数据存储
 * @date : 2023/11/17 16:13
 */
object UserMK {
    private val appKV by lazy { MKUtils.getInstance("UserMK") }

    //用户信息
    fun saveUserInfo(userBean: UserBean) {
        appKV.put(UserConfig.CACHE_USER_INFO, userBean.toJson())
    }

    fun getUserInfo(): UserBean {
        val json = appKV.getString(UserConfig.CACHE_USER_INFO)
        return JsonUtil.fromJson(json, UserBean::class.java)?:UserBean()
    }
}
