package com.mamits.apnaonlines.user.listener

import com.mamits.apnaonlines.user.model.LocationObj

interface OnLocationFetchListener {

    fun onLocationSuccess(locationObj: LocationObj)

    fun onLocationFailure()
}