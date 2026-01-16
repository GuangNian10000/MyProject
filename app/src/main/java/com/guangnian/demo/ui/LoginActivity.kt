package com.guangnian.demo.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.guangnian.demo.app.base.BaseVBActivity
import com.guangnian.demo.databinding.ActivityLoginBinding
import com.guangnian.demo.databinding.ActivityMainBinding
import com.guangnian.demo.livedata.StateConfig
import com.guangnian.demo.livedata.StateLiveData
import com.guangnian.demo.viewmodel.state.MainsViewModel
import com.guangnian.mvvm.ext.view.clickNoRepeat

class LoginActivity : BaseVBActivity<MainsViewModel, ActivityLoginBinding>() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initOnClick() {
        mVB.btnLogin.clickNoRepeat {

        }
    }

    override fun initData() {
    }
}