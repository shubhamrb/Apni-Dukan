package com.mamits.apnaonlines.user.model

object LocationUtils {

    var currentLocation: LocationObj? = null
    var selectedLocation: LocationObj? = null

    @JvmName("setCurrentLocation1")
    fun setCurrentLocation(locationObj: LocationObj?){
        currentLocation = locationObj
    }

    @JvmName("getCurrentLocation1")
    fun getCurrentLocation(): LocationObj?{
        return if(selectedLocation !=null) selectedLocation else currentLocation
    }
    @JvmName("setSelectedLocation1")
    fun setSelectedLocation(locationObj: LocationObj?){
        selectedLocation = locationObj
    }

    @JvmName("getSelectedLocation1")
    fun getSelectedLocation(): LocationObj?{
        return selectedLocation
    }
}