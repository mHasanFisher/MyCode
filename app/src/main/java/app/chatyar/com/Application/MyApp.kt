package app.chatyar.com.Application

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.app.NotificationCompat
import app.chatyar.com.Model.Models.AccountModel
import app.chatyar.com.Model.Models.MainPageModel
import app.chatyar.com.Model.Models.MessageFromSocketModel
import app.chatyar.com.R
import app.chatyar.com.View.Activities.MainActivity
import app.chatyar.com.ViewModel.Classes.SharedPrefsTags
import app.chatyar.com.ViewModel.Interfaces.SocketReceiveMessage
import org.java_websocket.client.WebSocketClient

class MyApp : Application(), Application.ActivityLifecycleCallbacks {


    var isMainFinished = false
    var mainPageDataset: MutableList<MessageFromSocketModel.MessageData.MessageModel> = ArrayList()
    var connectionToSocketStatus: Int? = SharedPrefsTags.STATUS_WAITING_FOR_NETWORK
    var webSocketClient: WebSocketClient? = null

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    var currentActivity: FragmentActivity? = null
    lateinit var socketReceiveMessage: SocketReceiveMessage

     var inboxStyles: HashMap<Int, NotificationCompat.InboxStyle> = HashMap()
     var inboxStyleOld: HashMap<Int, NotificationCompat.InboxStyle> = HashMap()
     var mBuilder: NotificationCompat.Builder? = null
     var notificationManager: NotificationManager? = null
    var channelId: String = "0"
     var emptyIntent: Intent? = null
     var pendingIntent: PendingIntent? = null
    var notificationsIds: HashMap<Int, Int> = HashMap()
    var currentChatId = 0

    var siteList: MutableList<MainPageModel.DataMainPageModel.SiteModel> = ArrayList()
    var whichSiteCurrentId = 0

    var operatorList: MutableList<MainPageModel.DataMainPageModel.OperatorModel> = ArrayList()

    var badgeCount = 0

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: MyApp
            private set

    }


    override fun onCreate() {
        super.onCreate()

        instance = this
        sharedPreferences = this.getSharedPreferences(SharedPrefsTags.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        editor = getSharedPreferences(SharedPrefsTags.SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit()



        registerActivityLifecycleCallbacks(this)

    }


    override fun onActivityPaused(activity: Activity?) {
    }

    override fun onActivityResumed(activity: Activity?) {

        currentActivity = activity as FragmentActivity
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }


    fun initializeNotification() {

        notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager



        channelId = "channel-01"
        val channelName = "Channel Name"
        var importance = 0

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                channelId, channelName, importance
            )

            notificationManager!!.createNotificationChannel(mChannel)
        }

//        inboxStyleOld = NotificationCompat.InboxStyle()

        val bMap = BitmapFactory.decodeResource(applicationContext.resources, R.mipmap.icon_main_logo)

        var imgRes = 0
        imgRes = R.mipmap.icon_logo_notif

        emptyIntent = Intent(applicationContext, MainActivity::class.java)
        pendingIntent = PendingIntent.getActivity(applicationContext, 0, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        var subTitle = ""
        MyApp.instance.siteList.forEach {

            if(whichSiteCurrentId == it.accsiteId){
                subTitle = it.accsiteTitle!!

                emptyIntent!!.putExtra(SharedPrefsTags.SITE_ID, it.accsiteId!!)
            }


        }


        mBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(imgRes)
            .setLargeIcon(bMap)
            .setAutoCancel(true)
            .setContentTitle(resources.getString(R.string.app_name))
            .setGroup(whichSiteCurrentId.toString())
            .setSubText(subTitle)
            .setGroupSummary(true)
            .setContentIntent(pendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))




    }


}