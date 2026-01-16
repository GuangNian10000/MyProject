package com.guangnian.demo.util.mk

import com.guangnian.demo.data.config.AppConfig
import com.guangnian.demo.data.model.bean.BgData
import com.guangnian.mvvm.callback.unlive.keyvalue.utils.MKUtils
import com.tencent.mmkv.MMKV

/**
 * @author GuangNian
 * @description: 系统数据存储
 * @date : 2023/11/17 16:13
 */
object AppMK {
    private val appKV by lazy { MKUtils.getInstance("AppMK") }

    //状态栏
    fun getAppStatusBar():Boolean{
        return appKV.getBoolean(AppConfig.READ_STATUS_BAR,false)
    }

    fun setReadStatusBar(b:Boolean){
        appKV.put(AppConfig.READ_STATUS_BAR,b)
    }

    //夜间模式
    fun getAppNight():Boolean{
        return appKV.getBoolean(AppConfig.CACHE_SYSTEM_NIGHT,false)
    }

    //获取当前主题
    fun getAppTheme():Int{
        //是否是夜间模式
        val isNight = appKV.getBoolean(AppConfig.CACHE_SYSTEM_NIGHT, false)
        return if(isNight){
            BgData.BG_night.ordinal
        }else{
            0;
        }
    }

    fun setAppNight(b:Boolean) {
        appKV.put(AppConfig.CACHE_SYSTEM_NIGHT, b)
    }
}
