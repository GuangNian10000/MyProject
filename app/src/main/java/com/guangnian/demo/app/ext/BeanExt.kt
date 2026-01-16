package com.guangnian.demo.app.ext

import java.lang.reflect.Modifier
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

/**
 * @author GuangNian
 * @description:
 * @date : 2023/11/17 17:02
 */
object BeanExt {
    fun Any.copyCommonTo(other: Any) {
        this::class.memberProperties.forEach { prop ->
            other::class.memberProperties
                .find { it.name == prop.name && it.returnType == prop.returnType }
                ?.let { destProp ->
                    destProp.isAccessible = true
                    destProp.call(other, prop.getter.call(this))
                }
        }
    }

    fun copyProperties(source: Any, destination: Any) {
        source::class.memberProperties.forEach { prop ->
            destination::class.memberProperties.find { it.name == prop.name }?.let { destProp ->
                destProp.isAccessible = true
                prop.isAccessible = true

                if (Modifier.isPrivate(destProp.javaField?.modifiers ?: 0)) {
                    destProp.javaField?.isAccessible = true
                }

                destProp.javaField?.set(destination, prop.javaField?.get(source))
            }
        }
    }

    //能使用 continue和break的 foreach
    fun <T> ArrayList<T>.forE(action: (T) -> Unit) {
        for (element in this) {
            action(element)
        }
    }

    fun <T> List<T>.forE(action: (T) -> Unit) {
        for (element in this) {
            action(element)
        }
    }
}