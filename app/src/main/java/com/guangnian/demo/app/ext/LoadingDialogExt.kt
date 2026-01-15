package com.guangnian.demo.app.ext

import android.app.Activity
import android.content.Context
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.guangnian.demo.R
import java.lang.ref.WeakReference

/**
 * @author : guangnian
 * @date : 2020/6/28
 */
// 在 Activity 或 Fragment 类中声明 loadingDialog 弱引用
private var loadingDialog: WeakReference<MaterialDialog>? = null

private fun createMaterialDialog(context: Context, message: String): MaterialDialog {
    return MaterialDialog(context)
        .cancelable(true)
        .cancelOnTouchOutside(false)
        .cornerRadius(12f)
        .customView(R.layout.layout_custom_progress_dialog_view)
        .apply {
            getCustomView()
                .findViewById<TextView>(R.id.loading_tips)
                ?.text = message
        }
}


fun AppCompatActivity.showLoadingExt(message: String = "under loading") {
    if (!isFinishing) {
        val dialog = createMaterialDialog(this, message)
        loadingDialog = WeakReference(dialog) // 使用弱引用保存对话框引用
        dialog.show()
    }
}

fun Fragment.showLoadingExt(message: String = "under loading") {
    activity?.let { activity ->
        if (!activity.isFinishing) {
            val dialog = createMaterialDialog(activity, message)
            loadingDialog = WeakReference(dialog) // 使用弱引用保存对话框引用
            dialog.show()
        }
    }
}

// 其他函数保持不变

fun Activity.dismissLoadingExt() {
    loadingDialog?.get()?.dismiss() // 关闭对话框
    loadingDialog = null // 释放弱引用
}

fun Fragment.dismissLoadingExt() {
    loadingDialog?.get()?.dismiss() // 关闭对话框
    loadingDialog = null // 释放弱引用
}
