package app.myCode.ir.Model.Models

import com.google.gson.annotations.SerializedName


class SendMessageToSocketModel {

    fun SendMessageToSocketModel(

        authorization: String? = null,
        type: String? = null,
        data: SendMessageData? = null
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
    var data: SendMessageData? = null


    inner class SendMessageData {

        fun SendMessageData(messages: MutableList<Message>? = null) {

            this.messages = messages
        }

        @SerializedName("messages")
        var messages: MutableList<Message>? = null

        inner class Message {

            fun Message(
                chatid: Int? = null,
                chatname: Int? = null,
                document_id: String? = null,
                msgtoken: String? = null,
                messagetype: String? = null,
                text: String? = null,
                size: Long? = null,
                accsite_id: Int? = null,
                 solved: Int? = null
            ) {

                this.accsite_id = accsite_id
                this.chatid = chatid
                this.chatname = chatname
                this.document_id = document_id
                this.msgtoken = msgtoken
                this.messagetype = messagetype
                this.text = text
                this.size = size
                this.solved = solved
            }

            @SerializedName("accsite_id")
            var accsite_id: Int? = null

            @SerializedName("chatid")
            var chatid: Int? = null

            @SerializedName("chatname")
            var chatname: Int? = null

            @SerializedName("document_id")
            var document_id: String? = null

            @SerializedName("msgtoken")
            var msgtoken: String? = null

            @SerializedName("messagetype")
            var messagetype: String? = null

            @SerializedName("text")
            var text: String? = null

            @SerializedName("size")
            var size: Long? = null

            @SerializedName("solved")
            var solved: Int? = null
        }
    }

}