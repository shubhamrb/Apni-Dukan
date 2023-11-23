package com.mamits.apnaonlines.userv.livedata

import androidx.lifecycle.MutableLiveData
import com.zplesac.connectionbuddy.ConnectionBuddy
import com.zplesac.connectionbuddy.interfaces.ConnectivityChangeListener
import com.zplesac.connectionbuddy.models.ConnectivityEvent

class ConnectionLiveData : MutableLiveData<ConnectivityEvent>(), ConnectivityChangeListener {

    fun init(hasSavedInstanceState: Boolean): MutableLiveData<ConnectivityEvent> {
        if (!hasSavedInstanceState) {
            ConnectionBuddy.getInstance().configuration.networkEventsCache.
                    clearLastNetworkState(this)
        }
        return this
    }

    fun registerForNetworkUpdates() {
        ConnectionBuddy.getInstance().registerForConnectivityEvents(this, this)
    }

    fun unregisterFromNetworkUpdates() {
        ConnectionBuddy.getInstance().unregisterFromConnectivityEvents(this)
    }

    override fun onConnectionChange(event: ConnectivityEvent) {
        postValue(event)
    }
}
