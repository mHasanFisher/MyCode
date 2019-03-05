package app.myCode.ir.Model.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class MessageFromSocketModel {

    @SerializedName("error")
    @Expose
    var error: Boolean? = null

    @SerializedName("type")
    @Expose
    var type: String? = null


    @SerializedName("data")
    @Expose
    var data: MessageData? = null


    inner class MessageData {


        @SerializedName("message")
        @Expose
        var message: MutableList<MessageModel>? = null


        inner class MessageModel {

            public fun messageModel(chatid: Int? = null,
                                    chatname: Int? = null,
                                    messageid: Int? = null,
                                    messagetype: String? = null,
                                    documentId: String? = null,
                                    photo: String? = null,
                                    text: String? = null,
                                    date: Long? = null,
                                    operatorid: Int? = null,
                                    accsiteId: Int? = null,
                                    seenmessage: Int? = null,
                                    message: String? = null,
                                    messagetoken: String? = null,
                                    size: Long? = null,
                                    solved: Int? = null
            ){


                this.chatid = chatid
                this.chatname = chatname
                this.messageid = messageid
                this.messagetype = messagetype
                this.photo = photo
                this.text = text
                this.date = date
                this.operatorid = operatorid
                this.seenmessage = seenmessage
                this.message = message
                this.messagetoken = messagetoken
                this.documentId = documentId
                this.accsiteId = accsiteId
                this.size = size
                this.solved = solved

            }



            @SerializedName("chatid")
            @Expose
            var chatid: Int? = null

            @SerializedName("chatname")
            @Expose
            var chatname: Int? = null

            @SerializedName("messageid")
            @Expose
            var messageid: Int? = null

            @SerializedName("messagetype")
            @Expose
            var messagetype: String? = null

            @SerializedName("photo")
            @Expose
            var photo: String? = null

            @SerializedName("text")
            @Expose
            var text: String? = null

            @SerializedName("date")
            @Expose
            var date: Long? = null

            @SerializedName("operatorid")
            @Expose
            var operatorid: Int? = null

            @SerializedName("operatorname")
            @Expose
            var operatorname: String? = null

            @SerializedName("seenmessage")
            @Expose
            var seenmessage: Int? = null

            @SerializedName("message")
            @Expose
            var message: String? = null

            @SerializedName("msgtoken")
            @Expose
            var messagetoken: String? = null

            @SerializedName("document_id")
            @Expose
            var documentId: String? = ""

            @SerializedName("accsite_id")
            @Expose
            var accsiteId: Int? = null

            @SerializedName("size")
            @Expose
            var size: Long? = null


            @SerializedName("solved")
            @Expose
            var solved: Int? = null

        }
    }

}