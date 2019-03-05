package app.myCode.ir.View.Fragments


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import app.myCode.ir.Application.MyApp
import app.myCode.ir.Model.Models.MessageFromSocketModel

import app.myCode.ir.R
import app.myCode.ir.View.Activities.ChangePasswordActivity
import app.myCode.ir.ViewModel.Classes.SharedPrefsTags
import app.myCode.ir.ViewModel.Interfaces.SocketReceiveMessage
import de.hdodenhof.circleimageview.CircleImageView
import android.net.Uri
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.webkit.MimeTypeMap
import android.widget.*
import app.myCode.ir.View.Activities.EditOperatorInfoActivity
import app.myCode.ir.ViewModel.Classes.ProgressRequestBody
import app.myCode.ir.ViewModel.Repositories.APIRepository
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.yalantis.ucrop.UCrop
import okhttp3.MultipartBody
import java.io.File


class ProfileFragment : Fragment(), SocketReceiveMessage, ProgressRequestBody.UploadCallbacks {

    private lateinit var profileView: View
    private lateinit var exitButt: Button
    private lateinit var editUserInfoButt: Button
    private lateinit var editPassButt: Button
    private lateinit var switchOnOff: Switch
    private lateinit var profileImage: CircleImageView
    private lateinit var operatorName: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarTV: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        try {
            profileView = inflater.inflate(R.layout.fragment_profile, container, false)
            MyApp.instance.socketReceiveMessage = this

            exitButt = profileView.findViewById(R.id.exitButt)
            editUserInfoButt = profileView.findViewById(R.id.editUserInfoButt)
            editPassButt = profileView.findViewById(R.id.editPassButt)
            switchOnOff = profileView.findViewById(R.id.switchOnOff)
            profileImage = profileView.findViewById(R.id.profileImage)
            operatorName = profileView.findViewById(R.id.operatorName)
            progressBar = profileView.findViewById(R.id.progressBar)
            progressBarTV = profileView.findViewById(R.id.progressBarTV)

            val opName = "${MyApp.instance.sharedPreferences.getString(
                SharedPrefsTags.OPERATOR_USERNAME,
                resources.getString(R.string.operator_name)
            )}  "

            operatorName.text = opName


            exitButt.setOnClickListener {


                exitWarningAlert(profileView.context)

            }

            editPassButt.setOnClickListener {

                val intent = Intent(profileView.context, ChangePasswordActivity::class.java)
                profileView.context.startActivity(intent)

            }

            editUserInfoButt.setOnClickListener {

                val intent = Intent(profileView.context, EditOperatorInfoActivity::class.java)
                profileView.context.startActivity(intent)

            }

            switchOnOff.isChecked = MyApp.instance.sharedPreferences.getBoolean(SharedPrefsTags.NOTIFICATION_STATUS, true)

            if (switchOnOff.isChecked) {
                switchOnOff.text = resources.getString(R.string.notification_is_on)
            } else {
                switchOnOff.text = resources.getString(R.string.notification_is_off)
            }

            switchOnOff.setOnCheckedChangeListener { buttonView, isChecked ->

                if (isChecked) {

                    switchOnOff.text = resources.getString(R.string.notification_is_on)
                    MyApp.instance.editor.putBoolean(SharedPrefsTags.NOTIFICATION_STATUS, true).apply()
                } else {

                    switchOnOff.text = resources.getString(R.string.notification_is_off)
                    MyApp.instance.editor.putBoolean(SharedPrefsTags.NOTIFICATION_STATUS, false).apply()
                    MyApp.instance.notificationManager!!.cancelAll()
                }

            }


            Glide.with(profileView.context)

               .load(MyApp.instance.sharedPreferences.getString(SharedPrefsTags.AVATAR_URL_SMALL, ""))
               .apply(
                   RequestOptions.fitCenterTransform()
                       .placeholder(R.mipmap.icon_username)
                       .error(R.mipmap.icon_username).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
               )
               .into(profileImage)


            Glide.with(profileView.context)

                .load(MyApp.instance.sharedPreferences.getString(SharedPrefsTags.AVATAR_URL_XLARGE, ""))
                .apply(RequestOptions.fitCenterTransform())
                .into(profileImage)





            profileImage.setOnClickListener {


                try {
                    if (ActivityCompat.checkSelfPermission(
                            profileView.context,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                            profileView.context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                        ActivityCompat.requestPermissions(
                            profileView.context as Activity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            1
                        )

                    } else {

                        val pickPhoto = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                        startActivityForResult(pickPhoto, 1)//one can be replaced with any action code

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        return profileView
    }


    override fun onReceiveMessage(
        context: Context,
        message: MessageFromSocketModel?
    ) {

    }

    override fun onOpenSocket(connectedTo: String) {
    }

    override fun onCloseSocket(disconnectedFrom: String, code: Int, reason: String, remote: Boolean) {
    }

    override fun onErrorSocket(ex: Exception) {
    }


    private fun exitWarningAlert(context: Context) {

        try {
            val builder = AlertDialog.Builder(context)
            val dialogCustomView: AlertDialog
            val inflaterAlert = (context as Activity).layoutInflater
            val viewAlert = inflaterAlert.inflate(R.layout.alert_exit_user, null)

            val yesButt: Button = viewAlert.findViewById(R.id.yesButt)
            val noButt: Button = viewAlert.findViewById(R.id.noButt)
            val loadingView: ProgressBar = viewAlert.findViewById(R.id.loadingView)

            builder.setView(viewAlert)
            dialogCustomView = builder.create()

            yesButt.setOnClickListener {

                loadingView.visibility =View.VISIBLE
                APIRepository.logout(profileView.context, dialogCustomView, loadingView)

            }

            noButt.setOnClickListener {

                dialogCustomView.dismiss()
            }

            dialogCustomView.show()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        try {
            if (requestCode == 1) {

                try {
                    val pickPhoto = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )

                    startActivityForResult(pickPhoto, 1)//one can be replaced with any action code

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

                1 -> {
                    try {
                        if (resultCode == Activity.RESULT_OK) {

                            val uri = data!!.data


                            val root = Environment.getExternalStorageDirectory().toString()
                            var myDir = File("$root/chatyar/")

                            if (!myDir.exists()) {
                                myDir.mkdirs()
                            }

                            val imageFile = File(uri!!.path)

                            val cR = profileView.context.contentResolver;
                            val mime = MimeTypeMap.getSingleton();
                            val type = mime.getExtensionFromMimeType(cR.getType(uri))

                            myDir = File(myDir, imageFile.name + "." + type)


                            val options = UCrop.Options();

                            options.setToolbarColor(ContextCompat.getColor(profileView.context, R.color.colorPrimary))
                            options.setStatusBarColor(
                                ContextCompat.getColor(
                                    profileView.context,
                                    R.color.colorPrimaryDark
                                )
                            )
                            options.setActiveWidgetColor(
                                ContextCompat.getColor(
                                    profileView.context,
                                    R.color.colorAccent
                                )
                            )

                            UCrop.of(uri, Uri.fromFile(myDir))
                                .withAspectRatio(1F, 1F)
                                .withOptions(options)
                                .start(profileView.context, this, UCrop.REQUEST_CROP)


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
                UCrop.REQUEST_CROP -> {
                    try {

                        if (resultCode == Activity.RESULT_OK) {

                            val resultUri = UCrop.getOutput(data!!);


//                            val projection = arrayOf(MediaStore.Images.Media.DATA)
//                            val cursor =
//                                profileView.context.contentResolver.query(resultUri!!, projection, null, null, null)
//                            cursor!!.moveToFirst()
//
//                            val columnIndex = cursor.getColumnIndex(projection[0])
//                            val filePath = cursor.getString(columnIndex)
//                            cursor.close()

//                            val selectedImage = BitmapFactory.decodeFile(resultUri!!.path)
//
//                            profileImage.setImageBitmap(selectedImage)

//                            var str = resultUri!!.toString()

                            val avatarFile = File(resultUri!!.path)
//                            val avatarFile = File("${Environment.getExternalStorageDirectory()}/chatyar/pro.png")

                            val fileBody = ProgressRequestBody(avatarFile, this)

//                             var requestFile: RequestBody = RequestBody.create(MediaType.parse("image/*"), avatarFile)

                            val fileToUpload = MultipartBody.Part.createFormData("file", avatarFile.name, fileBody)


//                            profileImage.setImageDrawable(null)
                            progressBar.visibility = View.VISIBLE
                            progressBarTV.visibility = View.VISIBLE

                            APIRepository.uploadAvatar(profileView.context, fileToUpload, profileImage, resultUri)


                        } else if (resultCode == UCrop.RESULT_ERROR) {
                            val cropError: Throwable = UCrop.getError(data!!)!!
                            Toast.makeText(profileView.context, cropError.toString(), Toast.LENGTH_LONG).show()

                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun onProgressUpdate(percentage: Int) {

        try {
            progressBarTV.text = "$percentage %"
            progressBar.progress = percentage
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onError() {

        try {
            Toast.makeText(profileView.context, "Error When Uploading, please try again.", Toast.LENGTH_LONG).show()

            progressBarTV.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            profileImage.setImageResource(R.mipmap.icon_username)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onFinish() {

        try {
            progressBarTV.text = "100 %"
            progressBar.progress = 100

            progressBarTV.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
