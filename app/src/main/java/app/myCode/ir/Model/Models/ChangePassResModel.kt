package app.myCode.ir.Model.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ChangePassResModel {

    @SerializedName("error")
    @Expose
    val error: Boolean? = null

    @SerializedName("data")
    @Expose
    val data: MutableList<String> = ArrayList()

    @SerializedName("message")
    @Expose
    val message: String? = null

}
