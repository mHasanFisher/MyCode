package app.myCode.ir.View.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import app.myCode.ir.R
import app.myCode.ir.View.Adapters.ProfileInfoRVAdapter
import app.myCode.ir.ViewModel.Classes.SharedPrefsTags
import app.myCode.ir.Model.Models.ClientInfoModel
import app.myCode.ir.ViewModel.Repositories.APIRepository


class ProfileInfoActivity : AppCompatActivity() {

    private lateinit var profileInfoRV: RecyclerView
    private lateinit var profileInfoRVAdapter: ProfileInfoRVAdapter
    private lateinit var profileInfoRVLinearLM: LinearLayoutManager
    private var dataset: MutableList<ClientInfoModel.ClientDataModel> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_info)

        try {
            val clientname = intent.extras?.getInt(SharedPrefsTags.CHAT_NAME, 0)
            val clientId = intent.extras?.getInt(SharedPrefsTags.CHAT_ID, 0)

            supportActionBar!!.title = if(clientname != 0) " گفتگوی $clientname " else "در دسترس نیست ... "
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(true)


//        dataset.add(ProfileInfoModel("IP info", "IP : ", R.mipmap.icon_profile))
//        dataset.add(ProfileInfoModel("Browser info", "Browser : ", R.mipmap.icon_change_site))
//        dataset.add(ProfileInfoModel("System info", "System : ", R.mipmap.icon_all_chats))
//        dataset.add(ProfileInfoModel("IP info", "IP : ", R.mipmap.icon_profile))
//        dataset.add(ProfileInfoModel("Browser info", "Browser : ", R.mipmap.icon_change_site))
//        dataset.add(ProfileInfoModel("System info", "System : ", R.mipmap.icon_all_chats))

            profileInfoRV = findViewById(R.id.profileInfoRV)
            profileInfoRVLinearLM = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            profileInfoRVAdapter = ProfileInfoRVAdapter(this, dataset)

            profileInfoRV.layoutManager = profileInfoRVLinearLM
            profileInfoRV.adapter = profileInfoRVAdapter

            APIRepository.getClientInfo(this, clientId!!, profileInfoRVAdapter )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }

}
