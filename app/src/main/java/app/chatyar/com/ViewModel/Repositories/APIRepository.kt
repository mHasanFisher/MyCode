package app.chatyar.com.ViewModel.Repositories

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import app.chatyar.com.Application.MyApp
import app.chatyar.com.BuildConfig
import app.chatyar.com.Model.Models.*
import app.chatyar.com.View.Activities.LuncherActivity
import app.chatyar.com.View.Activities.MainActivity
import app.chatyar.com.View.Activities.SigninActivity
import app.chatyar.com.View.Adapters.ChatRVAdapter
import app.chatyar.com.View.Adapters.ProfileInfoRVAdapter
import app.chatyar.com.ViewModel.Classes.APIClient
import app.chatyar.com.ViewModel.Classes.SharedPrefsTags
import app.chatyar.com.ViewModel.Interfaces.Webservice
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object APIRepository {


    private var webservice: Webservice = APIClient.getClient(SharedPrefsTags.BASE_URL).create(Webservice::class.java)

    var callBackUploadFile: HashMap<String, Call<UploadAvatarResModel>> = HashMap()


    fun postLogin(username: String, pass: String, loadingView: ProgressBar) {


        try {
            loadingView.visibility = View.VISIBLE
            var fireStatus = "0"

            if (MyApp.instance.sharedPreferences.getString(SharedPrefsTags.FIREBASE_TOKEN, "0") != "0") {
                fireStatus = "1"
            }

            webservice.postLogin(
                username, pass, "android", BuildConfig.VERSION_CODE.toString(), fireStatus,
                MyApp.instance.sharedPreferences.getString(SharedPrefsTags.FIREBASE_TOKEN, "0")!!
            ).enqueue(object : Callback<LoginModel> {

                override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {

                    loadingView.visibility = View.GONE

                    if (!response.body()!!.error!!) {

                        val cookie = response.headers().get("authorization")
                        MyApp.instance.editor.putString(SharedPrefsTags.MY_SESSION, cookie).apply()

                        MyApp.instance.editor.putInt(
                            SharedPrefsTags.OPERATOR_ID,
                            response.body()!!.data!![0].accountId!!
                        ).apply()

                        MyApp.instance.editor.putString(
                            SharedPrefsTags.OPERATOR_USERNAME,
                            response.body()!!.data!![0].accountUsername!!
                        ).apply()

                        val intent = Intent(loadingView.context, MainActivity::class.java)
                        loadingView.context.startActivity(intent)

                        (loadingView.context as SigninActivity).finish()
                    } else {

                        SharedPrefsTags.notConnectedAlertWithText(loadingView.context, response.body()!!.message!!)
                    }
                }

                override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                    loadingView.visibility = View.GONE


                    SharedPrefsTags.notConnectedAlertWithText(loadingView.context, t.cause.toString())

                    t.printStackTrace()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getMainPage(context: Context) {


        try {
            webservice.getMainPage(
                MyApp.instance.sharedPreferences.getString(
                    SharedPrefsTags.MY_SESSION,
                    SharedPrefsTags.MY_SESSION_DEFAULT
                )!!
            ).enqueue(object : Callback<MainPageModel> {

                override fun onResponse(call: Call<MainPageModel>, response: Response<MainPageModel>) {

                    if (response.body() != null && !response.body()!!.error!!) {


                        MyApp.instance.editor.putString(
                            SharedPrefsTags.OPERATOR_FIRSTNAME,
                            response.body()!!.data!!.profile!!.accountFirstname
                        )
                        MyApp.instance.editor.putString(
                            SharedPrefsTags.OPERATOR_LASTNAME,
                            response.body()!!.data!!.profile!!.accountLastname
                        )

                        MyApp.instance.editor.putString(
                            SharedPrefsTags.AVATAR_URL_SMALL,
                            response.body()!!.data!!.profile!!.accountAvatar!!.small)

                        MyApp.instance.editor.putString(
                            SharedPrefsTags.AVATAR_URL_MEDIUM,
                            response.body()!!.data!!.profile!!.accountAvatar!!.medium
                        )

                        MyApp.instance.editor.putString(
                            SharedPrefsTags.AVATAR_URL_LARGE,
                            response.body()!!.data!!.profile!!.accountAvatar!!.large)

                        MyApp.instance.editor.putString(
                            SharedPrefsTags.AVATAR_URL_XLARGE,
                            response.body()!!.data!!.profile!!.accountAvatar!!.xlarge
                        )




                        MyApp.instance.editor.putInt(
                            SharedPrefsTags.OPERATOR_ID,
                            response.body()!!.data!!.profile!!.accountId!!
                        )
                        MyApp.instance.editor.putString(
                            SharedPrefsTags.OPERATOR_USERNAME,
                            response.body()!!.data!!.profile!!.accountUsername
                        )

                        MyApp.instance.editor.apply()



                        if (response.body()!!.data!!.site != null && response.body()!!.data!!.site!!.size != 0) {

                            MyApp.instance.siteList = ArrayList()

                            MyApp.instance.siteList.addAll(response.body()!!.data!!.site!!)
                            MyApp.instance.editor.putString(
                                SharedPrefsTags.ALL_SITES_TITLE,
                                Gson().toJson(MyApp.instance.siteList)
                            ).apply()


                            if (MyApp.instance.whichSiteCurrentId == 0) {

                                val lastSiteIdSaved =
                                    MyApp.instance.sharedPreferences.getInt(SharedPrefsTags.LAST_SITE_ID, -1)
                                var exist = false

                                for (it in MyApp.instance.siteList) {

                                    if (lastSiteIdSaved == it.accsiteId) {

                                        exist = true
                                        break
                                    }
                                }

                                if (exist) {

                                    MyApp.instance.whichSiteCurrentId = lastSiteIdSaved
                                    MyApp.instance.initializeNotification()
                                } else {
                                    MyApp.instance.whichSiteCurrentId = MyApp.instance.siteList[0].accsiteId!!
                                    MyApp.instance.initializeNotification()
                                }
                            }

                        }

                        else {
                            SharedPrefsTags.notConnectedAlertWithText(context, "There is no site!")
                        }

                        if (response.body()!!.data!!.operator != null) {
                            MyApp.instance.operatorList = response.body()!!.data!!.operator!!
                        }
                        SharedPrefsTags.connectToSock(context)

                    } else if (response.body() != null) {

                        SharedPrefsTags.notConnectedAlertWithText(context, response.body()!!.message!!)

                    }

                }

                override fun onFailure(call: Call<MainPageModel>, t: Throwable) {

                    SharedPrefsTags.notConnectedAlertWithText(context, t.message.toString())

                    t.printStackTrace()
                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun changePass(context: Context, oldPass: String, newPass: String) {


        try {
            webservice.changePass(
                MyApp.instance.sharedPreferences.getString(
                    SharedPrefsTags.MY_SESSION,
                    SharedPrefsTags.MY_SESSION_DEFAULT
                )!!,
                oldPass, newPass
            ).enqueue(object : Callback<ChangePassResModel> {

                override fun onResponse(call: Call<ChangePassResModel>, response: Response<ChangePassResModel>) {

                    if (!response.body()!!.error!!) {

                        Toast.makeText((context as Activity), response.body()!!.message, Toast.LENGTH_LONG).show()
                        context.finish()
                    } else {

                        SharedPrefsTags.notConnectedAlertWithText(context, response.body()!!.message!!)
                    }
                }

                override fun onFailure(call: Call<ChangePassResModel>, t: Throwable) {

                    SharedPrefsTags.notConnectedAlertWithText(context, t.message.toString())

                    t.printStackTrace()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getFileLink(context: Context, docId: String?, chatRVAdapter: ChatRVAdapter, fileName: String) {

        try {
            val data: MutableList<OneChatDataModel> = ArrayList()
            data.addAll(chatRVAdapter.data)

            for (it in data) {

                if (it.docId != null && it.docId == docId) {

                    it.loadingActive = true
                    chatRVAdapter.data[chatRVAdapter.data.indexOf(it)] = it

                    (context as Activity).runOnUiThread(Runnable {

                        chatRVAdapter.notifyItemChanged(chatRVAdapter.data.indexOf(it))
                    })

                    break
                }
            }

            webservice.getFileLink(docId!!).enqueue(object : Callback<MainPageModel> {

                override fun onResponse(call: Call<MainPageModel>, response: Response<MainPageModel>) {

                    if (!response.body()!!.error!!) {

                        for (it in data) {

                            if (it.docId != null && it.docId == docId) {

                                it.loadingActive = false
                                chatRVAdapter.data[chatRVAdapter.data.indexOf(it)] = it
                                (context as Activity).runOnUiThread(Runnable {

                                    chatRVAdapter.notifyItemChanged(chatRVAdapter.data.indexOf(it))
                                })
                                break
                            }
                        }

                        SharedPrefsTags.downloadFile(
                            context,
                            docId,
                            chatRVAdapter,
                            response.body()!!.data!!.link!!,
                            fileName)

                    } else {

                        SharedPrefsTags.notConnectedAlertWithText(context, response.body()!!.message!!)
                    }
                }

                override fun onFailure(call: Call<MainPageModel>, t: Throwable) {

                    SharedPrefsTags.notConnectedAlertWithText(context, t.message.toString())

                    t.printStackTrace()
                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun uploadAvatar(context: Context, bodyToUp: MultipartBody.Part, imageView: ImageView, path: Uri) {

        try {
            val auth = RequestBody.create(
                MediaType.parse("text/plain"), MyApp.instance.sharedPreferences.getString(
                    SharedPrefsTags.MY_SESSION,
                    SharedPrefsTags.MY_SESSION_DEFAULT
                )!!
            )

            webservice.uploadAvatar(
                auth,
                bodyToUp
            ).enqueue(object : Callback<UploadAvatarResModel> {
                override fun onResponse(call: Call<UploadAvatarResModel>, response: Response<UploadAvatarResModel>) {

                    try {

                        if (response.body()!!.status == "ok") {

                            MyApp.instance.editor.putString(
                                SharedPrefsTags.AVATAR_URL_SMALL,
                                response.body()!!.result!!.small
                            )

                            MyApp.instance.editor.putString(
                                SharedPrefsTags.AVATAR_URL_MEDIUM,
                                response.body()!!.result!!.medium
                            )

                            MyApp.instance.editor.putString(
                                SharedPrefsTags.AVATAR_URL_LARGE,
                                response.body()!!.result!!.large
                            )

                            MyApp.instance.editor.putString(
                                SharedPrefsTags.AVATAR_URL_XLARGE,
                                response.body()!!.result!!.xlarge
                            )

                            MyApp.instance.editor.apply()

                            val selectedImage = BitmapFactory.decodeFile(path.path)

                            imageView.setImageBitmap(selectedImage)

                        } else {

                            SharedPrefsTags.notConnectedAlertWithText(context, response.body()!!.message!!)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()

                    }
                }

                override fun onFailure(call: Call<UploadAvatarResModel>, t: Throwable) {

                    SharedPrefsTags.notConnectedAlertWithText(context, t.message.toString())
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun uploadFile(activity: Activity, fileToUpload: MultipartBody.Part, message: String, chatId: Int, chatname: Int, siteId: Int,
        messageToken: String, fileSize: Long) {

        try {
            val auth = RequestBody.create(
                MediaType.parse("text/plain"), MyApp.instance.sharedPreferences.getString(
                    SharedPrefsTags.MY_SESSION,
                    SharedPrefsTags.MY_SESSION_DEFAULT
                )!!
            )

            callBackUploadFile[messageToken] = webservice.uploadFile(
                auth,
                fileToUpload
            )

            callBackUploadFile[messageToken]!!.enqueue(object : Callback<UploadAvatarResModel> {
                override fun onResponse(call: Call<UploadAvatarResModel>, response: Response<UploadAvatarResModel>) {

                    try {

                        if (response.body()!!.status == "ok") {

                            Toast.makeText(activity, response.body()!!.message, Toast.LENGTH_LONG).show()

                            val docId = "${response.body()!!.result!!.name}.${response.body()!!.result!!.ext}"

                            SharedPrefsTags.sendMessageToSocket(
                                activity, message, chatId, chatname, siteId, messageToken,
                                "file", fileSize, docId
                            )

                            val messages: MessageFromSocketModel.MessageData.MessageModel =
                                MessageFromSocketModel().MessageData().MessageModel()
                            messages.messageModel(
                                chatId, chatname,null, "file", null, null, message, System.currentTimeMillis() / 1000,
                                MyApp.instance.sharedPreferences.getInt(SharedPrefsTags.OPERATOR_ID, 0), siteId, null, null, messageToken, fileSize
                            )
                            MyApp.instance.mainPageDataset.add(messages)

                        } else {

                            SharedPrefsTags.notConnectedAlertWithText(activity, response.body()!!.message!!)
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<UploadAvatarResModel>, t: Throwable) {

                    if (call.isCanceled) {
                        SharedPrefsTags.notConnectedAlertWithText(activity, "Upload cancelled")
                    } else {
                        SharedPrefsTags.notConnectedAlertWithText(activity, t.message.toString())
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelUpload(messageToken: String) {

        if (callBackUploadFile[messageToken] != null) {
            callBackUploadFile[messageToken]!!.cancel()
        }
    }

    fun getClientInfo(context: Context, clientId: Int, profileInfoRVAdapter: ProfileInfoRVAdapter){


        try {
            webservice.getClientInfo(
                MyApp.instance.sharedPreferences.getString(
                    SharedPrefsTags.MY_SESSION,
                    SharedPrefsTags.MY_SESSION_DEFAULT
                )!!,
                clientId
            ).enqueue(object : Callback<ClientInfoModel> {

                override fun onResponse(call: Call<ClientInfoModel>, response: Response<ClientInfoModel>) {

                    if (!response.body()!!.error!!) {

                        val dataset: MutableList<ClientInfoModel.ClientDataModel> = response.body()!!.data!!
                        profileInfoRVAdapter.data = dataset
                        profileInfoRVAdapter.notifyDataSetChanged()

                    } else {

                        SharedPrefsTags.notConnectedAlertWithText(context, response.body()!!.message!!)
                    }
                }

                override fun onFailure(call: Call<ClientInfoModel>, t: Throwable) {

                    SharedPrefsTags.notConnectedAlertWithText(context, t.message.toString())

                    t.printStackTrace()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun logout(context: Context, dialogCustomView: AlertDialog, loadingView: ProgressBar){


        try {
            webservice.logout(
                MyApp.instance.sharedPreferences.getString(
                    SharedPrefsTags.MY_SESSION,
                    SharedPrefsTags.MY_SESSION_DEFAULT
                )!!).enqueue(object : Callback<MainPageModel> {

                override fun onResponse(call: Call<MainPageModel>, response: Response<MainPageModel>) {

                    loadingView.visibility = View.GONE

                    if (!response.body()!!.error!!) {

                        val intent = Intent(context, LuncherActivity::class.java)

                        MyApp.instance.editor.putString(SharedPrefsTags.MY_SESSION, SharedPrefsTags.MY_SESSION_DEFAULT)
                        MyApp.instance.editor.putInt(SharedPrefsTags.OPERATOR_ID, 0)
                        MyApp.instance.editor.putString(SharedPrefsTags.OPERATOR_USERNAME, "non")
                        MyApp.instance.editor.putInt(SharedPrefsTags.CHAT_ID, 0)
                        MyApp.instance.editor.putBoolean(SharedPrefsTags.NOTIFICATION_STATUS, true)
                        MyApp.instance.editor.putString(SharedPrefsTags.OPERATOR_AVATAR, "non")


                        MyApp.instance.editor.apply()

                        context.startActivity(intent)

                        (context as Activity).finish()

                        dialogCustomView.dismiss()

                       Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()

                    } else {

                        SharedPrefsTags.notConnectedAlertWithText(context, response.body()!!.message!!)
                    }
                }

                override fun onFailure(call: Call<MainPageModel>, t: Throwable) {

                    loadingView.visibility = View.GONE
                    SharedPrefsTags.notConnectedAlertWithText(context, t.message.toString())

                    t.printStackTrace()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}