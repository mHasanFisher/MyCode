package app.chatyar.com.View.Adapters

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import app.chatyar.com.Application.MyApp
import app.chatyar.com.Model.Models.MessageFromSocketModel
import app.chatyar.com.R
import app.chatyar.com.View.Activities.ChatActivity
import app.chatyar.com.ViewModel.Classes.SharedPrefsTags
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.HashMap

class AllChatsRVAdapter(
    var context: Context, var data: List<MessageFromSocketModel.MessageData.MessageModel>?,
    var mainData: HashMap<Int, Int>
) : RecyclerView.Adapter<AllChatsRVAdapter.ViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): AllChatsRVAdapter.ViewHolder {

        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.card_view_all_chats_item, viewGroup, false)
        return AllChatsRVAdapter.ViewHolder(v)

    }

    override fun getItemCount(): Int {

        return data?.size!!
    }

    override fun onBindViewHolder(viewHolder: AllChatsRVAdapter.ViewHolder, position: Int) {


        val whichAv = data!![position].chatid!! % 6
        val colorName = "backAvatar"
        val newColorName = colorName + whichAv

        val theColor = context.resources.getIdentifier(newColorName, "color", context.packageName)
        viewHolder.userImage.circleBackgroundColor = ContextCompat.getColor(context, theColor)



            viewHolder.itemNameTV.text = " گفتگوی ${data?.get(position)?.chatname.toString()} "

            var accSiteTitle = ""
            MyApp.instance.siteList.forEach {
                if (data?.get(position)?.accsiteId == it.accsiteId) {
                    accSiteTitle = it.accsiteTitle!!
                }
            }
            viewHolder.domainNameTV.text = accSiteTitle

            viewHolder.dateTV.text = convertUnixToDateOrTime(data?.get(position)?.date)
            viewHolder.itemTextTV.text = data?.get(position)?.text.toString()



            if (data?.get(position)?.seenmessage == 0 && data?.get(position)?.operatorid != null) {
                viewHolder.statusOfMessageImage.visibility = View.VISIBLE
            } else {
                viewHolder.statusOfMessageImage.visibility = View.INVISIBLE
            }



            if (mainData.get(data?.get(position)?.chatid) == null || (mainData.get(data?.get(position)?.chatid) != null && mainData.get(
                    data?.get(position)?.chatid
                )!! < 1)
            ) {
                viewHolder.itemUnreadMessagesTV.visibility = View.INVISIBLE
            } else {
                viewHolder.itemUnreadMessagesTV.visibility = View.VISIBLE
                viewHolder.itemUnreadMessagesTV.text = mainData.get(data?.get(position)?.chatid).toString()
            }

            viewHolder.mView.setOnClickListener {

                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra(SharedPrefsTags.CHAT_ID, data?.get(position)?.chatid)
                intent.putExtra(SharedPrefsTags.CHAT_NAME, data?.get(position)?.chatname)
                intent.putExtra(SharedPrefsTags.SITE_ID, data?.get(position)?.accsiteId)

                intent.putExtra(SharedPrefsTags.SOLVED, data?.get(position)?.solved)


                context.startActivity(intent)

            }



    }


    class ViewHolder(var mView: View) : RecyclerView.ViewHolder(mView) {

        var itemNameTV: TextView
        var dateTV: TextView
        var itemTextTV: TextView
        var itemUnreadMessagesTV: TextView
        var statusOfMessageImage: ImageView
        var userImage: CircleImageView
        var domainNameTV: TextView

        init {

            itemNameTV = mView.findViewById(R.id.itemNameTV)
            domainNameTV = mView.findViewById(R.id.domainNameTV)
            dateTV = mView.findViewById(R.id.dateTV)
            itemTextTV = mView.findViewById(R.id.itemTextTV)
            itemUnreadMessagesTV = mView.findViewById(R.id.itemUnreadMessagesTV)
            statusOfMessageImage = mView.findViewById(R.id.statusOfMessageImage)
            userImage = mView.findViewById(R.id.userImage)


        }
    }


    fun convertUnixToDateOrTime(unixTime: Long?): String {

        if (unixTime == null) {
            return ""
        }

        val calendarNow = Calendar.getInstance()
        calendarNow.timeInMillis = System.currentTimeMillis()
        calendarNow.timeZone = java.util.TimeZone.getTimeZone(TimeZone.getDefault().id)

        val calendar = Calendar.getInstance()


        calendar.timeInMillis = unixTime * 1000L
        calendar.timeZone = java.util.TimeZone.getTimeZone(TimeZone.getDefault().id)

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        val yearNow = calendar.get(Calendar.YEAR)
        val monthNow = calendar.get(Calendar.MONTH)
        val dayNow = calendar.get(Calendar.DAY_OF_MONTH)
        val hourNow = calendar.get(Calendar.HOUR_OF_DAY)
        val minuteNow = calendar.get(Calendar.MINUTE)
        val secondNow = calendar.get(Calendar.SECOND)

        if (day == dayNow) {

            return String.format("%02d:%02d", hour, minute)
        } else if (day != dayNow) {

            val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
            return "$day $monthName"
        } else {
            val monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
            return "$day $monthName $year"
        }
    }


}