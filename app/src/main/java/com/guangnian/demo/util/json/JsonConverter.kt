package com.guangnian.demo.util.json

import com.drake.net.convert.JSONConvert
import com.hjq.gson.factory.GsonFactory
import java.lang.reflect.Type

/**
 * @author GuangNian
 * @description:
 * @date : 2024/12/5 15:01
 */
class JsonConverter : JSONConvert(code = "errorCode", message = "errorMsg", success = "0") {

    override fun <R> String.parseBody(succeed: Type): R? {
        return GsonFactory.getSingletonGson().fromJson(this,succeed)
    }
}