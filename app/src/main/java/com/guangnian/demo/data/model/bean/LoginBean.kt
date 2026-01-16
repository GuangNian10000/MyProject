package com.guangnian.demo.data.model.bean

/**
 * @author GuangNian
 * @description:
 * @date : 2023/11/16 16:12
 */
data class LoginBean(
    val st: Int=0,
    val msg: String="",
    val result: String="",
    var token: String="",
    val uid: String="",
)