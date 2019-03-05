package app.myCode.ir.ViewModel.Services


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import app.myCode.ir.Application.MyApp
import app.myCode.ir.Model.Models.MainPageModel
import app.myCode.ir.Model.Models.MessageFromSocketModel
import app.myCode.ir.R
import app.myCode.ir.View.Activities.MainActivity
import app.myCode.ir.ViewModel.Classes.SharedPrefsTags
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.leolin.shortcutbadger.ShortcutBadger

class MyFirebaseMessagingService : FirebaseMessagingService() {

    var counter = 1
    val channelId = "channel-01"
    val channelName = "Channel Name"
    var importance = 0


    override fun onNewToken(token: String?) {
        super.onNewToken(token)

        MyApp.instance.editor.putString(SharedPrefsTags.FIREBASE_TOKEN, token).apply()
    }


    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)


        Log.e("point", "1111111111111111111111111111111111111111111")

        val map = message!!.data
        val values = map.values.first()
        val data: MessageFromSocketModel = Gson().fromJson(values.toString().trim(), MessageFromSocketModel::class.java)

        if (data.type == "message") {

            Log.e("point", "2222222222222222222222222222222222222222222")

            ShortcutBadger.applyCount(this, MyApp.instance.badgeCount++);

            if (MyApp.instance.sharedPreferences.getBoolean(SharedPrefsTags.NOTIFICATION_STATUS, true)) {

                Log.e("point", "33333333333333333333333333333333333333333")

                val messageItems = data.data!!.message

                val bMap = BitmapFactory.decodeResource(applicationContext.resources, R.mipmap.icon_main_logo)

                var imgRes = 0
                imgRes = R.mipmap.icon_logo_notif

                val nameOfUserToNotify = " گفتگوی ${messageItems?.get(0)?.chatname} "
                val textOfUserToNotify = messageItems?.get(0)?.text.toString()
                val chatIdToNotify = messageItems?.get(0)?.chatid!!
                val accSiteId = messageItems[0].accsiteId ?: 0

//        val md5 = 123

                var existed = false
                for(i in MyApp.instance.inboxStyles.keys){

                    if(chatIdToNotify == i){

                        existed = true
                        break
                    }

                }

                if(!existed){

                    MyApp.instance.inboxStyles[chatIdToNotify] = NotificationCompat.InboxStyle()

                }


                existed = false
                for(i in MyApp.instance.inboxStyleOld.keys){

                    if(accSiteId == i){

                        existed = true
                        break
                    }

                }

                if(!existed){

                    MyApp.instance.inboxStyleOld[accSiteId] = NotificationCompat.InboxStyle()

                }

                    MyApp.instance.notificationsIds[chatIdToNotify] = accSiteId


                if (MyApp.instance.emptyIntent != null && MyApp.instance.pendingIntent != null && MyApp.instance.mBuilder != null
                    && MyApp.instance.notificationManager != null && MyApp.instance.currentChatId != chatIdToNotify
                ) {


                    Log.e("point", "444444444444444444444444444444444444444444")


                    if (Build.VERSION.SDK_INT >= 24) {


                        MyApp.instance.emptyIntent = Intent(applicationContext, MainActivity::class.java)
                        MyApp.instance.emptyIntent!!.putExtra(SharedPrefsTags.SITE_ID, accSiteId)
                        MyApp.instance.pendingIntent = PendingIntent.getActivity(
                            applicationContext,
                            0,
                            MyApp.instance.emptyIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )

                        var subTitle = ""


                        MyApp.instance.siteList.forEach {

                            if (accSiteId == it.accsiteId) {
                                MyApp.instance.mBuilder!!.setGroup(it.accsiteId.toString())
                                MyApp.instance.mBuilder!!.setSubText(it.accsiteTitle)
                                subTitle = it.accsiteTitle!!
                            }
                        }


                        val newBuilderGroup = NotificationCompat.Builder(applicationContext, MyApp.instance.channelId)
                            // Other properties
                            .setGroupSummary(false)
                            .setGroup(accSiteId.toString())
                            .setSubText(subTitle)
                            .setSmallIcon(imgRes)
                            .setLargeIcon(bMap)
                            .setAutoCancel(true)
                            .setContentTitle(nameOfUserToNotify)
                            .setContentText(textOfUserToNotify)
                            .setContentIntent(MyApp.instance.pendingIntent)

                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

                        MyApp.instance.inboxStyles[chatIdToNotify]!!.addLine(textOfUserToNotify)
                        MyApp.instance.inboxStyles[chatIdToNotify]!!.setBigContentTitle(nameOfUserToNotify)
                        newBuilderGroup.setStyle(MyApp.instance.inboxStyles[chatIdToNotify])



                        MyApp.instance.notificationManager!!.notify(
                            accSiteId.toString(),
                            accSiteId,
                            MyApp.instance.mBuilder!!.build()
                        )
                        MyApp.instance.notificationManager!!.notify(
                            accSiteId.toString(),
                            chatIdToNotify,
                            newBuilderGroup.build()
                        )

                    } else {

                        MyApp.instance.emptyIntent = Intent(applicationContext, MainActivity::class.java)
                        MyApp.instance.emptyIntent!!.putExtra(SharedPrefsTags.SITE_ID, accSiteId)
                        MyApp.instance.pendingIntent = PendingIntent.getActivity(
                            applicationContext,
                            0,
                            MyApp.instance.emptyIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )

                        MyApp.instance.inboxStyleOld[accSiteId]!!.addLine("$nameOfUserToNotify : $textOfUserToNotify")
                        MyApp.instance.mBuilder!!.setContentText("New messages")
                        MyApp.instance.inboxStyleOld[accSiteId]!!.setSummaryText("New messages")
                        MyApp.instance.inboxStyleOld[accSiteId]!!.setBigContentTitle(resources.getString(R.string.app_name))
                        MyApp.instance.mBuilder!!.setStyle(MyApp.instance.inboxStyleOld[accSiteId])
                        MyApp.instance.mBuilder!!.setContentIntent(MyApp.instance.pendingIntent!!)

                        MyApp.instance.siteList.forEach {

                            if (accSiteId == it.accsiteId) {

                                MyApp.instance.mBuilder!!.setGroup(it.accsiteId.toString())
                                MyApp.instance.mBuilder!!.setSubText(it.accsiteTitle)
                            }

                        }

                        MyApp.instance.notificationManager!!.notify(
                            accSiteId.toString(),
                            accSiteId,
                            MyApp.instance.mBuilder!!.build()
                        )

                    }
                } else if (MyApp.instance.emptyIntent != null && MyApp.instance.pendingIntent != null && MyApp.instance.mBuilder != null
                    && MyApp.instance.notificationManager != null && MyApp.instance.currentChatId == chatIdToNotify
                ) {


                } else {

                    Log.e("point", "55555555555555555555555555555555555")

                    var siteTitle = "non"

                    if (counter == 1) {

                        counter -= 1

//                        MyApp.instance.inboxStyleOld = NotificationCompat.InboxStyle()

                        MyApp.instance.sharedPreferences = this.getSharedPreferences(SharedPrefsTags.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
                        MyApp.instance.editor = getSharedPreferences(SharedPrefsTags.SHARED_PREFS_NAME, Context.MODE_PRIVATE).edit()

                        MyApp.instance.siteList = Gson().fromJson(MyApp.instance.sharedPreferences.getString(SharedPrefsTags.ALL_SITES_TITLE, ""),
                            object: TypeToken<MutableList<MainPageModel.DataMainPageModel.SiteModel>>(){}.type)

                        MyApp.instance.siteList.forEach{

                            if(it.accsiteId == accSiteId){
                                siteTitle = it.accsiteTitle.toString()
                            }

                        }
                        Log.e("point", siteTitle)

                        MyApp.instance.notificationManager =
                            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            importance = NotificationManager.IMPORTANCE_HIGH
                        }

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            val mChannel = NotificationChannel(
                                channelId, channelName, importance
                            )

                            MyApp.instance.notificationManager!!.createNotificationChannel(mChannel)
                        }



                        val bMap = BitmapFactory.decodeResource(applicationContext.resources, R.mipmap.icon_main_logo)

                        var imgRes = 0
                        imgRes = R.mipmap.icon_logo_notif

                        MyApp.instance.emptyIntent = Intent(applicationContext, MainActivity::class.java)
                        MyApp.instance.pendingIntent = PendingIntent.getActivity(
                            applicationContext,
                            0,
                            MyApp.instance.emptyIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )

                        MyApp.instance.emptyIntent!!.putExtra(SharedPrefsTags.SITE_ID, accSiteId)

                        MyApp.instance.mBuilder = NotificationCompat.Builder(applicationContext, channelId)
                            .setSmallIcon(imgRes)
                            .setLargeIcon(bMap)
                            .setAutoCancel(true)
                            .setContentTitle(resources.getString(R.string.app_name))
                            .setGroup(accSiteId.toString())
                            .setSubText(siteTitle)
                            .setGroupSummary(true)
                            .setContentIntent(MyApp.instance.pendingIntent)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

                    }

                    if (Build.VERSION.SDK_INT >= 24) {


                        MyApp.instance.mBuilder!!.setGroup(accSiteId.toString())
                        MyApp.instance.mBuilder!!.setSubText(siteTitle)


                        val newBuilderGroup = NotificationCompat.Builder(applicationContext, channelId)
                            // Other properties
                            .setGroupSummary(false)
                            .setGroup(accSiteId.toString())
                            .setSubText(siteTitle)
                            .setSmallIcon(imgRes)
                            .setLargeIcon(bMap)
                            .setAutoCancel(true)
                            .setContentTitle(nameOfUserToNotify)
                            .setContentText(textOfUserToNotify)
                            .setContentIntent(MyApp.instance.pendingIntent)

                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

                        MyApp.instance.inboxStyles[chatIdToNotify]!!.addLine(textOfUserToNotify)
                        MyApp.instance.inboxStyles[chatIdToNotify]!!.setBigContentTitle(nameOfUserToNotify)
                        newBuilderGroup.setStyle(MyApp.instance.inboxStyles[chatIdToNotify]!!)



                        MyApp.instance.notificationManager!!.notify(
                            accSiteId.toString(),
                            accSiteId,
                            MyApp.instance.mBuilder!!.build()
                        )
                        MyApp.instance.notificationManager!!.notify(
                            accSiteId.toString(),
                            chatIdToNotify,
                            newBuilderGroup.build()
                        )

                    } else {

                        val emptyIntent = Intent(applicationContext, MainActivity::class.java)
                        emptyIntent.putExtra(SharedPrefsTags.SITE_ID, accSiteId)
                        val pendingIntent = PendingIntent.getActivity(
                            applicationContext,
                            0,
                            emptyIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )

                        MyApp.instance.inboxStyleOld[accSiteId]!!.addLine("$nameOfUserToNotify : $textOfUserToNotify")
                        MyApp.instance.mBuilder!!.setContentText("New messages")
                        MyApp.instance.inboxStyleOld[accSiteId]!!.setSummaryText("New messages")
                        MyApp.instance.inboxStyleOld[accSiteId]!!.setBigContentTitle(resources.getString(R.string.app_name))
                        MyApp.instance.mBuilder!!.setStyle(MyApp.instance.inboxStyleOld[accSiteId]!!)
                        MyApp.instance.mBuilder!!.setContentIntent(MyApp.instance.pendingIntent!!)

                        MyApp.instance.mBuilder!!.setGroup(accSiteId.toString())
                        MyApp.instance.mBuilder!!.setSubText(siteTitle)


                        MyApp.instance.notificationManager!!.notify(
                            accSiteId.toString(),
                            accSiteId,
                            MyApp.instance.mBuilder!!.build()
                        )

                    }


                }
            }
        }
    }

}