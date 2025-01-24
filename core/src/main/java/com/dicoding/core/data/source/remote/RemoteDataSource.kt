package com.dicoding.core.data.source.remote

import android.content.ContentValues.TAG
import android.util.Log
import com.dicoding.core.data.source.remote.network.ApiResponse
import com.dicoding.core.data.source.remote.network.ApiService
import com.dicoding.core.data.source.remote.response.auth.LoginResponse
import com.dicoding.core.data.source.remote.response.auth.OtpResponse
import com.dicoding.core.data.source.remote.response.auth.RegisterResponse
import com.dicoding.core.data.source.remote.response.membership.MembershipListResponse
import com.dicoding.core.data.source.remote.response.promo.ActivatePromoResponse
import com.dicoding.core.data.source.remote.response.promo.CreatePromoResponse
import com.dicoding.core.data.source.remote.response.promo.DeletePromoResponse
import com.dicoding.core.data.source.remote.response.promo.EditPromoRequest
import com.dicoding.core.data.source.remote.response.promo.EditPromoResponse
import com.dicoding.core.data.source.remote.response.promo.GetPromoHistoryResponse
import com.dicoding.core.data.source.remote.response.promo.GetPromoResponse
import com.dicoding.core.data.source.remote.response.promo.PromoHistoryItem
import com.dicoding.core.data.source.remote.response.promo.RedeemPromoResponse
import com.dicoding.core.data.source.remote.response.membership.MembershipResponse
import com.dicoding.core.data.source.remote.response.points.PointHistoryResponse
import com.dicoding.core.data.source.remote.response.points.PointHistoryResponseItem
import com.dicoding.core.data.source.remote.response.points.PointsResponse
import com.dicoding.core.data.source.remote.response.test.DetailStoryResponse
import com.dicoding.core.data.source.remote.response.test.LoginTest
import com.dicoding.core.data.source.remote.response.test.RegisterTest
import com.dicoding.core.data.source.remote.response.test.StoryResponse
import com.dicoding.core.data.source.remote.response.user.UserListResponse
import com.dicoding.core.data.source.remote.response.user.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(private val apiService: ApiService) {

    // Test
    suspend fun loginTester(email: String, password: String): Flow<ApiResponse<LoginTest>> {
        return flow {
            try {
                Log.d(TAG, "Login request: email=$email, password=$password")
                val response = apiService.loginTest(email, password)

                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e(TAG, "Login error: ${e.message}", e)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun registerTester(name: String, email: String, password: String): Flow<ApiResponse<RegisterTest>> {
        return flow {
            try {
                Log.d(TAG, "Register request: name=$name, email=$email, password=$password")
                val response = apiService.registerTest(name, email, password)

                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e(TAG, "Register error: ${e.message}", e)
            }
        }.flowOn(Dispatchers.IO)
    }
    // Test

    suspend fun getStories(page: Int, size: Int): StoryResponse {
        return apiService.getStories(page, size)
    }

    suspend fun getDetailStories(id: String): Flow<ApiResponse<DetailStoryResponse>> {
        return flow {
            try {
                val response = apiService.getDetailStories(id)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    ///////////////////////////////////////////////////////////////////////////////

    suspend fun login(email: String, password: String): Flow<ApiResponse<LoginResponse>> {
        return flow {
            try {
                Log.d(TAG, "Login attempt for email: $email")
                val response = apiService.login(email, password)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                Log.e(TAG, "Login failed: ${e.message}", e)
                emit(ApiResponse.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun register(email: String, password: String): Flow<ApiResponse<RegisterResponse>> {
        return flow {
            try {
                Log.d(TAG, "Register attempt for email: $email")
                val response = apiService.register(email, password)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                Log.e(TAG, "Register failed: ${e.message}", e)
                emit(ApiResponse.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }


    suspend fun createUser(
        email: String,
        password: String,
        name: String,
        role: String = "member",
        memberType: String? = null,
        point: Int = 0,
        subscriptionStartDate: String? = null,
        subscriptionEndDate: String? = null
    ): Flow<ApiResponse<UserResponse>> {
        return flow {
            try {
                val response = apiService.createUser(
                    email = email,
                    password = password,
                    name = name,
                    role = role,
                    memberType = memberType,
                    point = point,
                    subscriptionStartDate = subscriptionStartDate,
                    subscriptionEndDate = subscriptionEndDate
                )

                if (response.id != null) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Timber.tag("RemoteDataSource").e(e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getAllUsersData(page: Int): Flow<ApiResponse<UserListResponse>> {
        return flow {
            try {
                val response = apiService.getAllUsersData(page)
                if (response.data.isNotEmpty()) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Timber.tag("RemoteDataSource").e(e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getUserData(id: String): Flow<ApiResponse<UserResponse>> {
        return flow<ApiResponse<UserResponse>>{
            try {
                val response = apiService.getUserData(id)

                if(!response.id.isNullOrEmpty()) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Timber.tag("RemoteDataSource").e(e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getUserByPhone(phone: String): Flow<ApiResponse<UserResponse>> {
        return flow {
            try {
                val response = apiService.getUserByPhone(phone)
                if (response.id != null) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Timber.tag("RemoteDataSource").e(e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun updateUserData(
        id: String,
        idPicturePath: String? = null,
        name: String? = null,
        citizenNumber: String? = null,
        phone: String? = null,
        address: String? = null,
        memberType: String? = null
    ): Flow<ApiResponse<UserResponse>> {
        return flow {
            try {
                val response = apiService.updateUserData(
                    id = id,
                    idPicturePath = idPicturePath,
                    name = name,
                    citizenNumber = citizenNumber,
                    phone = phone,
                    address = address
                    //memberType under maintenance
                    //memberType = memberType
                )
                if (response.id != null) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Timber.tag("RemoteDataSource").e(e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun completeUserData(
        id: String,
        name: String? = null,
        pathKTP: String? = null,
        citizenNumber: String? = null,
        phone: String? = null,
        address: String? = null,
        memberType: String? = null
    ): Flow<ApiResponse<UserResponse>> {
        return flow {
            try {
                val response = apiService.completeUserData(
                    id = id,
                    name = name,
                    pathKTP = pathKTP,
                    citizenNumber = citizenNumber,
                    phone = phone,
                    address = address
                    //memberType under maintenance
                    //memberType = memberType
                )
                if (response.id != null) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Timber.tag("RemoteDataSource").e(e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun deleteUser(id: String): Flow<ApiResponse<Unit>> {
        return flow {
            try {
                val response = apiService.deleteUser(id)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Timber.tag("RemoteDataSource").e(e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    /////////////////////////////////////////////////////////////////////////////// MEMBERSHIP

    suspend fun createMembership(
        type: String,
        duration: Int,
        price: Int,
        tnc: List<String>
    ): Flow<ApiResponse<MembershipResponse>> {
        return flow {
            try {
                val response = apiService.createMembership(
                    type = type,
                    duration = duration,
                    price = price,
                    tnc = tnc
                )
                if (response.id != null) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Timber.tag("RemoteDataSource").e(e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getAllMemberships(): Flow<ApiResponse<MembershipListResponse>> {
        return flow {
            try {
                val response = apiService.getAllMemberships()
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Timber.tag("RemoteDataSource").e(e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getMembershipById(id: String): Flow<ApiResponse<MembershipResponse>> {
        return flow {
            try {
                val response = apiService.getMembershipById(id)
                if (response.id != null) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Timber.tag("RemoteDataSource").e(e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun updateMembership(
        id: String,
        type: String? = null,
        duration: Int? = null,
        price: Int? = null,
        tnc: List<String>? = null
    ): Flow<ApiResponse<MembershipResponse>> {
        return flow {
            try {
                val response = apiService.updateMembership(
                    id = id,
                    type = type,
                    duration = duration,
                    price = price,
                    tnc = tnc
                )
                if (response.id != null) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Timber.tag("RemoteDataSource").e(e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun deleteMembership(id: String): Flow<ApiResponse<Unit>> {
        return flow {
            try {
                val response = apiService.deleteMembership(id)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Timber.tag("RemoteDataSource").e(e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }


    ////////////////////////

    suspend fun sendOtp(id: String): Flow<ApiResponse<OtpResponse>> = flow {
        try {
            Log.d(TAG, "Sending OTP for id: $id")
            val response = apiService.sendOtp(id)
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send OTP for id: $id. Error: ${e.message}", e)
            emit(ApiResponse.Error(e.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun verifyOtp(id: String, token: Int): Flow<ApiResponse<OtpResponse>> = flow {
        try {
            Log.d(TAG, "Verifying OTP with id: $id and token: $token")
            val response = apiService.verifyOtp(id, token)
            emit(ApiResponse.Success(response))
        } catch (e: Exception) {
            Log.e(TAG, "OTP verification failed for token: $token. Error: ${e.message}", e)
            emit(ApiResponse.Error(e.toString()))
        }
    }.flowOn(Dispatchers.IO)

    /////////////////////////////////////////////////////////////////////////////// PROMO
    suspend fun createPromo(
        name: String,
        token: String,
        category: String,
        detail: String,
        pictures: List<String>,
        tnc: List<String>,
        startDate: String,
        endDate: String,
        memberType: String,
        merchant: String,
        maximalUse: Int,
        used: Int,
        isActive: Boolean
    ): Flow<ApiResponse<CreatePromoResponse>> {
        return flow {
            try {
                val response = apiService.createPromo(
                    name, token, category, detail, pictures, tnc,
                    startDate, endDate, memberType, merchant,
                    maximalUse, used, isActive
                )
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e(TAG, "Create promo error: ${e.message}", e)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getPromos(page: Int, limit: Int): Flow<ApiResponse<GetPromoResponse>> {
        return flow {
            try {
                val response = apiService.getPromos(page, limit)
                if (response.results?.isNotEmpty() == true) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e(TAG, "Get promos error: ${e.message}", e)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getProposalPromos(): Flow<ApiResponse<GetPromoResponse>> {
        return flow {
            try {
                val response = apiService.getProposalPromos()
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e(TAG, "Get proposal promos error: ${e.message}", e)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun editPromo(
        id: String,
        name: String,
        category: String,
        detail: String,
        pictures: List<String>,
        tnc: List<String>,
        startDate: String,
        endDate: String,
        memberType: String,
        maximalUse: Int,
        isActive: Boolean
    ): Flow<ApiResponse<EditPromoResponse>> {
        return flow {
            try {
                val request = EditPromoRequest(
                    name = name,
                    category = category,
                    detail = detail,
                    pictures = pictures,
                    tnc = tnc,
                    start_date = startDate,
                    end_date = endDate,
                    member_type = memberType,
                    maximal_use = maximalUse,
                    is_active = isActive
                )
                val response = apiService.editPromo(id, request)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e(TAG, "Edit promo error: ${e.message}", e)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun deletePromo(id: String): Flow<ApiResponse<Unit>> {
        return flow {
            try {
                val response = apiService.deletePromo(id)
                when (response.code()) {
                    204, 200 -> {  // Success codes for DELETE
                        Log.d(TAG, "Delete promo success with code: ${response.code()}")
                        emit(ApiResponse.Success(Unit))
                    }
                    400 -> {
                        Log.e(TAG, "Delete promo failed: Bad Request")
                        emit(ApiResponse.Error("Invalid request"))
                    }
                    404 -> {
                        Log.e(TAG, "Delete promo failed: Promo not found")
                        emit(ApiResponse.Error("Promo tidak ditemukan"))
                    }
                    else -> {
                        Log.e(TAG, "Delete promo failed with code: ${response.code()}")
                        emit(ApiResponse.Error("Gagal menghapus promo (${response.code()})"))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Delete promo error: ${e.message}", e)
                emit(ApiResponse.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun activatePromo(id: String): Flow<ApiResponse<ActivatePromoResponse>> {
        return flow {
            try {
                val response = apiService.activatePromo(id)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e(TAG, "Activate promo error: ${e.message}", e)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun redeemPromo(token: String): Flow<ApiResponse<Unit>> {
        return flow {
            try {
                val response = apiService.redeemPromo(token)
                when (response.code()) {
                    201, 200 -> {
                        Log.d(TAG, "Redeem promo success with code: ${response.code()}")
                        emit(ApiResponse.Success(Unit))
                    }
                    400 -> {
                        Log.e(TAG, "Redeem promo failed: Invalid code")
                        emit(ApiResponse.Error("Kode promo tidak valid"))
                    }
                    404 -> {
                        Log.e(TAG, "Redeem promo failed: Code not found")
                        emit(ApiResponse.Error("Kode promo tidak ditemukan"))
                    }
                    else -> {
                        Log.e(TAG, "Redeem promo failed with code: ${response.code()}")
                        emit(ApiResponse.Error("Gagal menggunakan kode promo (${response.code()})"))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Redeem promo error: ${e.message}", e)
                emit(ApiResponse.Error("Gagal menggunakan kode promo"))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getPromoHistory(page: Int, limit: Int): Flow<ApiResponse<GetPromoHistoryResponse>> {
        return flow {
            try {
                val response = apiService.getPromoHistory(page, limit)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e(TAG, "Get promo history error: ${e.message}", e)
            }
        }.flowOn(Dispatchers.IO)
    }

    /////////////////////////////////////////////////////////////////////////////// POINTS

    suspend fun transferPoints(
        to: String,
        from: String,
        amount: Int,
        notes: String
    ): Flow<ApiResponse<PointHistoryResponseItem>> {
        return flow {
            try {
                val response = apiService.pointsTransfer(
                    to = to,
                    from = from,
                    amount = amount,
                    notes = notes
                )
                if (response.id.isNotEmpty()) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Timber.tag("RemoteDataSource").e(e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getUserPoints(userId: String): Flow<ApiResponse<PointsResponse>> {
        return flow {
            try {
                val response = apiService.getUserPoints(userId)
                if (response.id != null) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Timber.tag("RemoteDataSource").e(e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getUserHistory(userId: String): Flow<ApiResponse<PointHistoryResponse>> {
        return flow {
            try {
                val response = apiService.getUserHistory(userId)
                if (response.listHistory.isNotEmpty()) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Timber.tag("RemoteDataSource").e(e.toString())
            }
        }.flowOn(Dispatchers.IO)
    }
}
