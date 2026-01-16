package com.guangnian.demo.app.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.guangnian.demo.R
import com.guangnian.mvvm.base.appContext
import com.hjq.toast.Toaster
import com.hjq.window.EasyWindow
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * @author GuangNian
 * @description:
 * @date : 2023/11/20 15:50
 */

//获取设备 id
fun getDeviceId(): String {
    return Settings.Secure.getString(appContext.contentResolver, Settings.Secure.ANDROID_ID)
}

fun <T> subSafeList(inputList: List<T>, startIndex: Int, endIndex: Int): List<T> {
    if(inputList.isEmpty()){
        return emptyList()
    }

    val start = if (startIndex < 0) 0 else startIndex.coerceAtMost(inputList.size)
    val end = if (endIndex > inputList.size) inputList.size else endIndex.coerceAtLeast(0)

    if (start <= end) {
        return inputList.subList(start, end)
    } else {
        // 返回空列表或者抛出异常，取决于你的需求
        return emptyList()
        // 或者抛出异常
        // throw IndexOutOfBoundsException("Invalid indices for sublist")
    }
}

fun Context.isMainlandLanguage(): Boolean {
    val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales[0]
    } else {
        @Suppress("DEPRECATION")
        resources.configuration.locale
    }
    return locale.language == "zh" && locale.country == "CN"
}

fun Context.getLanguage():String{
    val locale: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        resources.configuration.locales[0]
    } else {
        @Suppress("DEPRECATION")
        resources.configuration.locale
    }
    return locale.language
}

//时间格式
fun timeFormat(time:Float):String{
    val seconds = time.toInt()
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

fun Activity.showPermissionsEasyWindow(str:String){
    EasyWindow.with(this)
        .setGravity(Gravity.TOP)
        .setContentView(R.layout.taost_hint)
        .setText(R.id.tvText, str)
        .show()
}

//删除指定目录
suspend fun deleteDirectory(directoryPath: String) {
    return withContext(Dispatchers.IO) {
        try {
            val directoryToDelete = File(appContext.filesDir,directoryPath)
            deleteDirectory(directoryToDelete)
        }catch (e:Exception){
            makeDebugToast("Deletion failure${e.toJson()}")
        }
    }
}

private fun deleteDirectory(directory: File) {
    if (directory.exists()) {
        val files = directory.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isDirectory) {
                    deleteDirectory(file)
                } else {
                    file.delete()
                }
            }
        }
    }
    directory.delete()
}

//获取指定目录大小
fun getDirectorySize(directoryPath: String): String {
    val directory = File(appContext.filesDir,directoryPath)
    val sizeBytes = directory.walkTopDown().sumOf { it.length() }

    return formatSize(sizeBytes)
}

fun getDirectorySize(directoryPath: File): String {
    val sizeBytes = directoryPath.walkTopDown().sumOf { it.length() }

    return formatSize(sizeBytes)
}

private fun formatSize(sizeInBytes: Long): String {
    val kilobyte = 1024
    val megabyte = kilobyte * 1024

    return when {
        sizeInBytes < kilobyte -> "$sizeInBytes B"
        sizeInBytes < megabyte -> String.format("%.2f KB", sizeInBytes.toFloat() / kilobyte)
        else -> String.format("%.2f MB", sizeInBytes.toFloat() / megabyte)
    }
}

//选择系统相册
fun Context.selectSystemAlbum(back: (ArrayList<LocalMedia?>) -> Unit){
    PictureSelector.create(this)
        .openSystemGallery(SelectMimeType.ofImage())
        .forSystemResultActivity(object : OnResultCallbackListener<LocalMedia?> {
            override fun onResult(result: ArrayList<LocalMedia?>) {
                back.invoke(result)
            }
            override fun onCancel() {}
        })
}

fun uriToFile(context: Context, uri: Uri): File? {
    // 获取文件名
    val fileName = getFileName(context, uri)
    // 创建临时文件
    val tempFile = File(context.cacheDir, fileName ?: "tempFile")

    // 打开输入流和输出流
    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(tempFile)

        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

    return tempFile
}

//文件管理
fun AppCompatActivity.autoFileManagement(back: (Uri?) -> Unit){
    val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // 获取返回的Uri
            val uri: Uri? = result.data?.data
            uri?.let {
                back.invoke(it)
                //requestCloneListViewModel.timbreUpload(it)
            } ?: makeToast( "未选择文件")
        }
    }
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        type = "*/*" // 设置文件类型，例如 "image/*" 表示选择图片
        addCategory(Intent.CATEGORY_OPENABLE)
    }
    filePickerLauncher.launch(intent)
}

// 获取文件名的辅助方法
fun getFileName(context: Context, uri: Uri): String? {
    var name: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            name = it.getString(it.getColumnIndexOrThrow("_display_name"))
        }
    }
    return name
}
//异步实现
fun performAsyncOperation(action: suspend CoroutineScope.() -> Unit) {
    val ioCoroutineScope = CoroutineScope(Dispatchers.IO)
    ioCoroutineScope.launch {
        try {
            // 执行传入的异步操作
            action()
        } catch (e: Throwable) {
            // 捕获并处理异常
            makeLogE(e.toJson())
        } finally {
            // 取消 CoroutineScope
            ioCoroutineScope.cancel()
        }
    }
}

