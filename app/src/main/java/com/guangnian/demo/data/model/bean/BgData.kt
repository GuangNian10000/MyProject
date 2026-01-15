package com.guangnian.demo.data.model.bean

import androidx.annotation.ColorRes
import com.guangnian.demo.R

/**
 * @author 光年
 * @since 2026/1/15
 * @summary
 */
enum class BgData(
    @ColorRes
    val bgColor:Int=0,
    @ColorRes
    val fontColor:Int=0){
    BG_1(R.color.read_bg_1, R.color.read_fn_1),
    BG_2(R.color.read_bg_2, R.color.read_fn_2),
    BG_3(R.color.read_bg_3, R.color.read_fn_3),
    BG_4(R.color.read_bg_4, R.color.read_fn_4),
    BG_night(R.color.read_bg_night, R.color.read_fn_night)
}