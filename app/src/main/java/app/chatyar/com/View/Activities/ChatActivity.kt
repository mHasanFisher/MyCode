package app.chatyar.com.View.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import app.chatyar.com.Application.MyApp
import app.chatyar.com.Model.Models.MessageFromSocketModel
import app.chatyar.com.R
import app.chatyar.com.View.Adapters.ChatRVAdapter
import app.chatyar.com.ViewModel.Classes.SharedPrefsTags
import app.chatyar.com.ViewModel.Interfaces.SocketReceiveMessage
import app.chatyar.com.Model.Models.OneChatDataModel
import app.chatyar.com.View.Adapters.EmojiRVAdapter
import app.chatyar.com.ViewModel.Repositories.APIRepository
import com.github.angads25.filepicker.controller.DialogSelectionListener
import com.github.angads25.filepicker.model.DialogConfigs
import com.github.angads25.filepicker.model.DialogProperties
import com.github.angads25.filepicker.view.FilePickerDialog
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MultipartBody
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity(), SocketReceiveMessage {

    private lateinit var chatRV: RecyclerView
    private lateinit var solvingRL: RelativeLayout
    private lateinit var solvingButt: Button
    private lateinit var loadingSolve: ProgressBar
    private lateinit var chatRVAdapter: ChatRVAdapter
    private lateinit var chatRVLinearLM: LinearLayoutManager
    private var dataset: MutableList<OneChatDataModel> = ArrayList()

    private lateinit var messageBoxET: EditText
    private lateinit var sendFileButt: ImageView
    private lateinit var emojiButt: ImageView
    private lateinit var rootView: ViewGroup
    private lateinit var sendMessageButt: ImageButton
    private lateinit var loadingView: ProgressBar

    private lateinit var toolbar: Toolbar
    private lateinit var customNav: View
    private lateinit var nameOfUserTV: TextView
    private lateinit var statusOfUserTV: TextView
    private lateinit var itemUnreadMessagesTV: TextView
    private lateinit var profileImage: CircleImageView
    private lateinit var imageViewForScrollingRV: ImageView

    private lateinit var emojiRV: RecyclerView
    private lateinit var emojiRVAdapter: EmojiRVAdapter
    private lateinit var emojiRVStaggeredGLM: StaggeredGridLayoutManager
    private var emojiDataset: MutableList<String> = ArrayList()

    private var chatId = 0
    private var chatname = 0
    private var siteId = 0
    private var solved = 0

    val MESSAGE_SEND_TYPE = 1
    val MESSAGE_RECEIVE_TYPE = 2
    val MESSAGE_DATE_TYPE = 3
    val MESSAGE_SEND_FILE_TYPE = 4
    val MESSAGE_RECEIVE_FILE_TYPE = 5

    lateinit var dialog: FilePickerDialog

    override fun onResume() {
        super.onResume()

        try {
            MyApp.instance.currentChatId = chatId

            val notifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val iteratorNoti = MyApp.instance.notificationsIds.iterator()

            for (item in iteratorNoti) {

                if (item.key == chatId) {

                    notifyMgr.cancel(siteId.toString(), chatId)
                    iteratorNoti.remove()
                }

            }

            var isExists = false

            for (item in MyApp.instance.notificationsIds.keys) {

                if (MyApp.instance.notificationsIds[item] == siteId) {
                    isExists = true
                }
            }

            if (!isExists) {
                notifyMgr.cancel(siteId.toString(), siteId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onPause() {
        super.onPause()

        MyApp.instance.currentChatId = 0
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)



        try {
            if (intent.extras != null) {
                chatId = intent.extras!!.getInt(SharedPrefsTags.CHAT_ID, 0)
                chatname = intent.extras!!.getInt(SharedPrefsTags.CHAT_NAME, 0)
                siteId = MyApp.instance.whichSiteCurrentId
                solved = intent.extras!!.getInt(SharedPrefsTags.SOLVED, 0)
            }

            initializeToolbar()


            messageBoxET = findViewById(R.id.messageBoxET)
            sendMessageButt = findViewById(R.id.sendMessageButt)
            loadingView = findViewById(R.id.loadingView)
            imageViewForScrollingRV = findViewById(R.id.imageViewForScrollingRV)
            itemUnreadMessagesTV = findViewById(R.id.itemUnreadMessagesTV)
            rootView = findViewById(R.id.chat_act_root)
            sendFileButt = findViewById(R.id.sendFileButt)
            emojiButt = findViewById(R.id.emojiButt)
            emojiRV = findViewById(R.id.emojiRV)


            window.setBackgroundDrawableResource(R.drawable.chat_back);


            emojiDataset.add("😋")
            emojiDataset.add("😂")
            emojiDataset.add("😎")
            emojiDataset.add("😍")
            emojiDataset.add("😐")
            emojiDataset.add("😥")
            emojiDataset.add("😛")
            emojiDataset.add("😱")
            emojiDataset.add("😤")
            emojiDataset.add("😡")
            emojiDataset.add("😮")
            emojiDataset.add("🙂")
            emojiDataset.add("🤔")
            emojiDataset.add("😊")
            emojiDataset.add("😮")
            emojiDataset.add("👍")
            emojiDataset.add("👎")
            emojiDataset.add("🌹")
            emojiDataset.add("💐")
            emojiDataset.add("💓")
            emojiDataset.add("👋")
            emojiDataset.add("👌")
            emojiDataset.add("👏")
            emojiDataset.add("🙏")


            emojiRVStaggeredGLM = StaggeredGridLayoutManager(10, StaggeredGridLayoutManager.VERTICAL)
            emojiRVAdapter = EmojiRVAdapter(this, messageBoxET, emojiDataset)

            emojiRV.layoutManager = emojiRVStaggeredGLM
            emojiRV.adapter = emojiRVAdapter


            chatRV = findViewById(R.id.chatRV)
            solvingRL = findViewById(R.id.solvingRL)
            solvingButt = findViewById(R.id.solvingButt)
            loadingSolve = findViewById(R.id.loadingSolve)
            chatRVLinearLM = LinearLayoutManager(this@ChatActivity, LinearLayoutManager.VERTICAL, false)
            chatRVAdapter = ChatRVAdapter(dataset, this@ChatActivity)

            chatRV.layoutManager = chatRVLinearLM
            chatRV.adapter = chatRVAdapter
            chatRV.itemAnimator = null

            chatRV.scrollToPosition(dataset.size - 1)

            if (messageBoxET.text.isEmpty()) {
                sendMessageButt.isEnabled = false
            }


            if(solved == 1){

                solvingRL.background = ContextCompat.getDrawable(this, R.color.green)
                solvingButt.setText(resources.getString(R.string.solved))

            }
            else {
                solvingRL.background = ContextCompat.getDrawable(this, R.color.redBadge)
                solvingButt.setText(resources.getString(R.string.solve))
            }

            solvingRL.setOnClickListener {

                loadingSolve.visibility = View.VISIBLE

                if(solved == 1) {
                    SharedPrefsTags.sendChatStatusToSocket(this, chatId, 0)
                }
                else {
                    SharedPrefsTags.sendChatStatusToSocket(this, chatId, 1)
                }
            }

            messageBoxET.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {

                    if (!messageBoxET.text.trim().isEmpty()) {

                        sendMessageButt.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@ChatActivity,
                                R.mipmap.icon_send
                            )
                        )
                        sendMessageButt.isEnabled = true
                    } else {

                        sendMessageButt.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@ChatActivity,
                                R.mipmap.icon_send_disabled
                            )
                        )

                        sendMessageButt.isEnabled = false
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })


            imageViewForScrollingRV.setOnClickListener {

                itemUnreadMessagesTV.visibility = View.GONE
                itemUnreadMessagesTV.text = ""
                imageViewForScrollingRV.visibility = View.GONE
                chatRV.scrollToPosition(dataset.size - 1)


            }



            chatRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if (!chatRV.canScrollVertically(1)) {

                        itemUnreadMessagesTV.visibility = View.GONE
                        itemUnreadMessagesTV.text = ""
                        imageViewForScrollingRV.visibility = View.GONE

                    }
                }


                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                    val visibleItemCount = recyclerView.layoutManager!!.getChildCount()
                    val totalItemCount = recyclerView.layoutManager!!.getItemCount()
                    val pastVisibleItems =
                        (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                        //End of list
                        imageViewForScrollingRV.visibility = View.GONE
                        itemUnreadMessagesTV.visibility = View.GONE

                    }
                }
            });




            sendMessageButt.setOnClickListener {

                sendMessage(messageBoxET.text.toString().trim())

            }

            MyApp.instance.socketReceiveMessage = this

