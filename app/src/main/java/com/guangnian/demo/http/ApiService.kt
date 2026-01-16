package com.guangnian.demo.http


import com.guangnian.demo.data.model.bean.LoginBean
import com.guangnian.demo.data.model.bean.UploadAvatarFile
import com.guangnian.demo.data.model.bean.UserBean
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response

import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Streaming
import retrofit2.http.Url


/**
 * 作者　: GuangNian
 * 时间　: 2019/12/23
 * 描述　: 网络APIff
 */
interface ApiService {

    /**
     * 登录
     */
    @FormUrlEncoded
    @POST("/login/")
    suspend fun login(@Field("account") phone: String,
                      @Field("password") password: String,): LoginBean

    /**
     * 获取个人资料
     */
    @GET("/me/user/")
    suspend fun getUserData(): UserBean

    /**
     * 意见反馈
     */
    @Multipart
    @POST("/feedback/upimg/")
    suspend fun uploadPicture(@Part file: MultipartBody.Part): UploadAvatarFile

    /**
     * 音频下载
     * */
    @Streaming
    @GET
    suspend fun downloadAudio(@Url fileUrl: String): Response<ResponseBody>
}