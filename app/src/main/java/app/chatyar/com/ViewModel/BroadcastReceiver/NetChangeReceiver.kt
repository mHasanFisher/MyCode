package app.chatyar.com.ViewModel.BroadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.chatyar.com.ViewModel.Interfaces.NetworkChangeListener

class NetChangeReceiver(): BroadcastReceiver() {

    private lateinit var networkChangeListener: NetworkChangeListener

    constructor( networkChangeListener: NetworkChangeListener) : this(){
        this.networkChangeListener = networkChangeListener

    }

    override fun onReceive(context: Context?, intent: Intent?) {

        try {


                    networkChangeListener.netChanged(context!!, true)


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}