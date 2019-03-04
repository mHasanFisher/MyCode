package app.chatyar.com.Model.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ClientInfoModel {

    @SerializedName("error")
    @Expose
    val error: Boolean? = null

    @SerializedName("data")
    @Expose
    val data: MutableList<ClientDataModel>? = null

    @SerializedName("message")
    @Expose
    val message: String? = null


    class ClientDataModel {

        @SerializedName("key")
        @Expose
        val key: String? = null

        @SerializedName("value")
        @Expose
        val value: String? = null

    }

}