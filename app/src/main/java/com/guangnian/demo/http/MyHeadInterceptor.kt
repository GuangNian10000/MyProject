package com.guangnian.demo.http

import com.guangnian.demo.data.config.SysConfig.getPackageName
import com.guangnian.demo.data.config.SysConfig.getVersionCode
import com.guangnian.demo.livedata.StateConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 自定义头部参数拦截器，传入heads
 */
class MyHeadInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        builder.addHeader("token", StateConfig.loginBean.token).build()
        builder.addHeader("device", "android").build()
        builder.addHeader("version", getVersionCode().toString()).build()
        builder.addHeader("pageName", getPackageName()).build()

        return chain.proceed(builder.build())
    }
}