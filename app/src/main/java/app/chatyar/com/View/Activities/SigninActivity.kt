package app.chatyar.com.View.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import app.chatyar.com.R
import app.chatyar.com.ViewModel.Repositories.APIRepository
import android.util.Log
import app.chatyar.com.Application.MyApp
import app.chatyar.com.ViewModel.Classes.SharedPrefsTags
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import android.content.Intent
import android.net.Uri


class SigninActivity : AppCompatActivity() {

    private lateinit var usernameET: EditText
    private lateinit var passwordET: EditText
    private lateinit var enterButton: Button
    private lateinit var loadingView: ProgressBar
    private lateinit var demoButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        try {

            SharedPrefsTags.resetAllDatasForSignin()

            firebaseGetToken()

            usernameET = findViewById(R.id.usernameET);
            passwordET = findViewById(R.id.passwordET);
            enterButton = findViewById(R.id.enterButton);
            loadingView = findViewById(R.id.loadingView);
            demoButton = findViewById(R.id.demoButton);

            demoButton.setOnClickListener {

                if (SharedPrefsTags.isConnected(this)) {

//                    APIRepository.postLogin("saffari21@gmail.com", "123456", loadingView)


                    val url = "https://panel.chatyar.com/register.php"
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)


                } else {

                    SharedPrefsTags.notConnectedAlert(this)
                }
            }


            enterButton.setOnClickListener {

                if (SharedPrefsTags.isConnected(this)) {

                    if (!usernameET.text.isEmpty() && !passwordET.text.isEmpty()) {

                        APIRepository.postLogin(usernameET.text.toString(), passwordET.text.toString(), loadingView)

                    } else if (!usernameET.text.isEmpty() && passwordET.text.isEmpty()) {

                        passwordET.background =
                                ContextCompat.getDrawable(this, R.drawable.username_password_background_wrong)

                    } else if (usernameET.text.isEmpty() && !passwordET.text.isEmpty()) {
                        usernameET.background =
                                ContextCompat.getDrawable(this, R.drawable.username_password_background_wrong)

                    } else if (usernameET.text.isEmpty() && passwordET.text.isEmpty()) {
                        usernameET.background =
                                ContextCompat.getDrawable(this, R.drawable.username_password_background_wrong)
                        passwordET.background =
                                ContextCompat.getDrawable(this, R.drawable.username_password_background_wrong)
                    }
                } else {

                    SharedPrefsTags.notConnectedAlert(this)
                }
            }

            textChangesImplementation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun textChangesImplementation() {

        try {
            usernameET.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {

                    usernameET.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.username_password_background)


                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })

            passwordET.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {

                    passwordET.background =
                            ContextCompat.getDrawable(applicationContext, R.drawable.username_password_background)

                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun firebaseGetToken() {

        try {
            FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->

                    if (!task.isSuccessful) {

                        Log.w("TAG", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result!!.token
                    MyApp.instance.editor.putString(SharedPrefsTags.FIREBASE_TOKEN, token).apply()
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
