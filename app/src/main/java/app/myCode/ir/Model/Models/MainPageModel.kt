package app.myCode.ir.Model.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class MainPageModel {

    @SerializedName("error")
    @Expose
    val error: Boolean? = null

    @SerializedName("data")
    @Expose
    val data: DataMainPageModel? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    class DataMainPageModel {


        @SerializedName("link")
        @Expose
        var link: String? = null

        @SerializedName("profile")
        @Expose
        val profile: ProfileModel? = null

        @SerializedName("site")
        @Expose
        val site: MutableList<SiteModel>? = null

        @SerializedName("operator")
        @Expose
        val operator: MutableList<OperatorModel>? = null

        class ProfileModel {

            @SerializedName("account_id")
            @Expose
            val accountId: Int? = null

            @SerializedName("account_username")
            @Expose
            val accountUsername: String? = null

            @SerializedName("account_avatar")
            @Expose
            val accountAvatar: AccountAvatarModel? = null

            @SerializedName("account_firstname")
            @Expose
            val accountFirstname: String? = null

            @SerializedName("account_lastname")
            @Expose
            val accountLastname: String? = null

        }


        class AccountAvatarModel {


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

        }


        class SiteModel {

            fun siteModel(
                accsiteId: Int?,
                accsiteTitle: String?,
                accsiteUrl: String?,
                isActive: Boolean
            ) {

                this.accsiteId = accsiteId
                this.accsiteTitle = accsiteTitle
                this.accsiteUrl = accsiteUrl
                this.isActive = isActive

            }


            @SerializedName("accsite_id")
            @Expose
            var accsiteId: Int? = null

            @SerializedName("accsite_title")
            @Expose
            var accsiteTitle: String? = null

            @SerializedName("accsite_url")
            @Expose
            var accsiteUrl: String? = null

            var isActive = false

        }

        class OperatorModel {

            @SerializedName("account_id")
            @Expose
            val accountId: Int? = null

            @SerializedName("account_firstname")
            @Expose
            val accountFirstname: String? = null

            @SerializedName("account_lastname")
            @Expose
            val accountLastname: String? = null

            @SerializedName("account_username")
            @Expose
            val accountUsername: String? = null

            @SerializedName("account_avatar")
            @Expose
            val accountAvatar: Any? = null

        }
    }

}
