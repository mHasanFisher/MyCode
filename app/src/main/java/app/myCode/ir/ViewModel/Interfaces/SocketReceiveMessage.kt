package app.myCode.ir.ViewModel.Interfaces

import android.content.Context
import app.myCode.ir.Model.Models.MessageFromSocketModel

interface SocketReceiveMessage {

    fun onReceiveMessage(context: Context, message: MessageFromSocketModel?)

    fun onOpenSocket(connectedTo: String)

    fun onCloseSocket(disconnectedFrom: String, code: Int, reason: String, remote: Boolean)

    fun onErrorSocket(ex: Exception)

}