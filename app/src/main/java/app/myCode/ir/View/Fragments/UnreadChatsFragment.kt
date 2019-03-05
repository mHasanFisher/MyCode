package app.myCode.ir.View.Fragments


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import app.myCode.ir.Application.MyApp
import app.myCode.ir.Model.Models.MessageFromSocketModel

import app.myCode.ir.R
import app.myCode.ir.View.Adapters.AllChatsRVAdapter
import app.myCode.ir.View.Adapters.ChangeDomainRVAdapter
import app.myCode.ir.ViewModel.Classes.SharedPrefsTags
import app.myCode.ir.ViewModel.Interfaces.SocketReceiveMessage

class UnreadChatsFragment : Fragment(), SocketReceiveMessage {

    lateinit var unreadChatsView: View
    lateinit var unreadChatsRV: RecyclerView
    lateinit var unreadChatsRVLinearLM: LinearLayoutManager
    lateinit var unreadChatsRVAdapter: AllChatsRVAdapter

    lateinit var loadingView: ProgressBar

    private lateinit var connectionStatusTV: TextView
    private lateinit var changeDomainImageButt: ImageButton

    override fun onResume() {
        super.onResume()

        try {
            MyApp.instance.socketReceiveMessage = this

            if (MyApp.instance.mainPageDataset.size != 0) {

                checkWhichDomainIsSet()
            }

            if ((unreadChatsView.context as Activity).intent.extras != null) {

                MyApp.instance.whichSiteCurrentId = (unreadChatsView.context as Activity).intent.extras!!.getInt(
                    SharedPrefsTags.SITE_ID,
                    MyApp.instance.whichSiteCurrentId
                )
                SharedPrefsTags.getAllMessages(unreadChatsView.context, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        try {
            unreadChatsView = inflater.inflate(R.layout.fragment_unread_chats, container, false)

            connectionStatusTV = unreadChatsView.findViewById(R.id.connectionStatusTV)
            changeDomainImageButt = unreadChatsView.findViewById(R.id.changeDomainImageButt)
            loadingView = unreadChatsView.findViewById(R.id.loadingView)
            unreadChatsRV = unreadChatsView.findViewById(R.id.unreadChatsRV)
            unreadChatsRVLinearLM = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

            unreadChatsRVAdapter = AllChatsRVAdapter(unreadChatsView.context, ArrayList(), HashMap())

            unreadChatsRV.layoutManager = unreadChatsRVLinearLM
            unreadChatsRV.adapter = unreadChatsRVAdapter

            unreadChatsRV.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

            changeDomainImageButt.setOnClickListener {

                alertForChangeSite()
            }

            SharedPrefsTags.connectionStatus(unreadChatsView.context, connectionStatusTV)
        } catch (e: Exception) {

            e.printStackTrace()
        }

        return unreadChatsView
    }

    override fun onReceiveMessage(context: Context, message: MessageFromSocketModel?) {

        try {
            (unreadChatsView.context as Activity).runOnUiThread {


                SharedPrefsTags.connectionStatus(unreadChatsView.context, connectionStatusTV)

                loadingView.visibility = View.GONE

                if (message!!.type == "message") {

                    checkWhichDomainIsSet()

                } else if (message.type == "typingclient") {

                    unreadChatsRVAdapter.data?.forEach {

                        if (message.data?.message?.get(0)?.chatid == it.chatid) {

                            val holder: AllChatsRVAdapter.ViewHolder =
                                unreadChatsRV.findViewHolderForAdapterPosition(unreadChatsRVAdapter.data!!.indexOf(it)) as AllChatsRVAdapter.ViewHolder

                            val tempText = holder.itemTextTV.text
                            holder.itemTextTV.text = resources.getString(R.string.is_typing)

                            Handler().postDelayed({

                                holder.itemTextTV.text = tempText

                            }, 3000)

                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOpenSocket(connectedTo: String) {

        try {
            (unreadChatsView.context as Activity).runOnUiThread {

                SharedPrefsTags.connectionStatus(unreadChatsView.context, connectionStatusTV)
            }
            SharedPrefsTags.getAllMessages(unreadChatsView.context, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onCloseSocket(disconnectedFrom: String, code: Int, reason: String, remote: Boolean) {

        try {
            (unreadChatsView.context as Activity).runOnUiThread {

                SharedPrefsTags.connectionStatus(unreadChatsView.context, connectionStatusTV)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onErrorSocket(ex: Exception) {

        try {
            (unreadChatsView.context as Activity).runOnUiThread {

                SharedPrefsTags.connectionStatus(unreadChatsView.context, connectionStatusTV)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun alertForChangeSite() {

        try {

            val inflaterAlert = this.layoutInflater
            val viewAlertPass = inflaterAlert.inflate(R.layout.alert_dialog_change_site, null)
            val dialog: AlertDialog
            val changeSiteRV: RecyclerView = viewAlertPass.findViewById(R.id.changeSiteRV)
            var loadingRV: ProgressBar = viewAlertPass.findViewById(R.id.loadingRV)

            val builder = AlertDialog.Builder(unreadChatsView.context)

            builder.setView(viewAlertPass)

            builder.setNeutralButton(
                "بسته شو"
            )
            { dialog, which ->
                dialog.dismiss()
            }


            dialog = builder.create()

            MyApp.instance.siteList.forEach {

                if (it.accsiteId == MyApp.instance.whichSiteCurrentId) {

                    it.isActive = true
                    MyApp.instance.siteList.set(MyApp.instance.siteList.indexOf(it), it)
                } else {
                    it.isActive = false
                    MyApp.instance.siteList.set(MyApp.instance.siteList.indexOf(it), it)
                }

            }


            val changeSiteRVLinearLM = LinearLayoutManager(unreadChatsView.context, LinearLayoutManager.VERTICAL, false)
            val changeSiteRVAdapter =
                ChangeDomainRVAdapter(unreadChatsView.context, this, dialog, MyApp.instance.siteList)

            changeSiteRV.adapter = changeSiteRVAdapter
            changeSiteRV.layoutManager = changeSiteRVLinearLM


            changeSiteRV.addItemDecoration(
                DividerItemDecoration(
                    unreadChatsView.context,
                    DividerItemDecoration.VERTICAL
                )
            )


            dialog.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkWhichDomainIsSet() {

        try {
            val dataWithSite: MutableList<MessageFromSocketModel.MessageData.MessageModel> = ArrayList()
            val countList: HashMap<Int, Int> = HashMap()

            MyApp.instance.mainPageDataset.forEach {

                if(it.chatid != null) {
                    countList[it.chatid!!] = it.chatid!!
                }

                if (MyApp.instance.whichSiteCurrentId == it.accsiteId  || it.accsiteId == null) {

                    dataWithSite.add(it)

                }
            }

            val unreadDataset: MutableList<MessageFromSocketModel.MessageData.MessageModel> = ArrayList()
            var countDataset: HashMap<Int, Int> = HashMap()

            var tempMutable: List<MessageFromSocketModel.MessageData.MessageModel> = ArrayList()

            tempMutable = dataWithSite.sortedByDescending { it.date }.distinctBy { it.chatid }

//            var tempList: MutableList<MessageFromSocketModel.MessageData.MessageModel> = ArrayList()
//
//            dataWithSite.sortedByDescending { it.date }.forEach {
//
//                if (it.operatorid == null && it.solved == 0) {
//                    tempList.add(it)
//                }
//
//                else {
//
//                        tempList.removeAll { item -> item.chatid == it.chatid  }
//
//                }
//
//                if(it.accsiteId == null){
//                    tempList = ArrayList()
//                }
//
//
//            }

            for (item in countList.keys){

                val unread = SharedPrefsTags.unAnsweredMessages(item)
                countList[item] = unread

                if (unread == 0){
                    unreadDataset.removeAll { it -> item == it.chatid  }

                }
            }

            countDataset = countList


            tempMutable.forEach {

                if (countDataset.get(it.chatid) == null || (countDataset.get(it.chatid) != null && countDataset.get(it.chatid)!! < 1)
                ) {

                } else {
                    unreadDataset.add(it)
                }
            }


            unreadDataset.sortByDescending { it.date }

            unreadChatsRVAdapter.data = unreadDataset

            unreadChatsRVAdapter.mainData = countList


            unreadChatsRVAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
