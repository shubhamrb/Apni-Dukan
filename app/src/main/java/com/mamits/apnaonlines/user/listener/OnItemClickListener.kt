package com.mamits.apnaonlines.user.listener

import android.view.View

interface OnItemClickListener {

    fun onClick(pos:Int, view: View, obj:Any?)
    fun onClick(pos:Int, view: View, obj:Any?, type:String){}
}