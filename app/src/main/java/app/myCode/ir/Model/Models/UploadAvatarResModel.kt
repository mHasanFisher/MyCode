package app.myCode.ir.Model.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UploadAvatarResModel {


    @SerializedName("status")
    @Expose
    val status: String? = null

    @SerializedName("message")
    @Expose
    val message: String? = null


    @SerializedName("result")
    @Expose
    val result: UploadAvatarResModel.ResUploadAvatarResModel? = null

    class ResUploadAvatarResModel{

        @SerializedName("small")
        @Expose
        val small: String? = null

        @SerializedName("medium")
        @Expose
        val medium: String? = null

        @SerializedName("large")
        @Expose
        val large: String? = null

        @SerializedName("xlarge")
        @Expose
        val xlarge: String? = null


        @SerializedName("name")
        @Expose
        val name: String? = null


        @SerializedName("ext")
        @Expose
        val ext: String? = null

    }

}
