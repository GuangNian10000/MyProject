package com.guangnian.demo.util

import com.guangnian.demo.data.config.AppConfig
import com.guangnian.demo.data.model.bean.BgData
import com.tencent.mmkv.MMKV

/**
 * @author GuangNian
 * @description: 本地数据存储
 * @date : 2023/11/17 16:13
 */
object AppMK {

    private val appKV by lazy { MMKV.mmkvWithID("appKV") }

    //状态栏
    fun getAppStatusBar():Boolean{
        return getBoolean(AppConfig.READ_STATUS_BAR,false)
    }

    fun setReadStatusBar(b:Boolean){
        setBoolean(AppConfig.READ_STATUS_BAR,b)
    }

    //夜间模式
    fun getAppNight():Boolean{
        return getBoolean(AppConfig.CACHE_SYSTEM_NIGHT,false)
    }

    //获取当前主题
    fun getAppTheme():Int{
        //是否是夜间模式
        val isNight =getBoolean(AppConfig.CACHE_SYSTEM_NIGHT, false)
        return if(isNight){
            BgData.BG_night.ordinal
        }else{
            0;
        }
    }

    fun setAppNight(b:Boolean) {
        setBoolean(AppConfig.CACHE_SYSTEM_NIGHT, b)
    }

    private fun setBoolean(key: String, value: Boolean) {
        appKV.encode(key, value)
    }

    private fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return appKV.decodeBool(key, defaultValue)
    }

    private fun getString(key: String, defaultValue: String = ""): String {
        return appKV.decodeString(key, defaultValue) ?: defaultValue
    }

    private fun setString(key: String, value: String) {
        appKV.encode(key, value)
    }

    private fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return appKV.decodeFloat(key, defaultValue)
    }

    private fun setFloat(key: String, value: Float) {
        appKV.encode(key, value)
    }

    private fun getInt(key: String, defaultValue: Int = 0): Int {
        return appKV.decodeInt(key, defaultValue)
    }

    private fun setInt(key: String, value: Int) {
        appKV.encode(key, value)
    }

    private fun getLong(key: String, defaultValue: Long = 0): Long {
        return appKV.decodeLong(key, defaultValue)
    }

    private fun setLong(key: String, value: Long) {
        appKV.encode(key, value)
    }
}
