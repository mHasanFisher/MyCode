package app.myCode.ir.ViewModel.Services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import app.myCode.ir.ViewModel.Classes.SharedPrefsTags
import app.myCode.ir.ViewModel.Interfaces.SocketReceiveMessage
import app.myCode.ir.ViewModel.ViewModels.MainDatasetViewModel
import java.util.*

class SocketService : Service() {

    lateinit var mainDatasetViewModel: MainDatasetViewModel

    val httpHeaders = HashMap<String, String>()
    val handler: Handler = Handler()
    private lateinit var socketReceiveMessage: SocketReceiveMessage

    override fun onBind(intent: Intent?): IBinder? {

        return null

    }

    override fun onCreate() {
        super.onCreate()


//        httpHeaders.put(
//            "authorization",
//            MyApp.instance.sharedPreferences.getString(SharedPrefsTags.MY_SESSION, SharedPrefsTags.MY_SESSION_DEFAULT)!!
//        )

//        MyApp.instance.webSocketClient = object : WebSocketClient(URI("ws://185.126.201.100:1337/websocket")) {
//
//            override fun onMessage(message: String) {
//
//                MyApp.instance.connectionToSocketStatus = SharedPrefsTags.STATUS_UPDATED
//
//                val messageReceived: MessageFromSocketModel =
//                    Gson().fromJson(message, MessageFromSocketModel::class.java)
//
//                if(messageReceived.getType() == "message") {
//                    MyApp.instance.mainPageDataset.addAll(messageReceived.getData()?.message!!)
//                }
//
//                socketReceiveMessage = MyApp.instance.socketReceiveMessage
//                socketReceiveMessage.onReceiveMessage(applicationContext, messageReceived)
//
//
//
//            }
//
//            override fun onOpen(handshake: ServerHandshake) {
//
//
//                MyApp.instance.connectionToSocketStatus = SharedPrefsTags.STATUS_UPDATING
//
//                socketReceiveMessage = MyApp.instance.socketReceiveMessage
//                socketReceiveMessage.onOpenSocket(getURI().toString())
//
//                SharedPrefsTags.getAllMessages(this@SocketService)
//
//            }
//
//            override fun onClose(code: Int, reason: String, remote: Boolean) {
//
//                if(!MyApp.instance.webSocketClient.isOpen) {
//                    SharedPrefsTags.connectToSock(this@SocketService)
//                }
//
//                socketReceiveMessage = MyApp.instance.socketReceiveMessage
//                socketReceiveMessage.onCloseSocket(getURI().toString(), code, reason, remote)
//
//                handler.postDelayed({
//
//                    if(!MyApp.instance.webSocketClient.isOpen) {
//                        if (SharedPrefsTags.connectToSock(this@SocketService))
//                            handler.postDelayed(this, 2000)
//                    }
//
//                }, 2000)
//            }
//
//            override fun onError(ex: Exception) {
//
//                if(!MyApp.instance.webSocketClient.isOpen) {
//                    SharedPrefsTags.connectToSock(this@SocketService)
//                }
//
//                socketReceiveMessage = MyApp.instance.socketReceiveMessage
//                socketReceiveMessage.onErrorSocket(ex)
//
//                handler.postDelayed({
//
//                    if(!MyApp.instance.webSocketClient.isOpen) {
//                        if (SharedPrefsTags.connectToSock(this@SocketService))
//                            handler.postDelayed(this, 2000)
//                    }
//
//                }, 2000)
//
//            }
//        }

//        SharedPrefsTags.connectToSock(this)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val bundle = intent?.extras

        if (bundle != null) {


            if (bundle.getString(SharedPrefsTags.COME_FROM_WHERE, "non") == SharedPrefsTags.COME_FROM_ALL_CHATS) {

//                val toSock = MessageToSocketModel()
//                val sockData = MessageToSocketModel().MessageToSocketData()
//                sockData.messageToSocketData(7, 100, 0, 0, 0)
//                toSock.messageToSocketModel(
//                    MyApp.instance.sharedPreferences.getString(
//                        SharedPrefsTags.MY_SESSION,
//                        SharedPrefsTags.MY_SESSION_DEFAULT
//                    ), "getmessages", sockData
//                )
//
//                val toSend = Gson().toJson(toSock)
//
//                if (MyApp.instance.webSocketClient!!.isOpen) {
//
//                    MyApp.instance.webSocketClient!!.send(toSend)
//                } else {
//
//                    SharedPrefsTags.connectToSock(this)

//                }
            } else if (bundle.getString(SharedPrefsTags.COME_FROM_WHERE, "non") == SharedPrefsTags.COME_FROM_CHAT) {


                if (bundle.getString(SharedPrefsTags.CHAT_ORDER, "non") == SharedPrefsTags.CHAT_GET_MESSAGES) {

//                    val chatId = bundle.getInt(SharedPrefsTags.CHAT_ID, 0)
//                    val toSock = MessageToSocketModel()
//                    val sockData = MessageToSocketModel().MessageToSocketData()
//
//                    sockData.messageToSocketData(7, 100, 0, chatId, 0)
//                    toSock.messageToSocketModel(
//                        MyApp.instance.sharedPreferences.getString(
//                            SharedPrefsTags.MY_SESSION,
//                            SharedPrefsTags.MY_SESSION_DEFAULT
//                        ),
//                        "getmessages", sockData
//                    )
//
//                    val toSend = Gson().toJson(toSock)
//
//                    if (MyApp.instance.webSocketClient!!.isOpen) {
//
//                        MyApp.instance.webSocketClient!!.send(toSend)
//                    } else {
//                        SharedPrefsTags.connectToSock(this)
//                    }

                } else {

//                    val messageToken = bundle.getString(SharedPrefsTags.MESSAGE_TOKEN, "non")
//                    val chatId = bundle.getInt(SharedPrefsTags.CHAT_ID, 0)
//                    val messageData = SendMessageToSocketModel().SendMessageData().Message()
//                    messageData.Message(
//                        chatId, "", messageToken, "text", bundle.getString(SharedPrefsTags.CHAT_MESSAGE, null), 7
//                    )
//
//
//                    val dataset: MutableList<SendMessageToSocketModel.SendMessageData.Message> = ArrayList()
//                    dataset.add(messageData)
//
//                    val sockData = SendMessageToSocketModel().SendMessageData()
//                    sockData.SendMessageData(dataset)
//
//                    val toSock = SendMessageToSocketModel()
//                    toSock.SendMessageToSocketModel(
//                        MyApp.instance.sharedPreferences.getString(
//                            SharedPrefsTags.MY_SESSION,
//                            SharedPrefsTags.MY_SESSION_DEFAULT
//                        ), "sendmessages", sockData
//                    )
//
//                    val toSend = Gson().toJson(toSock)
//
//                    if (MyApp.instance.webSocketClient!!.isOpen) {
//
//                        MyApp.instance.webSocketClient!!.send(toSend)
//                    } else {
//                        SharedPrefsTags.connectToSock(this)
//                    }

                }
            } else if (bundle.getString(SharedPrefsTags.STOP_SERVICE, "non") == "stop") {


//                MyApp.instance.webSocketClient!!.close()
//                stopSelf()


            }

//            else {
//                webSocketClient.connect()
//
//            }
        }

        return super.onStartCommand(intent, flags, startId)


    }




}