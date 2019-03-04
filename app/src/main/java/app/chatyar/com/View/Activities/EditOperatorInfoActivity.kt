package app.chatyar.com.View.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import app.chatyar.com.R

class EditOperatorInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_operator_info)


        supportActionBar!!.title = resources.getString(R.string.edit_operator_button)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }
}
