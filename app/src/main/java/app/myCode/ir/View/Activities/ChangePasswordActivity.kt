package app.myCode.ir.View.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.*
import app.myCode.ir.R
import app.myCode.ir.ViewModel.Classes.SharedPrefsTags
import app.myCode.ir.ViewModel.Repositories.APIRepository

class ChangePasswordActivity : AppCompatActivity() {


    private lateinit var currentPassET: EditText
    private lateinit var currentPassRepeatET: EditText
    private lateinit var newPassET: EditText
    private lateinit var changePassButtonRL: RelativeLayout
    private lateinit var loadingView: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)


        currentPassET = findViewById(R.id.currentPassET)
        currentPassRepeatET = findViewById(R.id.currentPassRepeatET)
        newPassET = findViewById(R.id.newPassET)
        changePassButtonRL = findViewById(R.id.changePassButtonRL)
        loadingView = findViewById(R.id.loadingView)


        supportActionBar!!.title = resources.getString(R.string.change_pass_button)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(true)


        changePassButtonRL.setOnClickListener {

            if (SharedPrefsTags.isConnected(this)) {

                if (!currentPassET.text.isEmpty() && !currentPassRepeatET.text.isEmpty() && !newPassET.text.isEmpty()) {


                    if ((currentPassET.text.toString().trim() == currentPassRepeatET.text.toString().trim()) && currentPassET.text.toString().trim() != newPassET.text.toString().trim()) {


                    APIRepository.changePass(this, currentPassET.text.toString().trim(), newPassET.text.toString().trim())

                    } else if (currentPassET.text.toString().trim() != currentPassRepeatET.text.toString().trim()) {

                        Toast.makeText(this, "رمز عبور و تکرارش با هم برابر نیستند!", Toast.LENGTH_LONG).show()

                        currentPassRepeatET.background =
                                ContextCompat.getDrawable(this, R.drawable.background_go_to_end_red)
                        currentPassET.background =
                                ContextCompat.getDrawable(this, R.drawable.background_go_to_end_red)

                    } else if (currentPassET.text.toString().trim() == newPassET.text.toString().trim()) {


                        Toast.makeText(this, "رمز عبور جدید باید با رمز عبور قبلی تفاوت داشته باشد!", Toast.LENGTH_LONG)
                            .show()

                        newPassET.background =
                                ContextCompat.getDrawable(this, R.drawable.background_go_to_end_red)


                    }


                } else if (!currentPassET.text.isEmpty() && currentPassRepeatET.text.isEmpty() && newPassET.text.isEmpty()) {

                    currentPassRepeatET.background =
                            ContextCompat.getDrawable(this, R.drawable.background_go_to_end_red)
                    newPassET.background =
                            ContextCompat.getDrawable(this, R.drawable.background_go_to_end_red)

                } else if (currentPassET.text.isEmpty() && !currentPassRepeatET.text.isEmpty() && newPassET.text.isEmpty()) {

                    currentPassET.background =
                            ContextCompat.getDrawable(this, R.drawable.background_go_to_end_red)
                    newPassET.background =
                            ContextCompat.getDrawable(this, R.drawable.background_go_to_end_red)

                } else if (currentPassET.text.isEmpty() && currentPassRepeatET.text.isEmpty() && !newPassET.text.isEmpty()) {

                    currentPassET.background =
                            ContextCompat.getDrawable(this, R.drawable.background_go_to_end_red)
                    currentPassRepeatET.background =
                            ContextCompat.getDrawable(this, R.drawable.background_go_to_end_red)
                } else if (!currentPassET.text.isEmpty() && !currentPassRepeatET.text.isEmpty() && newPassET.text.isEmpty()) {

                    newPassET.background =
                            ContextCompat.getDrawable(this, R.drawable.background_go_to_end_red)

                } else if (!currentPassET.text.isEmpty() && currentPassRepeatET.text.isEmpty() && !newPassET.text.isEmpty()) {

                    currentPassRepeatET.background =
                            ContextCompat.getDrawable(this, R.drawable.background_go_to_end_red)

                } else if (currentPassET.text.isEmpty() && !currentPassRepeatET.text.isEmpty() && !newPassET.text.isEmpty()) {

                    currentPassET.background =
                            ContextCompat.getDrawable(this, R.drawable.background_go_to_end_red)
                }

                else if (currentPassET.text.isEmpty() && currentPassRepeatET.text.isEmpty() && newPassET.text.isEmpty()) {

                    currentPassET.background =
                            ContextCompat.getDrawable(this, R.drawable.background_go_to_end_red)
                    currentPassRepeatET.background =
                            ContextCompat.getDrawable(this, R.drawable.background_go_to_end_red)
                    newPassET.background =
                            ContextCompat.getDrawable(this, R.drawable.background_go_to_end_red)
                }


            } else {

                SharedPrefsTags.notConnectedAlert(this)
            }

        }

        textChangesImplementation()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }


    private fun textChangesImplementation() {

        currentPassET.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

                currentPassET.background =
                        ContextCompat.getDrawable(applicationContext, R.drawable.background_go_to_end)

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        currentPassRepeatET.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

                currentPassRepeatET.background =
                        ContextCompat.getDrawable(applicationContext, R.drawable.background_go_to_end)

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })


        newPassET.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

                newPassET.background =
                        ContextCompat.getDrawable(applicationContext, R.drawable.background_go_to_end)

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })


    }

}
