package app.chatyar.com.ViewModel.Interfaces

import android.content.Context
import app.chatyar.com.Model.Models.MessageFromSocketModel

interface SocketReceiveMessage {

    fun onReceiveMessage(context: Context, message: MessageFromSocketModel?)

    fun onOpenSocket(connectedTo: String)

    fun onCloseSocket(disconnectedFrom: String, code: Int, reason: String, remote: Boolean)

    fun onErrorSocket(ex: Exception)

}