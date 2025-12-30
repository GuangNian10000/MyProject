package com.guangnian.mvvm.callback.unlive.keyvalue.utils

import android.annotation.SuppressLint
import android.app.Application
import java.lang.reflect.InvocationTargetException

/**
 * Converted to Kotlin
 */
object AppUtils {

    @SuppressLint("StaticFieldLeak")
    private var sApplication: Application? = null

    val app: Application
        get() {
            if (sApplication != null) {
                return sApplication!!
            }
            sApplication = getApplicationByReflect()
            return sApplication ?: throw NullPointerException("u should init first")
        }

    private fun getApplicationByReflect(): Application {
        try {
            @SuppressLint("PrivateApi")
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val thread = activityThreadClass.getMethod("currentActivityThread").invoke(null)
            val app = activityThreadClass.getMethod("getApplication").invoke(thread)
            return app as? Application ?: throw NullPointerException("u should init first")
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        throw NullPointerException("u should init first")
    }
}