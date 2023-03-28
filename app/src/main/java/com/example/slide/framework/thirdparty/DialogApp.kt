package com.example.slide.framework.thirdparty

import java.io.Serializable

class DialogApp(val id: Int,val type: Int,val name: String,val content: String, val vPackage: String, val icon: String,
                val banner: String,val percent: Int) : Serializable{

}