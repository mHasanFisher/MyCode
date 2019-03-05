package app.myCode.ir.Model.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



open class LoginModel {

    @SerializedName("error")
    @Expose
    public var error: Boolean? = null

    @SerializedName("message")
    @Expose
    public var message: String? = null

    @SerializedName("data")
    @Expose
    public var data: List<AccountModel>? = null






}