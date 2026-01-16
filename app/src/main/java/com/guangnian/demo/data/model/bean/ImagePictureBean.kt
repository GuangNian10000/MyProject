package com.guangnian.demo.data.model.bean

import com.luck.picture.lib.entity.LocalMedia

data class ImagePictureBean (
    val data: LocalMedia = LocalMedia(),
    val style: String="",
    var serviceUrl:String="")
