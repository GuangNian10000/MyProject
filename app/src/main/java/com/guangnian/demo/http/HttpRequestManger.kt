package com.guangnian.demo.http


import android.os.Environment
import com.guangnian.mvvm.base.appContext
import com.guangnian.mvvm.network.AppException
import com.guangnian.demo.app.ext.makeToast
import com.guangnian.demo.data.model.bean.ImagePictureBean
import com.guangnian.demo.data.model.bean.LoginBean
import com.guangnian.demo.data.model.bean.UserBean
import com.guangnian.demo.livedata.StateConfig
import com.guangnian.demo.util.mk.UserMK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException
import java.util.UUID


/**
 * 作者　: GuangNian
 * 时间　: 2020/5/4
 * 描述　: 处理协程的请求类
 */

val HttpRequestCoroutine: HttpRequestManger by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    HttpRequestManger()
}

class HttpRequestManger {

    //登录
    suspend fun login(user:String,pwd:String): LoginBean {
        val bean = apiService.login(user,pwd)
        checkCode(bean.st)
        StateConfig.loginBean = bean
        return bean
    }

    //用户数据
    suspend fun getUserData(): UserBean {
        val bean = apiService.getUserData()
        checkCode(bean.st)
        UserMK.saveUserInfo(bean)
        return bean
    }

    //并发图片上传
    suspend fun uploadPicture(listPicture: List<ImagePictureBean>): List<ImagePictureBean> = withContext(Dispatchers.IO) {
        listPicture
            .filter { it.data.path != null }
            .map { pic ->
                async {
                    val file = File(pic.data.realPath)
                    val body = file.asRequestBody("image/*".toMediaType())
                    val part = MultipartBody.Part.createFormData("img", file.name, body)

                    val resp = apiService.uploadPicture(part)

                    if (resp.st != 200) {
                        throw AppException(resp.st, resp.msg)
                    }

                    pic.apply {
                        serviceUrl = resp.url
                    }
                }
            }.awaitAll()
    }

    //下载音频文件
    suspend fun downloadAudio(url: String): Result<File> = withContext(Dispatchers.IO) {
        // 1. 校验输入
        if (url.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Url不能为空"))
        }

        try {
            val response = apiService.downloadAudio(url)

            if (!response.isSuccessful) {
                return@withContext Result.failure(IOException("网络错误: ${response.code()} ${response.message()}"))
            }

            val body = response.body()
            if (body == null) {
                return@withContext Result.failure(IOException("响应体为空"))
            }

            // 生成文件名 (逻辑解耦)
            val extension = url.substringAfterLast(".", "mp3") // 默认兜底 mp3
            val fileName = generateUniqueFileName(extension)

            // 执行保存 (建议传入 body 和 文件名，内部进行流式写入)
            // 假设 saveAudioToExternalStorage 现在返回保存后的 File 对象
            val savedFile = saveAudioStreamToStorage(body, fileName)

            return@withContext Result.success(savedFile)

        } catch (e: Exception) {
            // 捕获并返回异常
            e.printStackTrace()
            return@withContext Result.failure(e)
        }
    }

    //========================================辅助方法=====================================================

    // 生成唯一文件名
    private fun generateUniqueFileName(suffix: String): String {
        // 尽量减少在 Repository 层直接使用 getString(R.string...)，这里仅作保留逻辑演示
        // 建议：使用常量或由外部传入 prefix
        val prefix = "Audio_${System.currentTimeMillis()}"
        return "${prefix}_${UUID.randomUUID()}.$suffix"
    }

    //流式保存文件 (防止 OOM)
    private fun saveAudioStreamToStorage(body: ResponseBody, fileName: String): File {
        // 获取保存目录 (根据你的实际需求修改 context 获取方式)
        val file = File(appContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName)

        body.byteStream().use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file
    }

    //通用校验方法
    private fun checkCode(st: Int) {
        if (st != 200) {
            makeToast("请求失败")
            throw AppException(st, st.toString())
        }
    }
}