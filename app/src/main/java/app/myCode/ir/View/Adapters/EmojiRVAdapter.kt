package app.myCode.ir.View.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import app.myCode.ir.R

class EmojiRVAdapter(var context: Context, var messageBoxET: EditText, var data: MutableList<String>) :
    RecyclerView.Adapter<EmojiRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): EmojiRVAdapter.ViewHolder {

        val v = LayoutInflater.from(p0.getContext()).inflate(R.layout.card_view_emoji, p0, false)
        return EmojiRVAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {

        return data.size
    }

    override fun onBindViewHolder(viewHolder: EmojiRVAdapter.ViewHolder, position: Int) {

        viewHolder.emojiButt.text = data[position]

        viewHolder.emojiButt.setOnClickListener {

            messageBoxET.append(data[position])

        }
    }


    class ViewHolder(var mView: View) : RecyclerView.ViewHolder(mView) {

        var emojiButt: Button

        init {

            emojiButt = mView.findViewById(R.id.emojiButt)
        }
    }

}