package com.mamits.apnaonlines.userv.listener

import com.mamits.apnaonlines.userv.model.LocationObj

interface OnLocationFetchListener {

    fun onLocationSuccess(locationObj: LocationObj)

    fun onLocationFailure()
}