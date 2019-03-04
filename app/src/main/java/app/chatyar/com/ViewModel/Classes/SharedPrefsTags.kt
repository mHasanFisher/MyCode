package app.chatyar.com.ViewModel.Classes

import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import app.chatyar.com.Application.MyApp
import app.chatyar.com.R
import app.chatyar.com.View.Adapters.ChatRVAdapter
import app.chatyar.com.ViewModel.Interfaces.SocketReceiveMessage
import com.google.gson.Gson
import me.leolin.shortcutbadger.ShortcutBadger
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.io.File
import java.net.URI
import javax.net.ssl.SSLContext
import android.os.Build
import android.os.Environment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.NotificationCompat
import app.chatyar.com.Model.Models.*
import com.google.android.gms.security.ProviderInstaller
import com.koushikdutta.async.future.Future
import com.koushikdutta.ion.Ion
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import java.util.concurrent.TimeUnit


class SharedPrefsTags {

    companion object {
        const val SHARED_PREFS_NAME = "foodReadySharedPrefsName"
        const val FIREBASE_TOKEN = "firebaseToken"
        const val MY_SESSION = "session"
        const val MY_SESSION_DEFAULT = "0"

        const val BASE_URL = "https://api.chatyar.com/"
        //        const val BASE_URL = "http://185.126.201.100:1337/"
//        const val CURRENT_LATITUDE = "currentLatitude"
//        const val CURRENT_LONGITUDE = "currentLongitude"
        const val COME_FROM_WHERE = "comeFromWhere"
        const val COME_FROM_ALL_CHATS = "comeFromAllChats"
        const val COME_FROM_CHAT = "comeFromChat"
        const val CHAT_MESSAGE = "chatMessage"

        //=========== Operator datas
        const val OPERATOR_ID = "operatorId"
        const val OPERATOR_USERNAME = "operatorName"
        const val OPERATOR_FIRSTNAME = "operatorFirstName"
        const val OPERATOR_LASTNAME = "operatorLastName"
        const val OPERATOR_AVATAR = "operatorAvatar"

        const val STOP_SERVICE = "stopService"
        const val CHAT_ORDER = "chatOrder"
        const val CHAT_GET_MESSAGES = "chatGetMessages"
        const val CHAT_ID = "chatId"
        const val CHAT_NAME = "chatname"
            const val MESSAGE_TOKEN = "messageToken"

        const val STATUS_WAITING_FOR_NETWORK = 0
        const val STATUS_CONNECTING = 1
        const val STATUS_UPDATING = 2
        const val STATUS_UPDATED = 3

        const val NOTIFICATION_STATUS = "notificationStatus"
        const val SITE_ID = "siteId"
        const val SOLVED = "solved"
        const val ALL_SITES_TITLE = "allSitesTitle"

        const val AVATAR_URL_SMALL = "avatarUrlSmall"
        const val AVATAR_URL_MEDIUM = "avatarUrlMedium"
        const val AVATAR_URL_LARGE = "avatarUrlLarge"
        const val AVATAR_URL_XLARGE = "avatarUrlXLarge"
        const val LAST_SITE_ID = "lastSiteId"
        private var itemsToCancel: HashMap<String, Future<File>> = HashMap()



        fun isConnected(context: Context): Boolean {

            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnected == true

            return isConnected
        }

        fun notConnectedAlert(context: Context) {

            try {
                val builder = AlertDialog.Builder(context)
                val dialogCustomView: AlertDialog
                val inflaterAlert = (context as Activity).layoutInflater
                val viewAlert = inflaterAlert.inflate(R.layout.alert_not_connected_to_net, null)

                val okButt = viewAlert.findViewById<View>(R.id.okButton)

                builder.setView(viewAlert)
                dialogCustomView = builder.create()

                okButt.setOnClickListener {

                    dialogCustomView.dismiss()
                }

                dialogCustomView.show()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun notConnectedAlertWithText(context: Context, text: String) {

            try {
                val builder = AlertDialog.Builder(context)
                val dialogCustomView: AlertDialog
                val inflaterAlert = (context as Activity).layoutInflater
                val viewAlert = inflaterAlert.inflate(R.layout.alert_not_connected_to_net, null)

                val alertTitleTV = viewAlert.findViewById<TextView>(R.id.alertTitleTV)
                val okButt = viewAlert.findViewById<Button>(R.id.okButton)

                alertTitleTV.text = text

                builder.setView(viewAlert)
                dialogCustomView = builder.create()

                okButt.setOnClickListener {

                    dialogCustomView.dismiss()
                }

                dialogCustomView.show()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        fun connectToSock(context: Context): Boolean {

            if (SharedPrefsTags.isConnected(context)) {

                MyApp.instance.connectionToSocketStatus = SharedPrefsTags.STATUS_CONNECTING

                initializeSocket(context)

                return true

            } else {
                MyApp.instance.connectionToSocketStatus = SharedPrefsTags.STATUS_WAITING_FOR_NETWORK
                return false
            }
        }

        fun getOneChatMessages(context: Context, chatId: Int, siteId: Int, lastChatDate: Long) {


//            if(!SharedPrefsTags.isConnected(context)) {

            val toSock = MessageToSocketModel()
            val sockData = MessageToSocketModel().MessageToSocketData()

            sockData.messageToSocketData(siteId, 1000, lastChatDate, chatId, 0)
            toSock.messageToSocketModel(
                MyApp.instance.sharedPreferences.getString(
                    SharedPrefsTags.MY_SESSION,
                    SharedPrefsTags.MY_SESSION_DEFAULT
                ),
                "getmessages", sockData
            )

            val toSend = Gson().toJson(toSock)

            if (MyApp.instance.webSocketClient!!.isOpen) {

                MyApp.instance.webSocketClient!!.send(toSend)
            } else {
                connectToSock(context)
            }
//            }
        }

        fun getAllMessages(context: Context, isFirst: Boolean) {


            var beginDate: Long = 0


            if (isFirst) {
                if (MyApp.instance.mainPageDataset.size != 0) {

                    var siteId = 0
                    siteId = MyApp.instance.mainPageDataset.sortedByDescending { it.date }[0].accsiteId!!

                    if (siteId == MyApp.instance.whichSiteCurrentId) {
                        beginDate = MyApp.instance.mainPageDataset.sortedByDescending { it.date }[0].date!!
                    }
                }
            }

            val toSock = MessageToSocketModel()
            val sockData = MessageToSocketModel().MessageToSocketData()
            sockData.messageToSocketData(MyApp.instance.whichSiteCurrentId, 1000, 0, 0, beginDate)
            toSock.messageToSocketModel(
                MyApp.instance.sharedPreferences.getString(
                    SharedPrefsTags.MY_SESSION,
                    SharedPrefsTags.MY_SESSION_DEFAULT
                ), "getmessages", sockData
            )

            val toSend = Gson().toJson(toSock)

            if (MyApp.instance.webSocketClient!!.isOpen) {

                MyApp.instance.webSocketClient!!.send(toSend)
            } else {

                connectToSock(context)
            }

            Log.e("Get All Messages", toSend)

        }

        fun closeSocket() {

            if (MyApp.instance.webSocketClient != null && MyApp.instance.webSocketClient!!.isOpen) {

                MyApp.instance.webSocketClient!!.close()
            }
        }

        fun sendMessageToSocket(
            context: Context, message: String?, chatId: Int,chatname: Int, siteId: Int, messageToken: String,
            messageType: String, fileSize: Long?, docId: String?
        ) {

            val messageData = SendMessageToSocketModel().SendMessageData().Message()
            messageData.Message(
                chatId, chatname, docId, messageToken, messageType, message, fileSize, siteId, null
            )


            val dataset: MutableList<SendMessageToSocketModel.SendMessageData.Message> = ArrayList()
            dataset.add(messageData)

            val sockData = SendMessageToSocketModel().SendMessageData()
            sockData.SendMessageData(dataset)

            val toSock = SendMessageToSocketModel()
            toSock.SendMessageToSocketModel(
                MyApp.instance.sharedPreferences.getString(
                    SharedPrefsTags.MY_SESSION,
                    SharedPrefsTags.MY_SESSION_DEFAULT
                ), "sendmessages", sockData
            )

            val toSend = Gson().toJson(toSock)

            if (MyApp.instance.webSocketClient!!.isOpen) {

                MyApp.instance.webSocketClient!!.send(toSend)
            } else {
                SharedPrefsTags.connectToSock(context)
            }

        }



        fun sendChatStatusToSocket(context: Context, chatId: Int, solved: Int) {

            val messageData = SendMessageToSocketModel().SendMessageData().Message()
            messageData.Message(
                chatId, null, null, null, null, null, null, null, solved)


            val dataset: MutableList<SendMessageToSocketModel.SendMessageData.Message> = ArrayList()
            dataset.add(messageData)

            val sockData = SendMessageToSocketModel().SendMessageData()
            sockData.SendMessageData(dataset)

            val toSock = SendMessageToSocketModel()
            toSock.SendMessageToSocketModel(
                MyApp.instance.sharedPreferences.getString(
                    SharedPrefsTags.MY_SESSION,
                    SharedPrefsTags.MY_SESSION_DEFAULT
                ), "statuschat", sockData
            )

            val toSend = Gson().toJson(toSock)

            if (MyApp.instance.webSocketClient!!.isOpen) {

                MyApp.instance.webSocketClient!!.send(toSend)
            } else {
                SharedPrefsTags.connectToSock(context)
            }

        }



        fun connectionStatus(context: Context, tv: TextView) {

            if (MyApp.instance.connectionToSocketStatus == SharedPrefsTags.STATUS_WAITING_FOR_NETWORK) {
                tv.text = context.resources.getString(R.string.waiting_for_network)
            } else if (MyApp.instance.connectionToSocketStatus == SharedPrefsTags.STATUS_CONNECTING) {
                tv.text = context.resources.getString(R.string.connecting)
            } else if (MyApp.instance.connectionToSocketStatus == SharedPrefsTags.STATUS_UPDATING) {
                tv.text = context.resources.getString(R.string.updating)
            } else if (MyApp.instance.connectionToSocketStatus == SharedPrefsTags.STATUS_UPDATED) {
                tv.text = context.resources.getString(R.string.app_name)
            }
        }


         private fun enableTls12OnPreLollipop(client: OkHttpClient.Builder): OkHttpClient.Builder {

            if (Build.VERSION.SDK_INT in 17..21) {
                try {
                    val sc = SSLContext.getInstance("TLSv1.2")
                    sc.init(null, null, null)
                    client.sslSocketFactory(Tls12SocketFactory(sc.socketFactory))

                    val cs = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build()

                    val specs: MutableList<ConnectionSpec> = ArrayList()
                    specs.add(cs)
                    specs.add(ConnectionSpec.COMPATIBLE_TLS)
                    specs.add(ConnectionSpec.CLEARTEXT)

                    client.connectionSpecs(specs);
                } catch (exc: Exception) {
                    Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc);
                }
            }

            return client;
        }


        private fun initializeSocket(context: Context) {


            val client: OkHttpClient.Builder = OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .cache(null)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)

            enableTls12OnPreLollipop(client).build()

            ProviderInstaller.installIfNeeded(context)


            MyApp.instance.webSocketClient = null
            val handler = Handler()
            lateinit var socketReceiveMessage: SocketReceiveMessage




            MyApp.instance.webSocketClient = object : WebSocketClient(URI("wss://api.chatyar.com/websocket")) {

                override fun onMessage(message: String) {

                    MyApp.instance.connectionToSocketStatus = SharedPrefsTags.STATUS_UPDATED

                    val messageReceived: MessageFromSocketModel =
                        Gson().fromJson(message, MessageFromSocketModel::class.java)

                    if (messageReceived.type == "message") {


                        val tempData: MutableList<MessageFromSocketModel.MessageData.MessageModel> = ArrayList()
                        val tempHash: HashMap<String, MessageFromSocketModel.MessageData.MessageModel> = HashMap()

                        tempData.addAll(messageReceived.data!!.message!!)
                        tempData.addAll(MyApp.instance.mainPageDataset)

                        tempData.forEach {

                            if (it.messagetoken == null) {
                                tempHash[it.messageid!!.toString()] = it
                            } else {
                                tempHash[it.messagetoken!!] = it
                            }
                        }

                        MyApp.instance.mainPageDataset = ArrayList()
                        MyApp.instance.mainPageDataset.addAll(tempHash.values)

                        MyApp.instance.badgeCount = 0

                        val mainPageTemp: MutableList<MessageFromSocketModel.MessageData.MessageModel> = ArrayList()
                        mainPageTemp.addAll(MyApp.instance.mainPageDataset)

                        MyApp.instance.mainPageDataset.forEach {

                            if (it.operatorid == null && it.solved == 0) {

                                MyApp.instance.badgeCount++
                            }

//                            if(it.operatorid != null){
//
//                                MyApp.instance.mainPageDataset.forEach {item ->
//
//                                    if(it.chatid == item.chatid && it.date!! >= item.date!!){
//
//                                        item.seenmessage = 1
//                                        mainPageTemp[MyApp.instance.mainPageDataset.indexOf(item)] = item
//
//                                    }
//
//                                }
//
//                            }
                        }

                        MyApp.instance.mainPageDataset = ArrayList()
                        MyApp.instance.mainPageDataset.addAll(mainPageTemp)


                        ShortcutBadger.applyCount(context, MyApp.instance.badgeCount);

//                        Log.e("tag", tempHash.size.toString())

                    } else if (messageReceived.type == "resultsend") {

                        MyApp.instance.mainPageDataset.forEach {

                            if (it.messagetoken == messageReceived.data!!.message!![0].messagetoken) {
                                it.messageid = messageReceived.data!!.message!![0].messageid
                                it.messagetoken = null

                                MyApp.instance.mainPageDataset[MyApp.instance.mainPageDataset.indexOf(it)] = it

                            }

                            if (messageReceived.data!!.message!![0].chatid == it.chatid) {

                                it.seenmessage = 1
                            }
                        }
                    }

                    else if(messageReceived.type == "statuschat"){

                        MyApp.instance.mainPageDataset.forEach {

                            if (it.chatid == messageReceived.data!!.message!![0].chatid) {

                                it.solved = messageReceived.data!!.message!![0].solved

                                MyApp.instance.mainPageDataset[MyApp.instance.mainPageDataset.indexOf(it)] = it

                            }
                        }
                    }

                    socketReceiveMessage = MyApp.instance.socketReceiveMessage
                    socketReceiveMessage.onReceiveMessage(context, messageReceived)

                }

                override fun onOpen(handshake: ServerHandshake) {

                    MyApp.instance.connectionToSocketStatus = SharedPrefsTags.STATUS_UPDATING

                    MyApp.instance.mainPageDataset.forEach {

                        if (it.messageid == null) {

                            SharedPrefsTags.sendMessageToSocket(
                                context,
                                it.text!!,
                                it.chatid!!,
                                it.chatname!!,
                                it.accsiteId!!,
                                it.messagetoken!!,
                                it.messagetype!!,
                                it.size!!,
                                it.documentId
                            )
                        }
                    }

                    socketReceiveMessage = MyApp.instance.socketReceiveMessage
                    socketReceiveMessage.onOpenSocket(getURI().toString())
                }

                override fun onClose(code: Int, reason: String, remote: Boolean) {


                    if (MyApp.instance.webSocketClient != null) {
                        if (!MyApp.instance.webSocketClient!!.isOpen) {

                            if (SharedPrefsTags.isConnected(context)) {

                                MyApp.instance.connectionToSocketStatus = SharedPrefsTags.STATUS_CONNECTING
                            } else {

                                MyApp.instance.connectionToSocketStatus = SharedPrefsTags.STATUS_WAITING_FOR_NETWORK
                            }

                            handler.postDelayed({

                                if (!MyApp.instance.webSocketClient!!.isOpen) {
                                    if (!MyApp.instance.isMainFinished) {
                                        SharedPrefsTags.connectToSock(context)

                                    }
                                } else {
                                    handler.removeCallbacks(this)
                                }
                            }, 6000)
                        }
                    }


                    socketReceiveMessage = MyApp.instance.socketReceiveMessage
                    socketReceiveMessage.onCloseSocket(getURI().toString(), code, reason, remote)
                }

                override fun onError(ex: Exception) {

                    if (MyApp.instance.webSocketClient != null) {
                        if (!MyApp.instance.webSocketClient!!.isOpen) {

                            if (SharedPrefsTags.isConnected(context)) {

                                MyApp.instance.connectionToSocketStatus = SharedPrefsTags.STATUS_CONNECTING
                            } else {

                                MyApp.instance.connectionToSocketStatus = SharedPrefsTags.STATUS_WAITING_FOR_NETWORK
                            }

                            handler.postDelayed({

                                if (!MyApp.instance.webSocketClient!!.isOpen) {
                                    if (!MyApp.instance.isMainFinished) {
                                        SharedPrefsTags.connectToSock(context)
                                    }

                                } else {
                                    handler.removeCallbacks(this)
                                }
                            }, 6000)
                        }
                    }

                    socketReceiveMessage = MyApp.instance.socketReceiveMessage
                    socketReceiveMessage.onErrorSocket(ex)
                }
            }

            try {
                MyApp.instance.webSocketClient!!.connect()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }


//            val factory = WebSocketFactory()
//            val cont = NaiveSSLContext.getInstance("TLSv1.2")
//
//            cont.createSSLEngine()
//            var str = cont.defaultSSLParameters
//
//            factory.sslContext = cont
//            factory.verifyHostname = false
//
//            val ws = factory.createSocket("wss://api.chatyar.com/websocket")
//
//            ws.addListener(object : WebSocketAdapter(){
//
//
//                override fun onConnected(websocket: WebSocket?, headers: MutableMap<String, MutableList<String>>?) {
//                    super.onConnected(websocket, headers)

//
//
//                }
//
//                override fun onTextMessage(websocket: WebSocket?, text: String?) {
//                    super.onTextMessage(websocket, text)
//
//                    Log.e("onTextMessage", text)
//                }
//            })
//
//
//            Thread(Runnable {
//
//                try {
//                    ws.connect();
//                } catch (e: OpeningHandshakeException) {
//                    Log.e("OpeningHandshake", e.message)
//                } catch (e: HostnameUnverifiedException) {
//                    Log.e("HostnameUnverified", e.message)
//                } catch (e: WebSocketException) {
//                    Log.e("WebSocketException", e.message)
//                }
//
//
//            }).start()


        }


        fun getFolderSize(file: File): Long {
            var size: Long = 0
            if (file.isDirectory()) {
                for (child in file.listFiles()) {
                    size += getFolderSize(child)
                }
            } else {
                size = file.length()
            }
            return size
        }


        fun bodyToSend(file: File, chatRVAdapter: ChatRVAdapter, messageToken: String): ProgressRequestBody {


            val fileBody = ProgressRequestBody(file, object : ProgressRequestBody.UploadCallbacks {
                override fun onProgressUpdate(percentage: Int) {

                    val data: MutableList<OneChatDataModel> = ArrayList()
                    data.addAll(chatRVAdapter.data)

                    for (it in data) {

                        if (it.msgtoken != null && it.msgtoken == messageToken) {

                            it.percent = percentage
                            chatRVAdapter.data.set(chatRVAdapter.data.indexOf(it), it)
                            chatRVAdapter.notifyItemChanged(chatRVAdapter.data.indexOf(it))
                            break
                        }

                    }
                }

                override fun onError() {

                }

                override fun onFinish() {

                    val data: MutableList<OneChatDataModel> = ArrayList()
                    data.addAll(chatRVAdapter.data)

                    for (it in data) {

                        if (it.msgtoken != null && it.msgtoken == messageToken) {

                            it.percent = 100
                            chatRVAdapter.data.set(chatRVAdapter.data.indexOf(it), it)
                            chatRVAdapter.notifyItemChanged(chatRVAdapter.data.indexOf(it))
                            break
                        }
                    }
                }
            })

            return fileBody
        }


        public fun downloadFile(
            context: Context,
            docId: String?,
            chatRVAdapter: ChatRVAdapter,
            link: String,
            fileName: String
        ) {

            val data: MutableList<OneChatDataModel> = ArrayList()
            data.addAll(chatRVAdapter.data)

            val root = Environment.getExternalStorageDirectory().toString()
            var newFile = File("$root/chatyar/")

            if (!newFile.exists()) {
                newFile.mkdirs()
            }

            newFile = File(newFile, fileName)

            itemsToCancel[docId!!] = Ion.with(context)
                .load(link)

                .progress { downloaded, total ->

                    val percent = (downloaded * 100 / total)

                    for (it in data) {

                        if (it.docId != null && it.docId == docId) {

                            it.percent = percent.toInt()
                            chatRVAdapter.data[chatRVAdapter.data.indexOf(it)] = it

                            (context as Activity).runOnUiThread(Runnable {

                                chatRVAdapter.notifyItemChanged(chatRVAdapter.data.indexOf(it))
                            })

                            break
                        }

                    }

                    if (downloaded == total) {
                        for (it in data) {

                            if (it.docId != null && it.docId == docId) {

                                it.percent = 100
                                chatRVAdapter.data[chatRVAdapter.data.indexOf(it)] = it

                                (context as Activity).runOnUiThread(Runnable {

                                    chatRVAdapter.notifyItemChanged(chatRVAdapter.data.indexOf(it))
                                })

                                break
                            }

                        }
                    }

                }

                .write(newFile)
                .setCallback { e, result ->
                    //
//                    if(e != null) {
//
//                        (context as Activity).runOnUiThread(Runnable {
//                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
//                        })
//                    }

                }

        }

        public fun cancelDownload(docId: String?) {

            itemsToCancel[docId]!!.cancel()

        }


        fun resetAllDatasForSignin(){

            MyApp.instance.editor.putString(SharedPrefsTags.MY_SESSION, SharedPrefsTags.MY_SESSION_DEFAULT)
            MyApp.instance.editor.putInt(SharedPrefsTags.OPERATOR_ID, 0)
            MyApp.instance.editor.putString(SharedPrefsTags.OPERATOR_USERNAME, "non")
            MyApp.instance.editor.putInt(SharedPrefsTags.CHAT_ID, 0)
            MyApp.instance.editor.putBoolean(SharedPrefsTags.NOTIFICATION_STATUS, true)
            MyApp.instance.editor.putString(SharedPrefsTags.OPERATOR_AVATAR, "non")

            MyApp.instance.editor.apply()

            MyApp.instance.isMainFinished = false
            MyApp.instance.mainPageDataset = ArrayList()
            MyApp.instance.channelId = "0"
            MyApp.instance.emptyIntent = null
            MyApp.instance.pendingIntent = null
            MyApp.instance.notificationsIds = HashMap()
            MyApp.instance.currentChatId = 0
            MyApp.instance.siteList = ArrayList()
            MyApp.instance.whichSiteCurrentId = 0
            MyApp.instance.operatorList = ArrayList()
            MyApp.instance.badgeCount = 0

        }


        fun unAnsweredMessages(chatId: Int): Int{

            val oneChatMessages: MutableList<MessageFromSocketModel.MessageData.MessageModel> = ArrayList()

            MyApp.instance.mainPageDataset.forEach {

                if(chatId == it.chatid){
                    oneChatMessages.add(it)
                }
            }


            val item = oneChatMessages.sortedBy {it.date}.findLast { it.operatorid != null }
            var count = 0

            if(item != null){
                oneChatMessages.forEach {
                    if(it.date!! > item.date!!){

                        count += 1
                    }
                }
            }

            else {
                count = oneChatMessages.size
            }

            return count
        }




    }


}