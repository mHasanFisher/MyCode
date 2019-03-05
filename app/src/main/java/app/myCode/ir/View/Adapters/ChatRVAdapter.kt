package app.myCode.ir.View.Adapters

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import app.myCode.ir.Model.Models.OneChatDataModel
import app.myCode.ir.ViewModel.Repositories.APIRepository
import java.io.File
import java.util.*
import android.widget.Toast
import android.webkit.MimeTypeMap
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import app.myCode.ir.Application.MyApp
import app.myCode.ir.R
import app.myCode.ir.ViewModel.Classes.SharedPrefsTags


class ChatRVAdapter(var data: MutableList<OneChatDataModel>, var activity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val MESSAGE_SEND_TYPE = 1
    val MESSAGE_RECEIVE_TYPE = 2
    val MESSAGE_DATE_TYPE = 3
    val MESSAGE_FILE_SEND_TYPE = 4
    val MESSAGE_FILE_RECEIVE_TYPE = 5


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == MESSAGE_SEND_TYPE) {
            val v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view_message_send, viewGroup, false)
            return ChatRVAdapter.MessageSendViewHolder(v)
        } else if (viewType == MESSAGE_RECEIVE_TYPE) {
            val v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view_message_receive, viewGroup, false)
            return ChatRVAdapter.MessageReceiveViewHolder(v)

        } else if (viewType == MESSAGE_FILE_SEND_TYPE) {
            val v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view_message_send_file, viewGroup, false)
            return ChatRVAdapter.MessageSendFileViewHolder(v)

        } else if (viewType == MESSAGE_FILE_RECEIVE_TYPE) {
            val v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view_message_receive_file, viewGroup, false)
            return ChatRVAdapter.MessageReceiveFileViewHolder(v)

        } else {

            val v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_view_message_date, viewGroup, false)
            return ChatRVAdapter.MessageDateViewHolder(v)

        }
    }


    override fun getItemViewType(position: Int): Int {

        if (data.get(position).itemtype == MESSAGE_SEND_TYPE) {

            return MESSAGE_SEND_TYPE
        } else if (data.get(position).itemtype == MESSAGE_FILE_SEND_TYPE) {

            return MESSAGE_FILE_SEND_TYPE
        } else if (data.get(position).itemtype == MESSAGE_FILE_RECEIVE_TYPE) {

            return MESSAGE_FILE_RECEIVE_TYPE
        } else if (data.get(position).itemtype == MESSAGE_RECEIVE_TYPE) {

            return MESSAGE_RECEIVE_TYPE

        } else if (data.get(position).itemtype == MESSAGE_DATE_TYPE) {

            return MESSAGE_DATE_TYPE
        }

        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {

        return data.size
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        if (viewHolder.itemViewType == MESSAGE_SEND_TYPE) {

            val messageSendHolder = viewHolder as MessageSendViewHolder

            messageSendHolder.sentText.text = data[position].text


            val opName = MyApp.instance.operatorList.find { it.accountId == data[position].operatorid }

            if (opName!!.accountUsername != null) {

                if (opName.accountId == MyApp.instance.sharedPreferences.getInt(SharedPrefsTags.OPERATOR_ID, 0)) {
                    messageSendHolder.senderTV.text = "You"
                } else {
                    messageSendHolder.senderTV.text = opName.accountUsername.toString()
                }
            } else {
                messageSendHolder.senderTV.visibility = View.GONE
            }


            messageSendHolder.timeSent.text = convertUnixToTime(data[position].date!!)

            messageSendHolder.itemView.setOnClickListener {
                ordersAlert(position)
            }

            if (!data[position].isSent!!) {
                messageSendHolder.tickSent.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.mipmap.icon_timer_tick
                    )
                )
            } else if (data[position].isSent!!) {
                messageSendHolder.tickSent.setImageDrawable(ContextCompat.getDrawable(activity, R.mipmap.icon_one_tick))
            }


            if (data[position].seenmessage == 1) {
                messageSendHolder.tickSent.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.mipmap.icon_double_tick
                    )
                )
            }


