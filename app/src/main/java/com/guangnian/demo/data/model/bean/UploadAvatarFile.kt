package com.guangnian.demo.data.model.bean

/**
 * @author GuangNian
 * @description:
 * @date : 2021/5/27 6:10 下午
 */

data class UploadAvatarFile(var st: Int = 0,
                            var msg: String= "",
                            var res: String= "",
                            var result:String="",
                            var path:String= "",
                            var url: String= ""){
    fun getTextMsg():String{
        return if(""!=result){
            result
        }else{
            res
        }
    }
}