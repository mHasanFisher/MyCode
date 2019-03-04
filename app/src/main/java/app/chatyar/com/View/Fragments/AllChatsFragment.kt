package app.chatyar.com.View.Fragments


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.TooltipCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import app.chatyar.com.Application.MyApp
import app.chatyar.com.Model.Models.MessageFromSocketModel

import app.chatyar.com.R
import app.chatyar.com.View.Activities.MainActivity
import app.chatyar.com.View.Adapters.AllChatsRVAdapter
import app.chatyar.com.View.Adapters.ChangeDomainRVAdapter
import app.chatyar.com.ViewModel.Classes.SharedPrefsTags
import app.chatyar.com.ViewModel.Interfaces.SocketReceiveMessage

class AllChatsFragment : Fragment(), SocketReceiveMessage {

    private lateinit var allChatsView: View
    private lateinit var allChatsRV: RecyclerView
    private lateinit var allChatsRVAdapter: AllChatsRVAdapter
    private lateinit var allChatsRVLinearLM: LinearLayoutManager
    private var dataset: MutableList<MessageFromSocketModel.MessageData.MessageModel>? = ArrayList()

    private lateinit var loadingView: ProgressBar
    private lateinit var connectionStatusTV: TextView
    private lateinit var changeDomainImageButt: ImageButton

    override fun onResume() {
        super.onResume()

        try {
            MyApp.instance.socketReceiveMessage = this

            if (MyApp.instance.mainPageDataset.size != 0) {

                checkWhichDomainIsSet()
            }

            if ((allChatsView.context as MainActivity).intent.extras != null) {

                MyApp.instance.whichSiteCurrentId = (allChatsView.context as Activity).intent.extras!!.getInt(
                    SharedPrefsTags.SITE_ID,
                    MyApp.instance.whichSiteCurrentId
                )
                SharedPrefsTags.getAllMessages(allChatsView.context, false)
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
            allChatsView = inflater.inflate(R.layout.fragment_all_chats, container, false)

            allChatsRV = allChatsView.findViewById(R.id.allChatsRV)
            loadingView = allChatsView.findViewById(R.id.loadingView)
            connectionStatusTV = allChatsView.findViewById(R.id.connectionStatusTV)
            changeDomainImageButt = allChatsView.findViewById(R.id.changeDomainImageButt)

            TooltipCompat.setTooltipText(changeDomainImageButt, resources.getString(R.string.changeDomainToolTip));

            allChatsRVLinearLM = LinearLayoutManager(allChatsView.context, LinearLayoutManager.VERTICAL, false)
            allChatsRVAdapter = AllChatsRVAdapter(allChatsView.context, MyApp.instance.mainPageDataset, HashMap())

            allChatsRV.layoutManager = allChatsRVLinearLM
            allChatsRV.adapter = allChatsRVAdapter


            changeDomainImageButt.setOnClickListener {

                alertForChangeSite()
            }

            allChatsRV.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

            SharedPrefsTags.connectionStatus(allChatsView.context, connectionStatusTV)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return allChatsView
    }


    override fun onReceiveMessage(context: Context, message: MessageFromSocketModel?) {

        try {
            (allChatsView.context as Activity).runOnUiThread {

                SharedPrefsTags.connectionStatus(allChatsView.context, connectionStatusTV)
                loadingView.visibility = View.GONE

                if (message!!.type == "message") {

                    checkWhichDomainIsSet()


                } else if (message.type == "typingclient") {

                    allChatsRVAdapter.data?.forEach {

                        if (message.data?.message?.get(0)?.chatid == it.chatid && allChatsRV.findViewHolderForAdapterPosition(
                                allChatsRVAdapter.data!!.indexOf(it)
                            ) != null
                        ) {

                            val holder: AllChatsRVAdapter.ViewHolder =
                                allChatsRV.findViewHolderForAdapterPosition(allChatsRVAdapter.data!!.indexOf(it)) as AllChatsRVAdapter.ViewHolder

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
            (allChatsView.context as Activity).runOnUiThread {

                SharedPrefsTags.connectionStatus(allChatsView.context, connectionStatusTV)
            }

            SharedPrefsTags.getAllMessages(allChatsView.context, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCloseSocket(disconnectedFrom: String, code: Int, reason: String, remote: Boolean) {

        try {
            (allChatsView.context as Activity).runOnUiThread {
                SharedPrefsTags.connectionStatus(allChatsView.context, connectionStatusTV)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onErrorSocket(ex: Exception) {

        try {
            (allChatsView.context as Activity).runOnUiThread {
                SharedPrefsTags.connectionStatus(allChatsView.context, connectionStatusTV)
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
//            var loadingRV: ProgressBar = viewAlertPass.findViewById(R.id.loadingRV)

            val builder = AlertDialog.Builder(allChatsView.context)

            builder.setView(viewAlertPass)

            builder.setNeutralButton(
                "بسته شو"
            )
            { dialog, _ ->
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


            val changeSiteRVLinearLM = LinearLayoutManager(allChatsView.context, LinearLayoutManager.VERTICAL, false)
            val changeSiteRVAdapter = ChangeDomainRVAdapter(allChatsView.context, this, dialog, MyApp.instance.siteList)

            changeSiteRV.adapter = changeSiteRVAdapter
            changeSiteRV.layoutManager = changeSiteRVLinearLM


            changeSiteRV.addItemDecoration(DividerItemDecoration(allChatsView.context, DividerItemDecoration.VERTICAL))


            dialog.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
//    1548765989
    private fun checkWhichDomainIsSet() {

        try {
            val dataWithSite: MutableList<MessageFromSocketModel.MessageData.MessageModel> = ArrayList()

            val countList: HashMap<Int, Int> = HashMap()

            MyApp.instance.mainPageDataset.forEach {

                if(it.chatid != null) {
                    countList[it.chatid!!] = it.chatid!!
                }

                if (MyApp.instance.whichSiteCurrentId == it.accsiteId || it.accsiteId == null) {

                    dataWithSite.add(it)
                }
            }

            allChatsRVAdapter.data = dataWithSite.sortedByDescending { it.date }.distinctBy { it.chatid }

//            var tempList: MutableList<MessageFromSocketModel.MessageData.MessageModel> = ArrayList()
//            dataWithSite.sortedBy { it.date }.forEach {
//
//                if (it.operatorid == null && it.solved == 0) {
//                    tempList.add(it)
//                }
//
//                if(it.accsiteId == null ){
//                    tempList = ArrayList()
//                }
//            }


            for (item in countList.keys){

                countList[item] = SharedPrefsTags.unAnsweredMessages(item)

            }


//            allChatsRVAdapter.mainData = tempList.groupingBy { it.chatid }.eachCount()
            allChatsRVAdapter.mainData = countList


            allChatsRVAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
