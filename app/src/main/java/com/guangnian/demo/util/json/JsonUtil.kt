package com.guangnian.demo.util.json

import android.text.TextUtils
import com.alibaba.fastjson.JSON.parseObject
import com.google.gson.reflect.TypeToken
import com.guangnian.demo.app.ext.makeToast
import com.hjq.gson.factory.GsonFactory


/**
 * @author GuangNian
 * @description:  JSON 工具类
 * @date : 2022/6/2 2:49 下午
 */
object JsonUtil {

    /**
     * String 转 实体类
     * 1.GsonFactory 框架
     * 2.google 官方
     * 3.阿里json
     * */
    fun  <T> fromJson(str:String,type:Class<T>): T? {
        try {
            //return GsonFactory.getSingletonGson().fromJson(str,type)
            //return Gson().fromJson(str,type)
            return jsonToObjectForFastJson(str,type)
        }catch (e:Exception){
            makeToast("类型转换出现异常$e")
        }
        return null
    }

    /**
     * String 转 List
     * */
    fun <T> fromJsonArr(str: String, cls: Class<T>): ArrayList<T>? {
        if (TextUtils.isEmpty(str)) {
            return null // 或者根据业务逻辑处理空字符串的情况
        }

        val gson = GsonFactory.getSingletonGson()
            ?: return null // 或者进行适当的错误处理

        return try {
            val listType = TypeToken.getParameterized(ArrayList::class.java, cls).type
            gson.fromJson<ArrayList<T>>(str, listType)
        } catch (e: Exception) {
            e.printStackTrace()
            null // 或者进行适当的错误处理
        }
    }

    private fun <T> jsonToObjectForFastJson(jsonData: String?, clazz: Class<T>): T? {
        if (TextUtils.isEmpty(jsonData)) {
            return null
        }
        try {
            return parseObject(jsonData, clazz)
        } catch (e: java.lang.Exception) {
        }
        return null
    }
}