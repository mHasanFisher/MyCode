package app.myCode.ir.View.Adapters

import android.app.AlertDialog
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import app.myCode.ir.Application.MyApp
import app.myCode.ir.Model.Models.MainPageModel
import app.myCode.ir.R
import app.myCode.ir.ViewModel.Classes.SharedPrefsTags

class ChangeDomainRVAdapter(var context: Context, var frag: Fragment, var dialog: AlertDialog, var data: MutableList<MainPageModel.DataMainPageModel.SiteModel>) :
    RecyclerView.Adapter<ChangeDomainRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ChangeDomainRVAdapter.ViewHolder {

        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.card_view_change_site, viewGroup, false)
        return ChangeDomainRVAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {

        return data.size
    }

    override fun onBindViewHolder(viewHolder: ChangeDomainRVAdapter.ViewHolder, position: Int) {

        viewHolder.siteTV.text = data[position].accsiteUrl
        viewHolder.siteTitleTV.text = data[position].accsiteTitle

        if (data[position].isActive) {
            viewHolder.isActiveImage.setImageResource(R.mipmap.icon_one_tick)
        } else {
            viewHolder.isActiveImage.setImageResource(R.mipmap.icon_empty_box)
        }

        viewHolder.mView.setOnClickListener {



            var activePos: MainPageModel.DataMainPageModel.SiteModel? = null

            var oldPos = -2

            data.forEach { dataItem ->

                if (dataItem.isActive) {

                    activePos = dataItem
                }
            }

            if (activePos != null) {

                activePos!!.isActive = false
                data[data.indexOf(activePos!!)] = activePos!!
                oldPos = data.indexOf(activePos!!)

            }

            val toBeActiveItem = data[position]
            toBeActiveItem.isActive = true

            data[position] = toBeActiveItem

            if(oldPos != -2){
                notifyItemChanged(oldPos)
            }

            notifyItemChanged(position)

            MyApp.instance.editor.putInt(SharedPrefsTags.LAST_SITE_ID, data[position].accsiteId!!.toInt()).apply()

            MyApp.instance.whichSiteCurrentId = data[position].accsiteId!!

            SharedPrefsTags.getAllMessages(context, false)
//
//            if(frag is AllChatsFragment) {
//
//                (frag as AllChatsFragment).checkWhichDomainIsSet()
//            }
//
//            else if(frag is UnreadChatsFragment) {
//
//                (frag as UnreadChatsFragment).checkWhichDomainIsSet()
//            }

            dialog.dismiss()
        }

    }


    class ViewHolder(var mView: View) : RecyclerView.ViewHolder(mView) {

        var siteTV: TextView = mView.findViewById(R.id.siteTV)
        var siteTitleTV: TextView = mView.findViewById(R.id.siteTitleTV)
        var isActiveImage: ImageView = mView.findViewById(R.id.isActiveImage)

    }
}