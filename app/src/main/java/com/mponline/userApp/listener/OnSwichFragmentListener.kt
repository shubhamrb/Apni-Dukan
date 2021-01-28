package com.mponline.userApp.listener

interface OnSwichFragmentListener {

    //With drawer
    fun onSwitchFragment(tag:String, type:String, obj:Any?, extras:Any?)

    //Without drawer
    fun onSwitchFragmentParent(tag: String, type:String, obj:Any?, extras:Any?)
}