//字符串添加点击事件
fun SpannableString.setTextClick(searchText:String,onClick:()->Unit){
    val firstStartIndex = indexOf(searchText)
    val firstEndIndex = firstStartIndex + searchText.length

    if (firstStartIndex == -1) {
        println("未找到匹配的子串")
        return
    }
    if (isNotEmpty() && firstStartIndex>=0 && length >= firstEndIndex) {
        setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {
                // 点击事件
                onClick.invoke()
            }
        }, firstStartIndex, firstEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        setSpan(ForegroundColorSpan(getColor("#333333")), firstStartIndex, firstEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}

// 用于将字节转换为十六进制字符串的辅助方法
private fun bytesToHex(bytes: ByteArray): String {
    val sb = java.lang.StringBuilder()
    for (b in bytes) {
        sb.append(String.format("%02x", b))
    }
    return sb.toString()
}

//获取当前 SHA1
fun Context.getSHA1(){
    try {
        val info: PackageInfo = getPackageManager().getPackageInfo(
            getPackageName(),
            PackageManager.GET_SIGNATURES
        )
        info.signatures?.apply {
            for (signature in this) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val digest = md.digest()
                val hexString = StringBuilder()
                for (b in digest) {
                    val hex = Integer.toHexString(0xFF and b.toInt())
                    if (hex.length == 1) {
                        hexString.append('0')
                    }
                    hexString.append(hex)
                }

                md.update(signature.toByteArray())
                val signatureString: String = bytesToHex(md.digest())
                Log.d("getSignature", "Signature: $signatureString")
                Log.d("getSHA1", hexString.toString())
            }
        }

    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
}
//获取当前密钥散列
fun Context.getKeyHashing(){
    try {
        val info = packageManager.getPackageInfo(
            packageName,
            PackageManager.GET_SIGNATURES
        )
//        for (signature in info.signatures) {
//            val md = MessageDigest.getInstance("SHA")
//            md.update(signature.toByteArray())
//            val sha1 = Base64.encodeToString(md.digest(), Base64.DEFAULT)
//            Log.d("MyKeyHash:", sha1)
//        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
}

//获取当前屏幕亮度进度
fun Window.getBrightness(): Int {
    val params = attributes
    val brightness = params.screenBrightness
    return (brightness * 255).toInt()
}

//设置当前屏幕亮度进度
fun Window.setBrightness(progress: Int) {
    val layoutParams = attributes
    layoutParams.screenBrightness = progress / 255f
    attributes = layoutParams
}

//延时任务
fun taskDeferred(time:Float,back:()->Unit){
    val handler = Handler(Looper.getMainLooper())
    handler.postDelayed({
        back.invoke()
    }, (time*1000).toLong()) // 1000毫秒（1秒）后执行
}

//集合安全的截取
fun <T> getSafeSubList(inputList: List<T>, startIndex: Int, endIndex: Int): List<T> {
    val start = if (startIndex < 0) 0 else startIndex.coerceAtMost(inputList.size)
    val end = if (endIndex > inputList.size) inputList.size else endIndex.coerceAtLeast(0)

    if (start <= end) {
        return inputList.subList(start, end)
    } else {
        // 返回空列表或者抛出异常，取决于你的需求
        return emptyList()
        // 或者抛出异常
        // throw IndexOutOfBoundsException("Invalid indices for sublist")
    }
}

fun getSafeSubstring(inputString: String, startIndex: Int, endIndex: Int): String {
    val start = if (startIndex < 0) 0 else startIndex.coerceAtMost(inputString.length)
    val end = if (endIndex > inputString.length) inputString.length else endIndex.coerceAtLeast(0)

    return if (start <= end) {
        inputString.substring(start, end)
    } else {
        // 返回空字符串或者抛出异常，取决于你的需求
        return ""
        // 或者抛出异常
        // throw IndexOutOfBoundsException("Invalid indices for substring")
    }
}

//获取当前时间戳秒11位
fun getCurrentTime():Long {
    val s= System.currentTimeMillis()/1000
    return s
}

//获取当前时间戳毫秒
fun getCurrentTimeMs():Long {
    val s= System.currentTimeMillis()
    return s
}

fun timestampToEnglishDate(timestamp: Long): String {
    val date = Date(timestamp * 1000) // 将 11 位时间戳转换为毫秒级别
    val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH)
    return formatter.format(date)
}

fun timestampToHoursDate(timestamp: Long): String {
    val date = Date(timestamp* 1000)
    val format = SimpleDateFormat("yyyy年M月d日H时m分s秒", Locale.getDefault())
    return format.format(date)
}


fun millisecondConversion(millisUntilFinished: Long):String {
    val day = millisUntilFinished / (1000 * 24 * 60 * 60) //单位天
    val hour =
        (millisUntilFinished - day * (1000 * 24 * 60 * 60)) / (1000 * 60 * 60)
    //单位时
    val minute =
        (millisUntilFinished - day * (1000 * 24 * 60 * 60) - hour * (1000 * 60 * 60)) / (1000 * 60)
    //单位分
    val second =
        (millisUntilFinished - day * (1000 * 24 * 60 * 60) - hour * (1000 * 60 * 60) - minute * (1000 * 60)) / 1000
    //单位秒
    if(hour==0L){
        if(minute==0L){
            return "$second"
        }else{
            return "$minute:$second"
        }
    }else{
        return "$hour:$minute:$second"
    }
}

//去掉文本内的标签
@RequiresApi(Build.VERSION_CODES.N)
fun String.fromHtmlToText(): String {
    return Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()
}
fun String.getHtmlColor(color:String):String{
    return "<font color=\"$color\">"+this+ "<font/>"
}

//获取状态栏高度
fun Context.getStatusbarHeight(): Int {
    var statusBarHeight = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")

    if (resourceId > 0) {
        statusBarHeight = resources.getDimensionPixelSize(resourceId)
    }
    return statusBarHeight
}

fun setViewHeight(subView:View,newHeight:Int){
    val params = subView.layoutParams
    params.height = newHeight
    subView.layoutParams = params
}

fun removeParent(view: View?){
    try {
        if (view == null) {
            return
        }
        val parent = view.parent as ViewGroup
        parent.removeView(view)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun viewToBitmap(view: View): Bitmap {
    // 创建一个 Bitmap 对象，大小与视图一致
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

    // 创建一个 Canvas 对象，并将 Bitmap 与其关联
    val canvas = Canvas(bitmap)

    // 将视图内容绘制到 Canvas 上
    view.draw(canvas)

    // 返回绘制完成的 Bitmap 对象
    return bitmap
}

fun dpToPx(context: Context, dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
}

fun Float.toPx():Int{
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, appContext.resources.displayMetrics).toInt()
}

fun View.toTop(x:Float){
    setPadding(0,x.toPx(),0,0)
}

fun Context.getBitMap(res:Int,desiredWidth:Int,desiredHeight:Int):Bitmap{
    val bitmapBack = BitmapFactory.decodeResource(resources, res)
    return Bitmap.createScaledBitmap(bitmapBack, desiredWidth, desiredHeight, true)
}

fun Context.getDipF(res:Int):Float{
    return resources.getDimension(res)
}

fun Context.getDip(res:Int):Int{
    return resources.getDimension(res).toInt()
}

fun Context.spToPx(spValue:Float):Float{
    val displayMetrics = resources.displayMetrics
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, spValue, displayMetrics
    )
}

fun View.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.draw(canvas)
    return bitmap
}