//=======================================================================================================================FILE SEND

        } else if (viewHolder.itemViewType == MESSAGE_FILE_SEND_TYPE) {

            val messageSendFileHolder = viewHolder as MessageSendFileViewHolder


            if (data[position].loadingActive!!) {
                messageSendFileHolder.loadingView.visibility = View.VISIBLE
            } else {
                messageSendFileHolder.loadingView.visibility = View.INVISIBLE
            }

            val opName = MyApp.instance.operatorList.find { it.accountId == data[position].operatorid }

            if (opName!!.accountUsername != null) {
                if (opName.accountId == MyApp.instance.sharedPreferences.getInt(SharedPrefsTags.OPERATOR_ID, 0)) {
                    messageSendFileHolder.senderTV.text = "You"
                } else {
                    messageSendFileHolder.senderTV.text = opName.accountUsername.toString()
                }
            } else {
                messageSendFileHolder.senderTV.visibility = View.GONE
            }



            if (data[position].percent != null && data[position].percent == 100) {

                messageSendFileHolder.progressBar.visibility = View.INVISIBLE
                messageSendFileHolder.fileCanSeeIV.visibility = View.VISIBLE
                messageSendFileHolder.multiplyIV.visibility = View.INVISIBLE
                messageSendFileHolder.downloadIV.visibility = View.INVISIBLE
                messageSendFileHolder.loadingView.visibility = View.INVISIBLE

            } else if (data[position].percent != null && data[position].percent!! < 100 && data[position].percent!! >= 0) {


                messageSendFileHolder.progressBar.visibility = View.VISIBLE
                messageSendFileHolder.fileCanSeeIV.visibility = View.INVISIBLE
                messageSendFileHolder.multiplyIV.visibility = View.VISIBLE
                messageSendFileHolder.downloadIV.visibility = View.INVISIBLE
                messageSendFileHolder.loadingView.visibility = View.INVISIBLE

                messageSendFileHolder.progressBar.progress = data[position].percent!!


            } else if (data[position].percent == null) {

                val root = Environment.getExternalStorageDirectory().toString()
                val myDir = File("$root/chatyar/${data[position].text}")

                if (myDir.exists()) {

                    messageSendFileHolder.fileCanSeeIV.visibility = View.VISIBLE
                    messageSendFileHolder.multiplyIV.visibility = View.INVISIBLE
                    messageSendFileHolder.downloadIV.visibility = View.INVISIBLE
                    messageSendFileHolder.progressBar.visibility = View.INVISIBLE
                    messageSendFileHolder.loadingView.visibility = View.INVISIBLE

                } else {

                    messageSendFileHolder.fileCanSeeIV.visibility = View.INVISIBLE
                    messageSendFileHolder.multiplyIV.visibility = View.INVISIBLE
                    messageSendFileHolder.downloadIV.visibility = View.VISIBLE
                    messageSendFileHolder.progressBar.visibility = View.INVISIBLE
                    messageSendFileHolder.loadingView.visibility = View.INVISIBLE

                }

            }


////                             var requestFile: RequestBody = RequestBody.create(MediaType.parse("image/*"), avatarFile)
//

            messageSendFileHolder.fileCanSeeIV.setOnClickListener {

                if (data[position].filePath != null) {

                    val newIntent = Intent(Intent.ACTION_VIEW)
                    val mimeType = getMimeType(data[position].filePath!!)
                    newIntent.setDataAndType(
                        FileProvider.getUriForFile(
                            activity,
                            activity.packageName + ".provider",
                            File(data[position].filePath!!)
                        ), mimeType
                    )
                    newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    newIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

                    try {
                        activity.startActivity(newIntent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(activity, "فایل مورد نظر قابل نمایش نیست.", Toast.LENGTH_LONG).show()
                    }

                } else {

                    val root = Environment.getExternalStorageDirectory().toString()
                    val myDir = File("$root/chatyar/${data[position].text}")

                    if (myDir.exists()) {

                        val newIntent = Intent(Intent.ACTION_VIEW)
                        val mimeType = getMimeType(myDir.path)
                        newIntent.setDataAndType(
                            FileProvider.getUriForFile(
                                activity,
                                activity.packageName + ".provider", myDir
                            ), mimeType
                        )
                        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        newIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

                        try {
                            activity.startActivity(newIntent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(activity, "فایل مورد نظر قابل نمایش نیست.", Toast.LENGTH_LONG).show()
                        }

                    }


                }
            }


            messageSendFileHolder.downloadIV.setOnClickListener {

                if (checkPermissions()) {

                    APIRepository.getFileLink(activity, data[position].docId, this, data[position].text!!)
                }

            }
//

            messageSendFileHolder.multiplyIV.setOnClickListener {


                if (data[position].docId != null) {

                    SharedPrefsTags.cancelDownload(data[position].docId)

                }

                if (data[position].msgtoken != null) {
                    APIRepository.cancelUpload(data[position].msgtoken!!)
                    data.removeAt(position)
                    notifyItemRemoved(position)
                }



                messageSendFileHolder.fileCanSeeIV.visibility = View.INVISIBLE
                messageSendFileHolder.multiplyIV.visibility = View.INVISIBLE
                messageSendFileHolder.downloadIV.visibility = View.VISIBLE
                messageSendFileHolder.progressBar.visibility = View.INVISIBLE
                messageSendFileHolder.loadingView.visibility = View.INVISIBLE

            }

            messageSendFileHolder.uploadFileFrameL.setOnClickListener {

            }

            messageSendFileHolder.fileNameTV.text = data[position].text

            var finalSize = ""

            if (data[position].size!! >= 1024) {


                if (data[position].size!! >= 1048576) {
                    finalSize = "${data[position].size!!.div(1048576)}  MB"
                } else {
                    finalSize = "${data[position].size!!.div(1024)}  KB"
                }
            } else {
                finalSize = "${data[position].size!!}  Bytes"
            }


            messageSendFileHolder.fileSizeTV.text = finalSize


            messageSendFileHolder.timeSent.text = convertUnixToTime(data[position].date!!)

            messageSendFileHolder.itemView.setOnClickListener {
                ordersAlert(position)
            }

            if (!data[position].isSent!!) {
                messageSendFileHolder.tickSent.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.mipmap.icon_timer_tick
                    )
                )
            } else if (data[position].isSent!!) {
                messageSendFileHolder.tickSent.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.mipmap.icon_one_tick
                    )
                )
            }

            if (data[position].seenmessage == 1) {
                messageSendFileHolder.tickSent.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.mipmap.icon_double_tick
                    )
                )
            }


        } else if (viewHolder.itemViewType == MESSAGE_RECEIVE_TYPE) {

            val messageReceiveHolder = viewHolder as MessageReceiveViewHolder

            messageReceiveHolder.receivedText.text = data[position].text
            messageReceiveHolder.timeReceived.text = convertUnixToTime(data[position].date!!)

            messageReceiveHolder.itemView.setOnClickListener {

                ordersAlert(position)
            }


//==================================================================================================================FILE RECEIVE
        } else if (viewHolder.itemViewType == MESSAGE_FILE_RECEIVE_TYPE) {

            val messageReceiveFileViewHolder = viewHolder as MessageReceiveFileViewHolder


            if (data[position].loadingActive!!) {
                messageReceiveFileViewHolder.loadingView.visibility = View.VISIBLE
            } else {
                messageReceiveFileViewHolder.loadingView.visibility = View.INVISIBLE
            }


            if (data[position].percent != null && data[position].percent == 100) {

                messageReceiveFileViewHolder.progressBar.visibility = View.INVISIBLE
                messageReceiveFileViewHolder.fileCanSeeIV.visibility = View.VISIBLE
                messageReceiveFileViewHolder.multiplyIV.visibility = View.INVISIBLE
                messageReceiveFileViewHolder.downloadIV.visibility = View.INVISIBLE
                messageReceiveFileViewHolder.loadingView.visibility = View.INVISIBLE

            } else if (data[position].percent != null && data[position].percent!! < 100 && data[position].percent!! >= 0) {


                messageReceiveFileViewHolder.progressBar.visibility = View.VISIBLE
                messageReceiveFileViewHolder.fileCanSeeIV.visibility = View.INVISIBLE
                messageReceiveFileViewHolder.multiplyIV.visibility = View.VISIBLE
                messageReceiveFileViewHolder.downloadIV.visibility = View.INVISIBLE
                messageReceiveFileViewHolder.loadingView.visibility = View.INVISIBLE

                messageReceiveFileViewHolder.progressBar.progress = data[position].percent!!


            } else if (data[position].percent == null) {

                val root = Environment.getExternalStorageDirectory().toString()
                val myDir = File("$root/chatyar/${data[position].text}")

                if (myDir.exists()) {

                    messageReceiveFileViewHolder.fileCanSeeIV.visibility = View.VISIBLE
                    messageReceiveFileViewHolder.multiplyIV.visibility = View.INVISIBLE
                    messageReceiveFileViewHolder.downloadIV.visibility = View.INVISIBLE
                    messageReceiveFileViewHolder.progressBar.visibility = View.INVISIBLE
                    messageReceiveFileViewHolder.loadingView.visibility = View.INVISIBLE

                } else {

                    messageReceiveFileViewHolder.fileCanSeeIV.visibility = View.INVISIBLE
                    messageReceiveFileViewHolder.multiplyIV.visibility = View.INVISIBLE
                    messageReceiveFileViewHolder.downloadIV.visibility = View.VISIBLE
                    messageReceiveFileViewHolder.progressBar.visibility = View.INVISIBLE
                    messageReceiveFileViewHolder.loadingView.visibility = View.INVISIBLE

                }
            }


            messageReceiveFileViewHolder.fileCanSeeIV.setOnClickListener {

                if (data[position].filePath != null) {

                    val newIntent = Intent(Intent.ACTION_VIEW)
                    val mimeType = getMimeType(data[position].filePath!!)
                    newIntent.setDataAndType(
                        FileProvider.getUriForFile(
                            activity,
                            activity.packageName + ".provider",
                            File(data[position].filePath!!)
                        ), mimeType
                    )
                    newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    newIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

                    try {
                        activity.startActivity(newIntent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(activity, "فایل مورد نظر قابل نمایش نیست.", Toast.LENGTH_LONG).show()
                    }

                } else {

                    val root = Environment.getExternalStorageDirectory().toString()
                    val myDir = File("$root/chatyar/${data[position].text}")

                    if (myDir.exists()) {

                        val newIntent = Intent(Intent.ACTION_VIEW)
                        val mimeType = getMimeType(myDir.path)
                        newIntent.setDataAndType(
                            FileProvider.getUriForFile(
                                activity,
                                activity.packageName + ".provider", myDir
                            ), mimeType
                        )
                        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        newIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

                        try {
                            activity.startActivity(newIntent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(activity, "فایل مورد نظر قابل نمایش نیست.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }


            messageReceiveFileViewHolder.downloadIV.setOnClickListener {

                if (checkPermissions()) {

                    APIRepository.getFileLink(activity, data[position].docId, this, data[position].text!!)
                }

            }
//

            messageReceiveFileViewHolder.multiplyIV.setOnClickListener {

                if (data[position].msgtoken != null) {
                    APIRepository.cancelUpload(data[position].msgtoken!!)
                }

                if (data[position].docId != null) {

                    SharedPrefsTags.cancelDownload(data[position].docId)
                }

                messageReceiveFileViewHolder.fileCanSeeIV.visibility = View.INVISIBLE
                messageReceiveFileViewHolder.multiplyIV.visibility = View.INVISIBLE
                messageReceiveFileViewHolder.downloadIV.visibility = View.VISIBLE
                messageReceiveFileViewHolder.progressBar.visibility = View.INVISIBLE
                messageReceiveFileViewHolder.loadingView.visibility = View.INVISIBLE
            }

            messageReceiveFileViewHolder.downloadFileFrameL.setOnClickListener {

            }


            messageReceiveFileViewHolder.fileNameTV.text = data[position].text

//            messageReceiveFileViewHolder.downloadIV.setOnClickListener {
//
//                messageReceiveFileViewHolder.downloadIV.visibility = View.GONE
//                messageReceiveFileViewHolder.loadingView.visibility = View.VISIBLE
//
//            }
//
            var finalSize = "no size"




            if (data[position].size != null) {
                if (data[position].size!! >= 1024) {


                    if (data[position].size!! >= 1048576) {
                        finalSize = "${data[position].size!!.div(1048576)}  MB"
                    } else {
                        finalSize = "${data[position].size!!.div(1024)}  KB"
                    }
                } else {
                    finalSize = "${data[position].size!!}  Bytes"
                }
            }


            messageReceiveFileViewHolder.fileSizeTV.text = finalSize
//
//
//            messageReceiveFileViewHolder.timeReceived.text = convertUnixToTime(data[position].date!!)
//
//            messageReceiveFileViewHolder.itemView.setOnClickListener {
//                ordersAlert(position)
//            }


        } else if (viewHolder.itemViewType == MESSAGE_DATE_TYPE) {

            val messageDateHolder = viewHolder as MessageDateViewHolder

            messageDateHolder.messageDateTV.text = convertUnixToDate(data[position].date!!)

        }
    }


    class MessageSendViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {

        var sentText: TextView = mView.findViewById(R.id.sentText)
        var timeSent: TextView = mView.findViewById(R.id.timeSent)
        var tickSent: ImageView = mView.findViewById(R.id.tickSent)
        var senderTV: TextView = mView.findViewById(R.id.senderTV)
        var messageInnerSendLL: LinearLayout = mView.findViewById(R.id.messageInnerSendLL)

    }

    class MessageSendFileViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {


        var uploadFileFrameL: FrameLayout = mView.findViewById(R.id.uploadFileFrameL)
        var fileCanSeeIV: ImageView = mView.findViewById(R.id.fileCanSeeIV)
        var progressBar: ProgressBar = mView.findViewById(R.id.progressBar)
        var loadingView: ProgressBar = mView.findViewById(R.id.loadingView)
        var multiplyIV: ImageView = mView.findViewById(R.id.multiplyIV)
        var downloadIV: ImageView = mView.findViewById(R.id.downloadIV)
        var timeSent: TextView = mView.findViewById(R.id.timeSent)
        var fileNameTV: TextView = mView.findViewById(R.id.fileNameTV)
        var fileSizeTV: TextView = mView.findViewById(R.id.fileSizeTV)
        var senderTV: TextView = mView.findViewById(R.id.senderTV)
        var tickSent: ImageView = mView.findViewById(R.id.tickSent)
        var messageInnerSendLL: LinearLayout = mView.findViewById(R.id.messageInnerSendLL)

    }

    class MessageReceiveFileViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {

        var downloadFileFrameL: FrameLayout = mView.findViewById(R.id.downloadFileFrameL)
        var fileCanSeeIV: ImageView = mView.findViewById(R.id.fileCanSeeIV)
        var progressBar: ProgressBar = mView.findViewById(R.id.progressBar)
        var loadingView: ProgressBar = mView.findViewById(R.id.loadingView)
        var multiplyIV: ImageView = mView.findViewById(R.id.multiplyIV)
        var downloadIV: ImageView = mView.findViewById(R.id.downloadIV)
        var timeReceived: TextView = mView.findViewById(R.id.timeReceived)
        var messageInnerReceiveLL: LinearLayout = mView.findViewById(R.id.messageInnerReceiveLL)
        var fileNameTV: TextView = mView.findViewById(R.id.fileNameTV)
        var fileSizeTV: TextView = mView.findViewById(R.id.fileSizeTV)


    }

    class MessageReceiveViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {

        var receivedText: TextView = mView.findViewById(R.id.receivedText)
        var timeReceived: TextView = mView.findViewById(R.id.timeReceived)
        var messageInnerReceiveLL: LinearLayout = mView.findViewById(R.id.messageInnerReceiveLL)

    }

    class MessageDateViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {

        var messageDateTV: TextView = mView.findViewById(R.id.messageDateTV)

    }


    private fun convertUnixToDate(unixTime: Long): String {

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = unixTime * 1000L
        calendar.timeZone = java.util.TimeZone.getTimeZone(TimeZone.getDefault().id)

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())

        return "$day  $monthName"

    }

    private fun convertUnixToTime(unixTime: Long): String {

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = unixTime * 1000L
        calendar.timeZone = java.util.TimeZone.getTimeZone(TimeZone.getDefault().id)

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return String.format("%02d:%02d", hour, minute)

    }


    private fun ordersAlert(position: Int) {

        val items = arrayOf<CharSequence>("کپی")

        val builder = AlertDialog.Builder(activity)

        val title = TextView(activity)
        title.text = "پیام"
        title.setPadding(20, 20, 50, 0)
        title.gravity = Gravity.START
        title.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary))
        title.textSize = 22f
        builder.setCustomTitle(title)

        builder.setItems(items) { dialog, which ->

            if (which == 0) {

                val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(
                    data.get(position).chatid.toString(),
                    data.get(position).text
                )

                clipboard.primaryClip = clip
                Toast.makeText(activity, "متن انتخاب شده کپی شد.", Toast.LENGTH_SHORT).show()

            }
        }
        builder.show()

    }


    private fun fileExt(url: String): String? {

        var u = url

        if (url.indexOf("?") > -1) {
            u = url.substring(0, url.indexOf("?"));
        }

        if (u.lastIndexOf(".") == -1) {
            return null
        } else {
            var ext = u.substring(u.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();

        }
    }

    private fun getMimeType(url: String): String? {
        var type: String? = null
        var extension = MimeTypeMap.getFileExtensionFromUrl(url)

        if (extension == null) {

            extension = fileExt(url)
        }

        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)

        }
        return type
    }

    private fun checkPermissions(): Boolean {

        try {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    activity as Activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    2
                )

            } else {

                return true
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }


}