package app.chatyar.com.View.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import app.chatyar.com.Model.Models.ClientInfoModel
import app.chatyar.com.R

class ProfileInfoRVAdapter(var context: Context, var data: MutableList<ClientInfoModel.ClientDataModel>?) : RecyclerView.Adapter<ProfileInfoRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ProfileInfoRVAdapter.ViewHolder {

        val v = LayoutInflater.from(p0.context).inflate(R.layout.card_view_profile_info_item, p0, false)
        return ProfileInfoRVAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {

        return data?.size!!
    }

    override fun onBindViewHolder(viewHolder: ProfileInfoRVAdapter.ViewHolder, position: Int) {

        viewHolder.itemNameTV.text = data!![position].value!!
        viewHolder.itemNameTitleTV.text = data!![position].key!!
//        viewHolder.itemImage.setImageResource(data?.get(position)?.image!!)

    }


    class ViewHolder(var mView: View) : RecyclerView.ViewHolder(mView) {

        var itemNameTV: TextView
        var itemNameTitleTV: TextView
        var itemImage: ImageView

        init {

            itemNameTV = mView.findViewById(R.id.itemNameTV)
            itemNameTitleTV = mView.findViewById(R.id.itemNameTitleTV)
            itemImage = mView.findViewById(R.id.itemImage)

        }
    }




}