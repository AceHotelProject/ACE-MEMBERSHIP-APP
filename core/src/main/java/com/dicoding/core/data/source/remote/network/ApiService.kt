package com.dicoding.core.data.source.remote.network

import com.dicoding.core.data.source.remote.response.auth.LoginResponse
import com.dicoding.core.data.source.remote.response.auth.OtpResponse
import com.dicoding.core.data.source.remote.response.auth.RegisterResponse
import com.dicoding.core.data.source.remote.response.promo.ActivatePromoResponse
import com.dicoding.core.data.source.remote.response.promo.CreatePromoResponse
import com.dicoding.core.data.source.remote.response.promo.DeletePromoResponse
import com.dicoding.core.data.source.remote.response.promo.EditPromoResponse
import com.dicoding.core.data.source.remote.response.promo.GetPromoHistoryResponse
import com.dicoding.core.data.source.remote.response.promo.GetPromoResponse
import com.dicoding.core.data.source.remote.response.promo.PromoHistoryItem
import com.dicoding.core.data.source.remote.response.promo.RedeemPromoResponse
import com.dicoding.core.data.source.remote.response.test.DetailStoryResponse
import com.dicoding.core.data.source.remote.response.test.LoginTest
import com.dicoding.core.data.source.remote.response.test.RegisterTest
import com.dicoding.core.data.source.remote.response.test.StoryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Tester
    @FormUrlEncoded
    @POST("register")
    suspend fun registerTest(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterTest

    @FormUrlEncoded
    @POST("login")
    suspend fun loginTest(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginTest

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): StoryResponse

    @GET("stories/{id}")
    suspend fun getDetailStories(
        @Path("id") id: String
    ): DetailStoryResponse
/////////////////////////////////////////////////////////////////////////////////

    // Lengkapi response terlebih dahulu

    ////////////////////////////////////////////// Auth
    @FormUrlEncoded
    @POST("v1/auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("v1/auth/register")
    suspend fun register(
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse


    @POST("v1/auth/send-otp")
    suspend fun sendOtp(
        @Field("id") id: String
    ):OtpResponse

    @POST("v1/auth/verify-otp")
    suspend fun verifyOtp(
        @Field("id") id: String,
        @Query("token") token: Int
    ):OtpResponse

    ////////////////////////////////////////////// User
    @FormUrlEncoded
    @POST("v1/auth/create-user")
    suspend fun createNewUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("phone") phone: String,
        @Field("address") address: String,
        @Field("citizenNumber") citizenNumber: String,
        @Field("pathKTP") pathKTP: String,
        @Field("androidId") androidId: String
    )

//    @GET("v1/users")
//    suspend fun getAllUsers(): List<>

    @GET("v1/users/{id}")
    suspend fun getUserById(
        @Path("id") userId: String
    )

    @FormUrlEncoded
    @PATCH("v1/users/{id}")
    suspend fun updateUserById(
        @Path("id") userId: String,
        @Field("name") name: String
    )

    @DELETE("v1/users/{id}")
    suspend fun deleteUserById(
        @Path("id") userId: String
    )

    @FormUrlEncoded
    @PATCH("v1/users/{userId}/complete-data")
    suspend fun completeUserData(
        @Path("userId") userId: String,
        @Field("pathKTP") pathKTP: String,
        @Field("citizenNumber") citizenNumber: String,
        @Field("phone") phone: String,
        @Field("address") address: String
    )

    ////////////////////////////////////////////// Token
    @GET("api/users/{userId}")
    suspend fun getSetoranByUserId(
        @Path("userId") userId: String
    )

    ////////////////////////////////////////////// Harga
    @FormUrlEncoded
    @POST("v1/harga")
    suspend fun postHarga(
        @Field("klasifikasi") klasifikasi: String,
        @Field("harga") harga: Int
    )

    @FormUrlEncoded
    @PUT("v1/harga/{id}")
    suspend fun updateHarga(
        @Path("id") id: String,
        @Field("harga") harga: Int
    )

//    @GET("v1/harga")
//    suspend fun getAllHarga(): List<HargaResponse>

    @GET("v1/harga")
    suspend fun getHargaByKlasifikasi(
        @Query("klasifikasi") klasifikasi: String
    )

    ////////////////////////////////////////////// Subscription
    @FormUrlEncoded
    @POST("v1/subscriptions")
    suspend fun createNewSubscription(
        @Field("type") type: String,
        @Field("duration") duration: Int,
        @Field("price") price: Int,
        @Field("tnc") tnc: List<String>
    )

//    @GET("v1/subscriptions")
//    suspend fun getAllSubscriptions(): List<Subscription>

    @GET("v1/subscriptions/{id}")
    suspend fun getSubscriptionById(
        @Path("id") id: String
    )

    @PATCH("v1/subscriptions/{id}")
    suspend fun updateSubscriptionById(
        @Path("id") id: String,
        @Body updateData: Map<String, Any>
    )

    ////////////////////////////////////////////// Promo
    @FormUrlEncoded
    @POST("v1/promos")
    suspend fun createPromo(
        @Field("name") name: String,
        @Field("token") token: String,
        @Field("category") category: String,
        @Field("detail") detail: String,
        @Field("pictures") pictures: List<String>,
        @Field("tnc") tnc: List<String>,
        @Field("start_date") startDate: String,
        @Field("end_date") endDate: String,
        @Field("member_type") memberType: String,
        @Field("merchant") merchant: String,
        @Field("maximal_use") maximalUse: Int,
        @Field("used") used: Int,
        @Field("is_active") isActive: Boolean
    ): CreatePromoResponse

    @GET("v1/promos")
    suspend fun getPromos(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): GetPromoResponse

    @GET("v1/promos")
    suspend fun getProposalPromos(): GetPromoResponse

    @FormUrlEncoded
    @PATCH("v1/promos/manage/{id}")
    suspend fun editPromo(
        @Path("id") id: String,
        @Header("Authorization") token: String,
        @Field("name") name: String? = null,
        @Field("category") category: String? = null,
        @Field("detail") detail: String? = null,
        @Field("pictures") pictures: List<String>? = null,
        @Field("tnc") tnc: List<String>? = null,
        @Field("start_date") startDate: String? = null,
        @Field("end_date") endDate: String? = null,
        @Field("member_type") memberType: String? = null,
        @Field("maximal_use") maximalUse: Int? = null,
        @Field("is_active") isActive: Boolean? = null
    ): EditPromoResponse

    @DELETE("v1/promos/manage/{id}")
    suspend fun deletePromo(
        @Path("id") id: String
    ): Response<Unit>

    @POST("v1/promos/activate/{id}")
    suspend fun activatePromo(
        @Path("id") id: String
    ): ActivatePromoResponse

    @POST("v1/promos/redeem/{token}")
    suspend fun redeemPromo(
        @Path("token") token: String,
    ): Response<Unit>

    @GET("v1/promos/history")
    suspend fun getPromoHistory(): List<PromoHistoryItem>
    ////////////////////////////////////////////// Merchant

}