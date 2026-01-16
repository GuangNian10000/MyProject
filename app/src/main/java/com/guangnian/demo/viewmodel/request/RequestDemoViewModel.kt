package com.guangnian.demo.viewmodel.request


import androidx.lifecycle.MutableLiveData
import com.guangnian.demo.data.model.bean.ImagePictureBean
import com.guangnian.demo.data.model.bean.LoginBean
import com.guangnian.demo.data.model.bean.UserBean
import com.guangnian.demo.http.HttpRequestCoroutine
import com.guangnian.mvvm.base.viewmodel.BaseViewModel
import com.guangnian.mvvm.ext.requestNoCheck
import java.io.File


class RequestDemoViewModel : BaseViewModel() {

    var loginResult = MutableLiveData<LoginBean>()
    var userDataResult = MutableLiveData<UserBean>()
    var uploadPictureResult = MutableLiveData<List<ImagePictureBean>>()
    var downloadAudioResult = MutableLiveData<Result<File>>()

    fun login(user:String,pwd:String){
        requestNoCheck({ HttpRequestCoroutine.login(user,pwd)},{ data->
            loginResult.value = data
        })
    }

    fun getUserData(){
        requestNoCheck({ HttpRequestCoroutine.getUserData()},{ data->
            userDataResult.value = data
        })
    }

    fun uploadPicture(listPicture: List<ImagePictureBean>){
        requestNoCheck({ HttpRequestCoroutine.uploadPicture(listPicture)},{ data->
            uploadPictureResult.value = data
        })
    }

    fun downloadAudio(url: String){
        requestNoCheck({ HttpRequestCoroutine.downloadAudio(url)},{ data->
            downloadAudioResult.value = data
        })
    }
}