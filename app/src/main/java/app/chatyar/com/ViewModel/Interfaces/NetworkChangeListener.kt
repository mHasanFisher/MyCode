package app.chatyar.com.ViewModel.Interfaces

import android.content.Context

interface NetworkChangeListener {

    fun netChanged(context: Context, status: Boolean)
}