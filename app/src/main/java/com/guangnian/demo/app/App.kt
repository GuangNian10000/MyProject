package com.guangnian.demo.app

import android.app.Activity
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.guangnian.demo.app.manager.ActivityManager
import com.drake.brv.utils.BRV
import com.drake.net.NetConfig
import com.drake.net.interceptor.RequestInterceptor
import com.drake.net.okhttp.setConverter
import com.drake.net.okhttp.setDebug
import com.drake.net.okhttp.setRequestInterceptor
import com.drake.net.request.BaseRequest
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonToken
import com.guangnian.demo.BR
import com.guangnian.demo.app.ext.makeToast
import com.guangnian.demo.app.theme.AppTheme
import com.guangnian.demo.data.config.SysConfig
import com.guangnian.demo.data.config.SysConfig.isDebug
import com.guangnian.demo.data.config.DebugLoggerTree
import com.guangnian.demo.util.json.JsonConverter
import com.guangnian.mvvm.base.BaseApp
import com.hjq.gson.factory.GsonFactory
import com.hjq.gson.factory.ParseExceptionCallback
import com.hjq.toast.Toaster
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.mmkv.MMKV
import timber.log.Timber
import java.util.concurrent.TimeUnit


/**
 * 作者　: guangnian
 * 时间　: 2019/12/23
 * 描述　:
 */
class App : BaseApp() {

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        initUISdk()
    }

    //初始化UiSDK
    private fun initUISdk(){

        BRV.modelId = BR.m

        //主题初始化
        AppTheme.initialize()

        // Activity 栈管理初始化
        ActivityManager.getInstance().init(instance)

        // Mkk 初始化
        MMKV.initialize(instance)

        NetConfig.initialize("", this) {
            // 超时配置, 默认是10秒, 设置太长时间会导致用户等待过久
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            setConverter(JsonConverter())
            setDebug(true)
            setRequestInterceptor(object : RequestInterceptor {
                override fun interceptor(request: BaseRequest) {
//                    getUserData().let {
//                        request.setHeader("lang", getLocLang())
//                        request.setHeader("token", it.token)
//                    }
                }
            })
        }

        // 初始化日志打印
        if (SysConfig.isLogEnable()) {
            Timber.plant(DebugLoggerTree())
        }

        // 初始化 Toast 框架
        Toaster.init(this)

        //初始化刷新头
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout -> MaterialHeader(this) }
        //关闭上拉加载动画
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            layout.setFooterHeight(0F)
            val classicsFooter = ClassicsFooter(context)
            classicsFooter.visibility = View.GONE
            classicsFooter
        }

        // 设置 Json 解析容错监听
        GsonFactory.setParseExceptionCallback(object : ParseExceptionCallback {
            override fun onParseObjectException(
                typeToken: TypeToken<*>,
                fieldName: String,
                jsonToken: JsonToken
            ) {
                handlerGsonParseException("解析对象析异常：$typeToken#$fieldName，后台返回的类型为：$jsonToken")
            }

            override fun onParseListItemException(
                typeToken: TypeToken<*>?,
                fieldName: String?,
                listItemJsonToken: JsonToken?
            ) {
                handlerGsonParseException("解析 List 异常：$typeToken#$fieldName，后台返回的条目类型为：$listItemJsonToken")
            }

            override fun onParseMapItemException(
                typeToken: TypeToken<*>?,
                fieldName: String?,
                mapItemKey: String?,
                mapItemJsonToken: JsonToken?
            ) {
                handlerGsonParseException("解析 Map 异常：$typeToken#$fieldName，mapItemKey = $mapItemKey，后台返回的条目类型为：$mapItemJsonToken")
            }

            private fun handlerGsonParseException(message: String) {
                require(!isDebug()) { message }
                //自定义上传
                //CrashReport.postCatchedException(IllegalArgumentException(message))
            }
        })

        // 注册网络状态变化监听
        val connectivityManager: ConnectivityManager? =
            ContextCompat.getSystemService(instance, ConnectivityManager::class.java)
        if (connectivityManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onLost(network: Network) {
                    val topActivity: Activity? = ActivityManager.getInstance().getTopActivity()
                    if (topActivity !is LifecycleOwner) {
                        return
                    }
                    val lifecycleOwner: LifecycleOwner = topActivity
                    if (lifecycleOwner.lifecycle.currentState != Lifecycle.State.RESUMED) {
                        return
                    }
                    makeToast("当前网络不可用，请检查网络")
                }
            })
        }
    }
}
