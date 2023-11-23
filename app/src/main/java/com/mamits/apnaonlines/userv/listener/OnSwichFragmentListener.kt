package com.mamits.apnaonlines.userv.listener

interface OnSwichFragmentListener {

    //With drawer
    fun onSwitchFragment(tag: String, type: String, obj: Any?, extras: Any?)
    fun onSwitchFragmentFromDrawer(tag: String, type: String, obj: Any?, extras: Any?) {

    }

    //Without drawer
    fun onSwitchFragmentParent(tag: String, type: String, obj: Any?, extras: Any?)

    //To manage toolbar
    fun onSwichToolbar(tag: String, type: String, obj: Any?) {}

    fun onStartNewActivity(listener: OnImgPreviewListener, imgPath: String,docName:String) {}
    fun onStartLocationAccess(listener: OnLocationFetchListener) {}
}