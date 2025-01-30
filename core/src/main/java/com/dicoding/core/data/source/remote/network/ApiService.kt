package com.dicoding.core.data.source.remote.network

import com.dicoding.core.data.source.remote.response.auth.LoginResponse
import com.dicoding.core.data.source.remote.response.auth.OtpResponse
import com.dicoding.core.data.source.remote.response.auth.RegisterResponse
import com.dicoding.core.data.source.remote.response.membership.MembershipListResponse
import com.dicoding.core.data.source.remote.response.membership.MembershipResponse
import com.dicoding.core.data.source.remote.response.points.PointHistoryResponse
import com.dicoding.core.data.source.remote.response.points.PointHistoryResponseItem
import com.dicoding.core.data.source.remote.response.points.PointsResponse
import com.dicoding.core.data.source.remote.response.merchants.CreateMerchantRequest
import com.dicoding.core.data.source.remote.response.merchants.CreateMerchantResponse
import com.dicoding.core.data.source.remote.response.merchants.GetMerchantsByIdResponse
import com.dicoding.core.data.source.remote.response.merchants.GetMerchantsResponse
import com.dicoding.core.data.source.remote.response.merchants.UpdateMerchantResponse
import com.dicoding.core.data.source.remote.response.promo.ActivatePromoResepsionisResponse
import com.dicoding.core.data.source.remote.response.promo.ActivatePromoUserResponse
import com.dicoding.core.data.source.remote.response.promo.CreatePromoResponse
import com.dicoding.core.data.source.remote.response.promo.EditPromoRequest
import com.dicoding.core.data.source.remote.response.promo.EditPromoResponse
import com.dicoding.core.data.source.remote.response.promo.GetPromoHistoryResponse
import com.dicoding.core.data.source.remote.response.promo.GetPromoResponse
import com.dicoding.core.data.source.remote.response.test.DetailStoryResponse
import com.dicoding.core.data.source.remote.response.test.LoginTest
import com.dicoding.core.data.source.remote.response.test.RegisterTest
import com.dicoding.core.data.source.remote.response.test.StoryResponse
import com.dicoding.core.data.source.remote.response.user.UserListResponse
import com.dicoding.core.data.source.remote.response.user.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
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
        @Path("id")id: String
    ): DetailStoryResponse
