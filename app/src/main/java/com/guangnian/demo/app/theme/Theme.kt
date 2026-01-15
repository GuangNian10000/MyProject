package com.guangnian.demo.app.theme

import androidx.lifecycle.MutableLiveData
import com.guangnian.demo.data.model.bean.BgData

data class Theme(
    val isNight: Boolean
)

object Themes {
    val Default = Theme(false)
    val Day = Theme(false)
    val Night = Theme(true)
}

object AppTheme {
    val appTheme : MutableLiveData<BgData> = MutableLiveData()
    fun initialize() {

    }
}

object ThemeAdapter {
}