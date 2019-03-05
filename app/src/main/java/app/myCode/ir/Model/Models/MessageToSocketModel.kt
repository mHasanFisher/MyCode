package app.myCode.ir.Model.Models

import com.google.gson.annotations.SerializedName


class MessageToSocketModel {

    fun messageToSocketModel(

        authorization: String?,
        type: String?,
        data: MessageToSocketData?
    ) {

        this.authorization = authorization
        this.type = type
        this.data = data
    }

    @SerializedName("authorization")
    var authorization: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("data")
    var data: MessageToSocketData? = null


    inner class MessageToSocketData {

        fun messageToSocketData(
            accsite_id: Int?,
            limit: Int?,
            last_chatdate: Long?,
            chatid: Int?,
            begin_chatdate: Long?
        ) {

            this.accsite_id = accsite_id
            this.limit = limit
            this.last_chatdate = last_chatdate
            this.chatid = chatid
            this.begin_chatdate = begin_chatdate

        }

        @SerializedName("accsite_id")
        var accsite_id: Int? = null

        @SerializedName("limit")
        var limit: Int? = null

        @SerializedName("last_chatdate")
        var last_chatdate: Long? = null

        @SerializedName("chatid")
        var chatid: Int? = null

        @SerializedName("begin_chatdate")
        var begin_chatdate: Long? = null
    }

}