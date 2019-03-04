package app.chatyar.com.Model.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AccountModel: LoginModel() {


        @SerializedName("account_id")
        @Expose
        var accountId: Int? = null

        @SerializedName("account_username")
        @Expose
        var accountUsername: String? = null

        @SerializedName("account_avatar")
        @Expose
        var accountAvatar: String? = null

        @SerializedName("account_firstname")
        @Expose
        var accountFirstname: String? = null

        @SerializedName("account_lastname")
        @Expose
        var accountLastname: String? = null

        @SerializedName("account_mobilenumber")
        @Expose
        var accountMobilenumber: String? = null

}