package app.myCode.ir.View.Activities

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.Log
import app.myCode.ir.R
import app.myCode.ir.Application.MyApp
import app.myCode.ir.Model.Models.MessageFromSocketModel
import app.myCode.ir.View.Fragments.AllChatsFragment
import app.myCode.ir.View.Fragments.ProfileFragment
import app.myCode.ir.View.Fragments.UnreadChatsFragment
import app.myCode.ir.ViewModel.BroadcastReceiver.NetChangeReceiver
import app.myCode.ir.ViewModel.Classes.SharedPrefsTags
import app.myCode.ir.ViewModel.Interfaces.NetworkChangeListener
import app.myCode.ir.ViewModel.Interfaces.SocketReceiveMessage
import app.myCode.ir.ViewModel.Repositories.APIRepository
import app.myCode.ir.ViewModel.ViewModels.MainDatasetViewModel


class MainActivity : AppCompatActivity(), SocketReceiveMessage, NetworkChangeListener {


    private lateinit var socketReceiveMessage: SocketReceiveMessage
    lateinit var mainDatasetViewModel: MainDatasetViewModel
    private lateinit var fm: android.support.v4.app.FragmentManager
    private lateinit var transaction: FragmentTransaction
    private lateinit var fragmentAllChats: AllChatsFragment
    private lateinit var fragmentProfile: ProfileFragment
    private lateinit var fragmentUnreadChats: UnreadChatsFragment
    private var isHomeCurrent = false
    private lateinit var navigation: BottomNavigationView
    private var justOnce = 1
    private lateinit var netChangeReceiver: NetChangeReceiver
    private lateinit var internetIF: IntentFilter

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {

                isHomeCurrent = true

                if (fragmentAllChats == null) {
                    fragmentAllChats = AllChatsFragment()
                }

                transaction = fm.beginTransaction()
                transaction.replace(R.id.fragmentContainer, fragmentAllChats, "fragmentAllChats")
                    .addToBackStack(fragmentAllChats.toString()).commit()

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {

                isHomeCurrent = false

                if (fragmentUnreadChats == null) {
                    fragmentUnreadChats = UnreadChatsFragment()
                }

                transaction = fm.beginTransaction()
                transaction.replace(R.id.fragmentContainer, fragmentUnreadChats, "fragmentUnreadChats")
                    .addToBackStack(fragmentUnreadChats.toString()).commit()

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {

                isHomeCurrent = false

                if (fragmentProfile == null) {
                    fragmentProfile = ProfileFragment()
                }

                transaction = fm.beginTransaction()
                transaction.replace(R.id.fragmentContainer, fragmentProfile, "fragmentProfile")
                    .addToBackStack(fragmentProfile.toString()).commit()

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            navigation = findViewById(R.id.navigation)
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

            fragmentAllChats = AllChatsFragment()
            fragmentProfile = ProfileFragment()
            fragmentUnreadChats = UnreadChatsFragment()

            internetIF = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            netChangeReceiver = NetChangeReceiver(this@MainActivity)
            registerReceiver(netChangeReceiver, internetIF)

            fm = supportFragmentManager
            transaction = fm.beginTransaction()

            transaction.replace(R.id.fragmentContainer, fragmentAllChats, "fragmentAllChats")
                .addToBackStack(fragmentAllChats.toString()).commit()

            navigation.selectedItemId = R.id.navigation_home

            MyApp.instance.socketReceiveMessage = this
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun onBackPressed() {

        try {
            if (isHomeCurrent) {
                finish()
            } else {

                isHomeCurrent = true

                if (fragmentAllChats == null) {
                    fragmentAllChats = AllChatsFragment()
                }

                transaction = fm.beginTransaction()
    //            transaction.setCustomAnimations(R.animator.fade_in_anim, R.animator.fade_out_anim)

                transaction.replace(R.id.fragmentContainer, fragmentAllChats, "fragmentAllChats")
                    .addToBackStack(fragmentAllChats.toString()).commit()

                navigation.selectedItemId = R.id.navigation_home
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onReceiveMessage(context: Context, message: MessageFromSocketModel?) {

        Log.i("Receive:", "received")
    }

    override fun onOpenSocket(connectedTo: String) {

        Log.i("Open:", connectedTo)
    }

    override fun onCloseSocket(disconnectedFrom: String, code: Int, reason: String, remote: Boolean) {

        Log.i("Close:", disconnectedFrom)
    }

    override fun onErrorSocket(ex: Exception) {
        Log.i("Error:", ex.message)
    }


    override fun onDestroy() {
        super.onDestroy()

        try {
            MyApp.instance.isMainFinished = true

            SharedPrefsTags.closeSocket()

            unregisterReceiver(netChangeReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun netChanged(context: Context, status: Boolean) {

        try {
            if (SharedPrefsTags.isConnected(context)) {
                APIRepository.getMainPage(context)
            }
            else {
                SharedPrefsTags.closeSocket()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
