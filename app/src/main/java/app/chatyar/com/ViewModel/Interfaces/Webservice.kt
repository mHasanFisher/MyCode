package app.chatyar.com.ViewModel.Interfaces

import app.chatyar.com.Model.Models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface Webservice {

    @FormUrlEncoded
    @POST("login")
    fun postLogin(
        @Field("username") username: String,
        @Field("pass") pass: String,
        @Field("platform") platform: String,
        @Field("version") version: String,
        @Field("fire_status") fire_status: String,
        @Field("firebase") firebase: String
    ): Call<LoginModel>


    @POST("mainpage")
    fun getMainPage(@Header("Authorization") token: String): Call<MainPageModel>

    @FormUrlEncoded
    @POST("profile/changepass")
    fun changePass(@Header("Authorization") token: String, @Field("oldpass") oldPass: String, @Field("newpass") newPass: String): Call<ChangePassResModel>

    @FormUrlEncoded
    @POST("getlink")
    fun getFileLink(@Field("file_id") fileId: String): Call<MainPageModel>

    @FormUrlEncoded
    @POST("clientInfo")
    fun getClientInfo(@Header("Authorization") token: String, @Field("ChatId") clientId: Int): Call<ClientInfoModel>

    @POST("logout")
    fun logout(@Header("Authorization") token: String): Call<MainPageModel>

    @Multipart
    @POST("https://panel.chatyar.com/apis/changeAvatar.php")
    fun uploadAvatar(@Part("auth") auth: RequestBody, @Part() image: MultipartBody.Part): Call<UploadAvatarResModel>


    @Multipart
    @POST("https://panel.chatyar.com/apis/fileUpload.php")
    fun uploadFile(@Part("auth") auth: RequestBody, @Part() fileToUpload: MultipartBody.Part): Call<UploadAvatarResModel>

}