fun View.toBitmap(width:Int,height:Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.draw(canvas)
    return bitmap
}

fun View.toVis(b:Boolean){
    visibility = if(b) View.VISIBLE else View.GONE
}

fun saveBitmapToFile(bitmap: Bitmap, filePath: String) {
    val file = File(filePath)
    try {
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun getStrBody(str:String) =
    RequestBody.create("text/plain".toMediaTypeOrNull(), str)

fun getImageBody(file: File) =
    MultipartBody.Part.createFormData("img", file.name, RequestBody.create("multipart/*".toMediaTypeOrNull(), file))

fun getMediaBody(file: File) =
    MultipartBody.Part.createFormData("media", file.name, RequestBody.create("multipart/*".toMediaTypeOrNull(), file))

fun getWavBody(file: File) =
    MultipartBody.Part.createFormData("wav", file.name, RequestBody.create("multipart/*".toMediaTypeOrNull(), file))

fun getUserImageBody(file: File) =
    MultipartBody.Part.createFormData("userimg", file.name, RequestBody.create("multipart/*".toMediaTypeOrNull(), file))

fun Context.showsKeyboard( view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT) }

@ColorInt
fun getColor(c:Int):Int= ContextCompat.getColor(appContext,c)

@ColorInt
fun getColor(c:String):Int= Color.parseColor(c)

fun getBackgroundExt(@DrawableRes c:Int): Drawable? = ContextCompat.getDrawable(appContext,c)

fun makeToast(text:String){
    Toaster.show(text)
}

fun makeToast(text:Int){
    Toaster.show(text)
}

fun makeToast(text:Object){
    Toaster.show(text)
}

fun makeDebugToast(text:String){
    Toaster.debugShow(text)
}

fun makeDebugToast(text:Int){
    Toaster.debugShow(text)
}

fun makeDebugToast(text:Object){
    Toaster.debugShow(text)
}

fun makeLogD(text:String?){
    Timber.tag("makeLog").d(text)
}

fun makeLogD(tag:String,text:String){
    Timber.tag(tag).d(text)
}

fun makeLogE(text:String){
    Timber.tag("makeLog").e(text)
}

fun makeLogE(text:String,tag:String){
    Timber.tag(tag).e(text)
}