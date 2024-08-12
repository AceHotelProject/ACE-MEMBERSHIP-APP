package com.dicoding.core.data.source.remote.network

import com.dicoding.core.data.source.remote.response.test.DetailStoryResponse
import com.dicoding.core.data.source.remote.response.test.LoginTest
import com.dicoding.core.data.source.remote.response.test.RegisterTest
import com.dicoding.core.data.source.remote.response.test.StoryResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
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


    // Non Member

    // Membership

    // Referral Code

    // Mitra

    // Discount and Promotion

    // Poin

}