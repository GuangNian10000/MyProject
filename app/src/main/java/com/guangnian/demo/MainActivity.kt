package com.guangnian.demo

import android.os.Bundle
import com.guangnian.demo.base.BaseVBActivity
import com.guangnian.demo.databinding.ActivityMainBinding
import com.guangnian.demo.viewmodel.state.MainsViewModel

class MainActivity : BaseVBActivity<MainsViewModel, ActivityMainBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_container, MviStressTestFragment()) // 替换容器内容
            .commit()
    }

    override fun initData() {

    }

}