//        val intent = Intent(this@ChatActivity, SocketService::class.java)
//        intent.putExtra(SharedPrefsTags.COME_FROM_WHERE, SharedPrefsTags.COME_FROM_CHAT)
//        intent.putExtra(SharedPrefsTags.CHAT_ORDER, SharedPrefsTags.CHAT_GET_MESSAGES)
//        intent.putExtra(SharedPrefsTags.CHAT_ID, chatId)
//        startService(intent)

            var lastChatDate = 0L

            if (dataset.size != 0) {
                lastChatDate = dataset.sortedByDescending { item -> item.date }[0].date!!
            }

            SharedPrefsTags.getOneChatMessages(this, chatId, siteId, lastChatDate)

            SharedPrefsTags.connectionStatus(this, statusOfUserTV)
            nameOfUserTV.text = " گفتگوی $chatname "

            emojiButt.setOnClickListener {

                // Check if no view has focus:
                val view = this.currentFocus
                if (view != null) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }

                Handler().postDelayed({

                    if (emojiRV.visibility == View.GONE) {
                        emojiRV.visibility = View.VISIBLE
                    } else {
                        emojiRV.visibility = View.GONE
                    }

                }, 100)


            }



            messageBoxET.setOnClickListener {

                if (emojiRV.visibility == View.VISIBLE) {
                    emojiRV.visibility = View.GONE
                }
            }

            messageBoxET.setOnFocusChangeListener { v, hasFocus ->

                if (hasFocus) {
                    if (emojiRV.visibility == View.VISIBLE) {
                        emojiRV.visibility = View.GONE
                    }
                }

            }


            val properties = DialogProperties()

            properties.selection_mode = DialogConfigs.SINGLE_MODE
            properties.selection_type = DialogConfigs.FILE_SELECT
            properties.root = File(DialogConfigs.DEFAULT_DIR)
            properties.error_dir = File(DialogConfigs.DEFAULT_DIR)
            properties.offset = File(DialogConfigs.DEFAULT_DIR)
            properties.extensions = null

            dialog = FilePickerDialog(this@ChatActivity, properties)
            dialog.setTitle("Select a File");

            dialog.setDialogSelectionListener(object : DialogSelectionListener {
                override fun onSelectedFilePaths(files: Array<out String>?) {

                    val uri = files!![0]
                    val fileToUp = File(uri)
                    sendFileMessage(uri, fileToUp.name, SharedPrefsTags.getFolderSize(fileToUp))

                }
            })


            sendFileButt.setOnClickListener {

                try {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CAMERA
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                        ActivityCompat.requestPermissions(
                            this as Activity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                            1
                        )

                    } else {

                        filePickerChooseTypeAlert()

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        try {
            if (requestCode == 1) {

                try {

                    filePickerChooseTypeAlert()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try {
            when (requestCode) {

//                Constant.REQUEST_CODE_PICK_IMAGE -> {

//                    if (resultCode == RESULT_OK) {
//                        val files: ArrayList<ImageFile> =
//                            data!!.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
//
//                        val uri = files[0].path
//                        val fileToUp = File(uri)
//                        sendFileMessage(uri, fileToUp.name, SharedPrefsTags.getFolderSize(fileToUp))
//                    }
//                }

                5 -> {

                    if (resultCode == Activity.RESULT_OK) {

//                        val files = data!!.getStringArrayListExtra(Pix.IMAGE_RESULTS)[0]
//                        Toast.makeText(this, returnValue, Toast.LENGTH_LONG).show()
                        val files = data!!.data!!

                        val fileToUp = File(getRealPathFromURI(files))
                        sendFileMessage(fileToUp.path, fileToUp.name, SharedPrefsTags.getFolderSize(fileToUp))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onReceiveMessage( context: Context, message: MessageFromSocketModel?) {

        try {
            runOnUiThread {

                if (MyApp.instance.connectionToSocketStatus == SharedPrefsTags.STATUS_UPDATED) {
                    statusOfUserTV.text = resources.getString(R.string.last_seen_recently)
                }

                loadingView.visibility = View.GONE
                val datasetSizeBeforeAdd = dataset.size

                if (message!!.type == "message") {

                    for (item in message.data!!.message!!) {


                        if (item.chatid == chatId) {

                            if (!thisDayAddedBefore(item.date!!)) {

                                setStartOfTheTime(item.date!!)
                            }

                            if (item.operatorid == null) {



                                var isAdded = false
                                dataset.forEach {

                                    if (item.messageid == it.messageid && item.date == it.date) {
                                        isAdded = true
                                    }
                                }

                                if (!isAdded) {
                                    if (item.messagetype == "file") {

                                        dataset.add(
                                            OneChatDataModel(
                                                MESSAGE_RECEIVE_FILE_TYPE, item.chatid!!, item.messageid,
                                                null, item.messagetype, item.photo, item.text,
                                                item.date, item.date, item.operatorid,
                                                item.accsiteId, item.seenmessage, item.documentId,
                                                false, null, null,
                                                item.size, false
                                            )
                                        )

                                    } else {

                                        dataset.add(
                                            OneChatDataModel(
                                                MESSAGE_RECEIVE_TYPE, item.chatid!!, item.messageid,
                                                null, item.messagetype, item.photo,
                                                item.text, item.date, item.date,
                                                item.operatorid, item.accsiteId, item.seenmessage,
                                                item.documentId, false, null,
                                                null, item.size, false
                                            )
                                        )

                                    }
                                }


                                if(message.data!!.message!!.indexOf(item) == 0 && solved == 1) {
                                    SharedPrefsTags.sendChatStatusToSocket(this, chatId, 0)
                                }

                                //                    dataset.add(ProfileInfoModel(item.text, 2))
                            } else {

                                var isAdded = false
                                dataset.forEach {

                                    if (item.messageid == it.messageid && item.date == it.date) {
                                        isAdded = true
                                    }
                                }

                                if (!isAdded) {

                                    if (item.messagetype == "file") {

                                        var path: String? = null

                                        val root = Environment.getExternalStorageDirectory().toString()
                                        val file = File("$root/chatyar/${item.text}")
                                        path = file.path

                                        if (!file.exists()) {

                                            path = null
                                        }

                                        dataset.add(
                                            OneChatDataModel(
                                                MESSAGE_SEND_FILE_TYPE, item.chatid!!, item.messageid,
                                                null, item.messagetype, item.photo,
                                                item.text, item.date, item.date,
                                                item.operatorid, item.accsiteId, item.seenmessage,
                                                item.documentId, true, path,
                                                null, item.size, false
                                            )
                                        )

                                    } else {

                                        dataset.add(
                                            OneChatDataModel(
                                                MESSAGE_SEND_TYPE, item.chatid!!, item.messageid,
                                                null, item.messagetype, item.photo,
                                                item.text, item.date, item.date,
                                                item.operatorid, item.accsiteId, item.seenmessage,
                                                item.documentId, true, null,
                                                null, item.size, false
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }


                    dataset.sortBy { it.date }

                    chatRVAdapter.data = dataset

                    var idOfFirstUnread = dataset.size - 1


                    for (item in dataset) {

                        if (item.seenmessage != null) {

                            if (item.seenmessage == 0 && item.operatorid == null) {
                                idOfFirstUnread = dataset.indexOf(item)
                                break
                            }
                        }
                    }


                    if (message.data!!.message!!.size > 1) {

                        chatRVAdapter.notifyItemRangeInserted(datasetSizeBeforeAdd, message.data!!.message!!.size)
                        chatRV.scrollToPosition(idOfFirstUnread)
                    } else {

                        chatRVAdapter.notifyItemInserted(dataset.size - 1)
                        chatRV.scrollToPosition(dataset.size - 1)

                    }

                    if ((dataset.size - 1) - chatRVLinearLM.findLastCompletelyVisibleItemPosition() > 4) {
                        // Its at bottom
                        var countUnread = 0

                        message.data!!.message!!.forEach {

                            if (it.seenmessage == 0) {
                                countUnread++
                            }
                        }

                        if (countUnread != 0) {

                            imageViewForScrollingRV.visibility = View.VISIBLE
                            itemUnreadMessagesTV.visibility = View.VISIBLE

                            itemUnreadMessagesTV.text = countUnread.toString()
                        }
                    }




                }
                else if (message.type == "resultsend") {

                    dataset.forEach {

                        if (it.msgtoken == message.data!!.message!![0].messagetoken) {
                            it.messageid = message.data!!.message!![0].messageid
                            it.isSent = true
                            dataset[dataset.indexOf(it)] = it
                            chatRVAdapter.notifyItemChanged(dataset.indexOf(it))

                        }
                    }
                }
                else if (message.type == "typingclient") {

                    if (chatId == message.data!!.message!![0].chatid) {
                        val tempStatus = statusOfUserTV.text

                        statusOfUserTV.text = resources.getString(R.string.is_typing)

                        Handler().postDelayed({

                            statusOfUserTV.text = tempStatus

                        }, 3000)
                    }

                }

                else if(message.type == "statuschat") {

                    loadingSolve.visibility = View.GONE

                    if(message.data!!.message!![0].solved == 1){

                        solvingRL.background = ContextCompat.getDrawable(this, R.color.green)
                        solvingButt.text = resources.getString(R.string.solved)
                        solved = 1

                    }
                    else {
                        solvingRL.background = ContextCompat.getDrawable(this, R.color.redBadge)
                        solvingButt.text = resources.getString(R.string.solve)
                        solved = 0
                    }

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOpenSocket(connectedTo: String) {

        try {
            runOnUiThread {

                SharedPrefsTags.connectionStatus(this, statusOfUserTV)
            }

            var lastChatDate = 0L

            if (dataset.size != 0) {
                lastChatDate = dataset.sortedByDescending { item -> item.date }[0].date!!
            }

            SharedPrefsTags.getOneChatMessages(this, chatId, siteId, lastChatDate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCloseSocket(disconnectedFrom: String, code: Int, reason: String, remote: Boolean) {

        try {
            runOnUiThread {

                SharedPrefsTags.connectionStatus(this, statusOfUserTV)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onErrorSocket(ex: Exception) {

        try {
            runOnUiThread {

                SharedPrefsTags.connectionStatus(this, statusOfUserTV)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendMessage(message: String) {

        try {
            if (!thisDayAddedBefore(System.currentTimeMillis() / 1000)) {

                setStartOfTheTime(System.currentTimeMillis() / 1000)
            }

            val messageToken = UUID.randomUUID().toString()

            val messages: MessageFromSocketModel.MessageData.MessageModel =
                MessageFromSocketModel().MessageData().MessageModel()
            messages.messageModel(
                chatId, chatname,null, "text", null, null, message, System.currentTimeMillis() / 1000,
                MyApp.instance.sharedPreferences.getInt(SharedPrefsTags.OPERATOR_ID, 0), siteId, null, null, messageToken
            )
            MyApp.instance.mainPageDataset.add(messages)

            dataset.add(
                OneChatDataModel(
                    MESSAGE_SEND_TYPE, chatId, null,
                    messageToken, "text", null,
                    message, System.currentTimeMillis() / 1000,
                    System.currentTimeMillis() / 1000,
                    MyApp.instance.sharedPreferences.getInt(SharedPrefsTags.OPERATOR_ID, 0), siteId, null,
                    null, false, null,
                    null, null, false
                )
            )

            chatRVAdapter.notifyItemInserted(dataset.size)
            chatRV.scrollToPosition(dataset.size - 1)

            SharedPrefsTags.sendMessageToSocket(this, message, chatId, chatname, siteId, messageToken, "text", null, "")

            messageBoxET.setText("")

            itemUnreadMessagesTV.visibility = View.GONE
            itemUnreadMessagesTV.text = ""
            imageViewForScrollingRV.visibility = View.GONE

            if(solved == 0) {
                SharedPrefsTags.sendChatStatusToSocket(this, chatId, 1)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendFileMessage(filePath: String, fileName: String, fileSize: Long) {

        try {
            val oldFile = File(filePath)
            val root = Environment.getExternalStorageDirectory().toString()
            var newFile = File("$root/chatyar/")

            if (!newFile.exists()) {
                newFile.mkdirs()
            }

            newFile = File(newFile, fileName)

            oldFile.copyTo(newFile, true)

            if (!thisDayAddedBefore(System.currentTimeMillis() / 1000)) {

                setStartOfTheTime(System.currentTimeMillis() / 1000)
            }

            val messageToken = UUID.randomUUID().toString()

//            val messages: MessageFromSocketModel.MessageData.MessageModel =
//                MessageFromSocketModel().MessageData().MessageModel()
//            messages.messageModel(
//                chatId, chatname,null, "file", null, null, fileName, System.currentTimeMillis() / 1000,
//                MyApp.instance.sharedPreferences.getInt(SharedPrefsTags.OPERATOR_ID, 0), siteId, null, null, messageToken, fileSize
//            )
//            MyApp.instance.mainPageDataset.add(messages)

            dataset.add(
                OneChatDataModel(
                    MESSAGE_SEND_FILE_TYPE, chatId, null, messageToken, "file", null,
                    fileName, System.currentTimeMillis() / 1000,
                    System.currentTimeMillis() / 1000, MyApp.instance.sharedPreferences.getInt(SharedPrefsTags.OPERATOR_ID, 0),
                    siteId, null, null, false, newFile.path, 0, fileSize, false
                )
            )
//        dataset.add(ProfileInfoModel(message, 1))
            chatRVAdapter.notifyItemInserted(dataset.size)
            chatRV.scrollToPosition(dataset.size - 1)

//        val file = File(filePath)
            val fileBody = SharedPrefsTags.bodyToSend(newFile, chatRVAdapter, messageToken)

            val fileToUpload = MultipartBody.Part.createFormData("file", newFile.name, fileBody)

            APIRepository.uploadFile(
                this, fileToUpload, fileName,
                chatId, chatname, siteId, messageToken, fileSize
            )

//        SharedPrefsTags.sendMessageToSocket(this, fileName, chatId, siteId, messageToken, "file", fileSize)

            messageBoxET.setText("")

            itemUnreadMessagesTV.visibility = View.GONE
            itemUnreadMessagesTV.text = ""
            imageViewForScrollingRV.visibility = View.GONE


            if(solved == 0) {
                SharedPrefsTags.sendChatStatusToSocket(this, chatId, 1)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initializeToolbar() {

        try {
            toolbar = findViewById(R.id.toolbar)
            setSupportActionBar(toolbar)

            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(true)

            supportActionBar!!.title = ""

            val lp = android.support.v7.app.ActionBar.LayoutParams(
                android.support.v7.app.ActionBar.LayoutParams.MATCH_PARENT,
                android.support.v7.app.ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.START or Gravity.CENTER_VERTICAL
            )

            //inflate the custom action bar layout
            customNav = LayoutInflater.from(this)
                .inflate(R.layout.action_bar_chat_page, null)

            nameOfUserTV = customNav.findViewById(R.id.nameOfUserTV)
            statusOfUserTV = customNav.findViewById(R.id.statusOfUserTV)
            profileImage = customNav.findViewById(R.id.profileImage)

            val whichAv = chatId % 6
            val colorName = "backAvatar"
            val newColorName = colorName + whichAv

            val theColor = resources.getIdentifier(newColorName, "color", packageName)
            profileImage.circleBackgroundColor = ContextCompat.getColor(this, theColor)

            //set custom action bar view and layout params
            supportActionBar!!.setCustomView(customNav, lp)
            supportActionBar!!.setDisplayShowCustomEnabled(true)

            customNav.setOnClickListener {

                val intent = Intent(this@ChatActivity, ProfileInfoActivity::class.java)
                intent.putExtra(SharedPrefsTags.CHAT_ID, chatId)
                intent.putExtra(SharedPrefsTags.CHAT_NAME, chatname)
                startActivity(intent)
            }

            if (Locale.getDefault().language == "en") {

                customNav.layoutDirection = View.LAYOUT_DIRECTION_LTR
                nameOfUserTV.layoutDirection = View.LAYOUT_DIRECTION_LTR
                statusOfUserTV.layoutDirection = View.LAYOUT_DIRECTION_LTR
                statusOfUserTV.gravity = Gravity.LEFT
                statusOfUserTV.textAlignment = View.TEXT_ALIGNMENT_GRAVITY
                nameOfUserTV.gravity = Gravity.LEFT
                nameOfUserTV.textAlignment = View.TEXT_ALIGNMENT_GRAVITY
            } else {

                customNav.layoutDirection = View.LAYOUT_DIRECTION_RTL
                nameOfUserTV.layoutDirection = View.LAYOUT_DIRECTION_RTL
                statusOfUserTV.layoutDirection = View.LAYOUT_DIRECTION_RTL
                statusOfUserTV.gravity = Gravity.RIGHT
                statusOfUserTV.textAlignment = View.TEXT_ALIGNMENT_GRAVITY
                nameOfUserTV.gravity = Gravity.RIGHT
                nameOfUserTV.textAlignment = View.TEXT_ALIGNMENT_GRAVITY
            }
        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    private fun thisDayAddedBefore(unixTime: Long): Boolean {

        try {
            if (dataset.size == 0) {

                return false
            }
            val unixTimeLast = dataset.get(dataset.lastIndex).date!!

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = unixTime * 1000L
            calendar.timeZone = java.util.TimeZone.getTimeZone(TimeZone.getDefault().id)

            val calendarLast = Calendar.getInstance()
            calendarLast.timeInMillis = unixTimeLast * 1000L
            calendarLast.timeZone = java.util.TimeZone.getTimeZone(TimeZone.getDefault().id)

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val yearLast = calendarLast.get(Calendar.YEAR)
            val monthLast = calendarLast.get(Calendar.MONTH)
            val dayLast = calendarLast.get(Calendar.DAY_OF_MONTH)

            return year == yearLast && month == monthLast && day == dayLast

        } catch (e: Exception) {

            return false
        }
    }

    private fun setStartOfTheTime(date: Long) {

        try {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = date * 1000L
            calendar.timeZone = java.util.TimeZone.getTimeZone(TimeZone.getDefault().id)

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            val dateIns = calendar.timeInMillis / 1000

            dataset.add(
                OneChatDataModel(
                    MESSAGE_DATE_TYPE,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    dateIns,
                    date,
                    null,
                    null
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun filePickerChooseTypeAlert() {

        try {
            val builder = AlertDialog.Builder(this)
            val dialogCustomView: AlertDialog
            val inflaterAlert = layoutInflater
            val viewAlert = inflaterAlert.inflate(R.layout.alert_file_type_chooser, null)

            val galleryButt: Button = viewAlert.findViewById(R.id.galleryButt)
            val fileButt: Button = viewAlert.findViewById(R.id.fileButt)


            builder.setView(viewAlert)
            dialogCustomView = builder.create()

            galleryButt.setOnClickListener {

//                val intent1 = Intent(this, ImagePickActivity::class.java)
//                intent1.putExtra(IS_NEED_CAMERA, true)
//                intent1.putExtra(Constant.MAX_NUMBER, 1)
//                startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE)

//                Pix.start(this, 5)

                val pickPhoto = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(pickPhoto, 5)//one can be replaced with any action code

                dialogCustomView.dismiss()
            }

            fileButt.setOnClickListener {

                dialog.show()

                dialogCustomView.dismiss()
            }

            dialogCustomView.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @SuppressLint("Recycle")
    fun getRealPathFromURI(contentURI: Uri): String {

        var result = ""
        val cursor = contentResolver.query(contentURI, null, null, null, null)!!

        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        result = cursor.getString(idx)

        cursor.close()

        return result;
    }
}
