package com.mponline.userApp.listener

import android.view.View

interface OnItemClickListener {

    fun onClick(pos:Int, view: View, obj:Any?)
    fun onClick(pos:Int, view: View, obj:Any?, type:String){}
}