/////////////////////////////////////////////////////////////////////////////////

    // Auth
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
    suspend fun sendOtp(): OtpResponse

    @POST("v1/auth/verify-otp")
    suspend fun verifyOtp(
        @Query("token") token: String
    ): Response<Unit>

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

    // User

    @POST("v1/users")
    @FormUrlEncoded
    suspend fun createUser(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("name") name: String,
        @Field("role") role: String = "member",
        @Field("member_type") memberType: String? = null,
        @Field("point") point: Int = 0,
        @Field("subscription_start_date") subscriptionStartDate: String? = null,
        @Field("subscription_end_date") subscriptionEndDate: String? = null
    ): UserResponse

    @GET("v1/users")
    suspend fun getAllUsersData(
        @Query("page") page: Int
    ): UserListResponse

    @GET("v1/users/{id}")
    suspend fun getUserData(
        @Path("id") id: String
    ): UserResponse

    @GET("v1/users/phone/{phone}")
    suspend fun getUserByPhone(
        @Path("phone") phone: String
    ): UserResponse

    @PATCH("v1/users/{id}")
    @FormUrlEncoded
    suspend fun updateUserData(
        @Path("id") id: String,
        @Field("pathKTP") idPicturePath: String? = null,
        @Field("name") name: String? = null,
        @Field("citizenNumber") citizenNumber: String? = null,
        @Field("phone") phone: String? = null,
        @Field("address") address: String? = null,
        @Field("memberType") memberType: String? = null
    ): UserResponse

    @PATCH("v1/users/{id}/complete-data")
    @FormUrlEncoded
    suspend fun completeUserData(
        @Path("id") id: String,
        @Field("name") name: String? = null,
        @Field("pathKTP") pathKTP: String? = null,
        @Field("citizenNumber") citizenNumber: String? = null,
        @Field("phone") phone: String? = null,
        @Field("address") address: String? = null,
        @Field("memberType") memberType: String? = null
    ): UserResponse

    @DELETE("v1/users/{id}")
    suspend fun deleteUser(
        @Path("id") id: String
    ): Unit

    // Non Member

    @PATCH("v1/subscriptions/{id}")
    suspend fun updateSubscriptionById(
        @Path("id") id: String,
        @Body updateData: Map<String, Any>
    )

    @POST("v1/subscriptions")
    @FormUrlEncoded
    suspend fun createMembership(
        @Field("type") type: String,
        @Field("duration") duration: Int,
        @Field("price") price: Int,
        @Field("tnc[]") tnc: List<String>
    ): MembershipResponse

    @GET("v1/subscriptions")
    suspend fun getAllMemberships(): MembershipListResponse

    @GET("v1/subscriptions/{id}")
    suspend fun getMembershipById(
        @Path("id") id: String
    ): MembershipResponse

    @PATCH("v1/subscriptions/{id}")
    @FormUrlEncoded
    suspend fun updateMembership(
        @Path("id") id: String,
        @Field("type") type: String? = null,
        @Field("duration") duration: Int? = null,
        @Field("price") price: Int? = null,
        @Field("tnc[]") tnc: List<String>? = null
    ): MembershipResponse

    @DELETE("v1/subscriptions/{id}")
    suspend fun deleteMembership(
        @Path("id") id: String
    ): Unit

    // Promo
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

    @PATCH("v1/promos/manage/{id}")
    suspend fun editPromo(
        @Path("id") id: String,
        @Body request: EditPromoRequest
    ): EditPromoResponse

    @DELETE("v1/promos/manage/{id}")
    suspend fun deletePromo(
        @Path("id") id: String
    ): Response<Unit>

    @POST("v1/promos/activate/{id}")
    suspend fun activatePromoResepsionis(
        @Path("id") id: String
    ): ActivatePromoResepsionisResponse

    @POST("v1/promos/activate-user/{id}")
    suspend fun activatePromoUser(
        @Path("id") id: String
    ): ActivatePromoUserResponse

    @POST("v1/promos/redeem/{token}")
    suspend fun redeemPromo(
        @Path("token") token: String,
    ): Response<Unit>

    @GET("v1/promos/history")
    suspend fun getPromoHistory(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): GetPromoHistoryResponse

    // Merchants
    @POST("v1/merchants")
    suspend fun createMerchant(
        @Body request: CreateMerchantRequest
    ): CreateMerchantResponse

    @GET("v1/merchants")
    suspend fun getMerchants(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): GetMerchantsResponse

    @GET("v1/merchants/{id}")
    suspend fun getMerchantById(
        @Path("id") id: String
    ): GetMerchantsByIdResponse

    @PATCH("v1/merchants/{id}")
    suspend fun updateMerchant(
        @Path("id") id: String,
        @Body request: CreateMerchantRequest
    ): UpdateMerchantResponse

    @DELETE("v1/merchants/{id}")
    suspend fun deleteMerchant(
        @Path("id") id: String
    ): Response<Unit>

    // Mitra

    // Discount and Promotion

    // Points

    @FormUrlEncoded
    @POST("v1/points/transfer")
    suspend fun pointsTransfer(
        @Field("to") to: String,
        @Field("from") from: String,
        @Field("amount") amount: Int,
        @Field("notes") notes: String
    ): PointHistoryResponseItem

    @GET("v1/points/{userId}")
    suspend fun getUserPoints(
        @Path("userId") userId: String
    ): PointsResponse

    @GET("v1/points/history/{userId}")
    suspend fun getUserHistory(
        @Path("userId") userId: String
    ): PointHistoryResponse
}