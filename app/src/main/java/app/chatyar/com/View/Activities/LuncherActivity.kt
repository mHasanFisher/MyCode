package app.chatyar.com.View.Activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import app.chatyar.com.Application.MyApp
import app.chatyar.com.R
import app.chatyar.com.ViewModel.Classes.SharedPrefsTags
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric



class LuncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_luncher)


        try {

            Fabric.with(this, Crashlytics())

            val lastSession = MyApp.instance.sharedPreferences.getString(SharedPrefsTags.MY_SESSION, SharedPrefsTags.MY_SESSION_DEFAULT)

            if (lastSession == SharedPrefsTags.MY_SESSION_DEFAULT) {

                intent = Intent(this@LuncherActivity, SigninActivity::class.java)
                startActivity(intent)
                finish()

            } else {

                intent = Intent(this@LuncherActivity, MainActivity::class.java)
                startActivity(intent)
                finish()

            }


        } catch (e: Exception) {
            e.printStackTrace()
        }


    }
}
