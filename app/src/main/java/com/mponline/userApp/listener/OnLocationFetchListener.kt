package com.mponline.userApp.listener

import com.mponline.userApp.model.LocationObj

interface OnLocationFetchListener {

    fun onLocationSuccess(locationObj: LocationObj)

    fun onLocationFailure()
}