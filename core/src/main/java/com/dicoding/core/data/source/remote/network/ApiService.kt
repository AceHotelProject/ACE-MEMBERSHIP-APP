package com.dicoding.core.data.source.remote.network

import com.dicoding.core.data.source.remote.response.auth.LoginResponse
import com.dicoding.core.data.source.remote.response.auth.RegisterResponse
import com.dicoding.core.data.source.remote.response.membership.MembershipListResponse
import com.dicoding.core.data.source.remote.response.membership.MembershipResponse
import com.dicoding.core.data.source.remote.response.membership.ValidateMembershipResponse
import com.dicoding.core.data.source.remote.response.points.PointHistoryListResponse
import com.dicoding.core.data.source.remote.response.points.PointsResponse
import com.dicoding.core.data.source.remote.response.test.DetailStoryResponse
import com.dicoding.core.data.source.remote.response.test.LoginTest
import com.dicoding.core.data.source.remote.response.test.RegisterTest
import com.dicoding.core.data.source.remote.response.test.StoryResponse
import com.dicoding.core.data.source.remote.response.user.UserListResponse
import com.dicoding.core.data.source.remote.response.user.UserResponse
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
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
    suspend fun getAllUsersData() : UserListResponse

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
        @Field("id_picture_path") idPicturePath: String? = null,
        @Field("name") name: String? = null,
        @Field("citizen_number") citizenNumber: String? = null,
        @Field("phone") phone: String? = null,
        @Field("address") address: String? = null
    ): UserResponse

    @DELETE("v1/users/{id}")
    suspend fun deleteUser(
        @Path("id") id: String
    ): Unit

    // Non Member

    // Membership

    @POST("v1/memberships")
    @FormUrlEncoded
    suspend fun createMembership(
        @Field("name") name: String,
        @Field("periode") periode: Int,
        @Field("price") price: Int,
        @Field("tnc") tnc: List<String>,
        @Field("discount") discount: Int
    ): MembershipResponse

    @GET("v1/memberships")
    suspend fun getAllMemberships(): MembershipListResponse

    @GET("v1/memberships/{id}")
    suspend fun getMembershipById(
        @Path("id") id: String
    ): MembershipResponse

    @PATCH("v1/memberships/{id}")
    @FormUrlEncoded
    suspend fun updateMembership(
        @Path("id") id: String,
        @Field("name") name: String? = null,
        @Field("periode") periode: Int? = null,
        @Field("price") price: Int? = null,
        @Field("tnc") tnc: List<String>? = null,
        @Field("discount") discount: Int? = null
    ): MembershipResponse

    @DELETE("v1/memberships/{id}")
    suspend fun deleteMembership(
        @Path("id") id: String
    ): Unit

    @POST("v1/memberships/validate/{userId}")
    @FormUrlEncoded
    suspend fun validateMembership(
        @Path("userId") userId: String,
        @Field("type") type: String,
        @Field("price") price: Int,
        @Field("start_date") startDate: String,
        @Field("end_date") endDate: String,
        @Field("status") status: String,
        @Field("proof_image_path") proofImagePath: String,
        @Field("verified_by") verifiedBy: String
    ): ValidateMembershipResponse

    // Referral Code

    // Mitra

    // Discount and Promotion

    // Poin

    @GET("v1/points/{userId}")
    suspend fun getUserPoints(
        @Path("userId") userId: String
    ): PointsResponse

    @GET("v1/points/history/{userId}")
    suspend fun getUserPointHistory(
        @Path("userId") userId: String
    ): PointHistoryListResponse

    @POST("v1/points/transfer/{userId}")
    @FormUrlEncoded
    suspend fun transferPoints(
        @Path("userId") userId: String,
        @Field("user_reciever_id") userReceiverId: String,
        @Field("amount") amount: Int,
        @Field("note") note: String,
        @Field("purpose") purpose: String
    ): Unit

}