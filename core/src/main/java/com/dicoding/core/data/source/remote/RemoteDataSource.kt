package com.dicoding.core.data.source.remote

import android.content.ContentValues.TAG
import android.util.Log
import com.dicoding.core.data.source.remote.network.ApiResponse
import com.dicoding.core.data.source.remote.network.ApiService
import com.dicoding.core.data.source.remote.response.auth.LoginResponse
import com.dicoding.core.data.source.remote.response.auth.OtpResponse
import com.dicoding.core.data.source.remote.response.auth.RegisterResponse
import com.dicoding.core.data.source.remote.response.membership.MembershipListResponse
import com.dicoding.core.data.source.remote.response.promo.ActivatePromoResepsionisResponse
import com.dicoding.core.data.source.remote.response.promo.CreatePromoResponse
import com.dicoding.core.data.source.remote.response.promo.EditPromoRequest
import com.dicoding.core.data.source.remote.response.promo.EditPromoResponse
import com.dicoding.core.data.source.remote.response.promo.GetPromoHistoryResponse
import com.dicoding.core.data.source.remote.response.promo.GetPromoResponse
import com.dicoding.core.data.source.remote.response.file.FileUploadResponse
import com.dicoding.core.data.source.remote.response.membership.MembershipResponse
import com.dicoding.core.data.source.remote.response.points.PointHistoryResponse
import com.dicoding.core.data.source.remote.response.points.PointHistoryResponseItem
import com.dicoding.core.data.source.remote.response.points.PointsResponse
import com.dicoding.core.data.source.remote.response.merchants.CreateMerchantRequest
import com.dicoding.core.data.source.remote.response.merchants.CreateMerchantResponse
import com.dicoding.core.data.source.remote.response.merchants.GetMerchantsByIdResponse
import com.dicoding.core.data.source.remote.response.merchants.GetMerchantsResponse
import com.dicoding.core.data.source.remote.response.merchants.MerchantData
import com.dicoding.core.data.source.remote.response.merchants.UpdateMerchantResponse
import com.dicoding.core.data.source.remote.response.promo.ActivatePromoUserResponse
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
import okhttp3.MultipartBody
import retrofit2.HttpException
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

    suspend fun login(email: String, password: String, androidId: String): Flow<ApiResponse<LoginResponse>> {
        return flow {
            try {
                Log.d(TAG, "Login attempt for email: $email")
                val response = apiService.login(email, password, androidId)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                Log.e(TAG, "Login failed: ${e.message}", e)
                emit(ApiResponse.Error(e.toString()))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun register(email: String, password: String, androidId: String): Flow<ApiResponse<RegisterResponse>> {
        return flow {
            try {
                Log.d(TAG, "Register attempt for email: $email")
                val response = apiService.register(email, password, androidId)
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
        address: String? = null
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
        pathKTP: String? = null,
        citizenNumber: String? = null,
        phone: String? = null,
        address: String? = null
    ): Flow<ApiResponse<UserResponse>> {
        return flow {
            try {
                val response = apiService.completeUserData(
                    id = id,
                    pathKTP = pathKTP,
                    citizenNumber = citizenNumber,
                    phone = phone,
                    address = address
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

    suspend fun sendOtp(): Flow<ApiResponse<OtpResponse>> {
        return flow {
            try {
                val response = apiService.sendOtp()
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e(TAG, "Send OTP error: ${e.message}", e)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun verifyOtp(token: String): Flow<ApiResponse<Unit>> {
        return flow {
            try {
                val response = apiService.verifyOtp(token)
                when (response.code()) {
                    200, 201, 204 -> {
                        Log.d(TAG, "Verify OTP success with code: ${response.code()}")
                        emit(ApiResponse.Success(Unit))
                    }
                    400, 401 -> {
                        Log.e(TAG, "Verify OTP failed: Invalid token")
                        emit(ApiResponse.Error("Kode OTP tidak valid"))
                    }
                    404 -> {
                        Log.e(TAG, "Verify OTP failed: Token not found")
                        emit(ApiResponse.Error("Kode OTP tidak ditemukan"))
                    }
                    else -> {
                        Log.e(TAG, "Verify OTP failed with code: ${response.code()}")
                        emit(ApiResponse.Error("Gagal memverifikasi kode OTP (${response.code()})"))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Verify OTP error: ${e.message}", e)
                emit(ApiResponse.Error("Gagal memverifikasi kode OTP"))
            }
        }.flowOn(Dispatchers.IO)
    }

    /////////////////////////////////////////////////////////////////////////////// PROMO
    suspend fun createPromo(
        name: String,
        category: String,
        detail: String,
        pictures: List<String>,
        tnc: List<String>,
        startDate: String,
        endDate: String,
        memberType: String,
        maximalUse: Int,
    ): Flow<ApiResponse<CreatePromoResponse>> {
        return flow {
            try {
                val response = apiService.createPromo(
                    name, category, detail, pictures, tnc,
                    startDate, endDate, memberType,
                    maximalUse
                )
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e(TAG, "Create promo error: ${e.message}", e)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getPromos(
        page: Int,
        limit: Int,
        category: String = "",
        status: String = "",
        name: String = ""
    ): Flow<ApiResponse<GetPromoResponse>> {
        return flow {
            try {
                val queryMap = createQueryMap(page, limit, category, status, name)
                Log.d("RemoteDataSource", "Query Map: $queryMap")
                val response = apiService.getPromos(queryMap)
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

    private fun createQueryMap(
        page: Int,
        limit: Int,
        category: String,
        status: String,
        name: String
    ): Map<String, String> = buildMap {
        put("page", page.toString())
        put("limit", limit.toString())
        if (category.isNotEmpty()) put("category", category)
        if (status.isNotEmpty()) put("status", status)
        if (name.isNotEmpty()) put("name", name)
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

    suspend fun activatePromoResepsionis(id: String): Flow<ApiResponse<ActivatePromoResepsionisResponse>> {
        return flow {
            try {
                val response = apiService.activatePromoResepsionis(id)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e(TAG, "Activate promo error: ${e.message}", e)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun activatePromoUser(id: String): Flow<ApiResponse<ActivatePromoUserResponse>> {
        return flow {
            try {
                val response = apiService.activatePromoUser(id)
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

    suspend fun getPromoHistory(
        page: Int,
        limit: Int,
        promoName: String = "",
        promoCategory: String = "",
        status: String = ""
    ): Flow<ApiResponse<GetPromoHistoryResponse>> {
        return flow {
            try {
                val queryMap = createHistoryQueryMap(page, limit, promoName, promoCategory, status)
                val response = apiService.getPromoHistory(queryMap)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e(TAG, "Get promo history error: ${e.message}", e)
            }
        }.flowOn(Dispatchers.IO)
    }

    private fun createHistoryQueryMap(
        page: Int,
        limit: Int,
        promoName: String?,
        promoCategory: String?,
        status: String?
    ): Map<String, String> {
        return mutableMapOf<String, String>().apply {
            put("page", page.toString())
            put("limit", limit.toString())
            promoName?.takeIf { it.isNotEmpty() }?.let { put("promo_name", it) }
            promoCategory?.takeIf { it.isNotEmpty() }?.let { put("promo_category", it) }
            status?.takeIf { it.isNotEmpty() }?.let { put("status", it) }
        }
    }

    /////////////////////////////////////////////////////////////////////////////// Merchants
    suspend fun createMerchant(request: CreateMerchantRequest): Flow<ApiResponse<CreateMerchantResponse>> {
        return flow {
            try {
                val response = apiService.createMerchant(request)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                if (e is HttpException) {
                    val errorResponse = e.response()?.errorBody()?.string()
                    emit(ApiResponse.Error(errorResponse ?: e.toString()))
                } else {
                    emit(ApiResponse.Error(e.toString()))
                }
                Log.e(TAG, "Create merchant error: ${e.message}", e)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getMerchants(page: Int, limit: Int): Flow<ApiResponse<GetMerchantsResponse>> {
        return flow {
            try {
                val response = apiService.getMerchants(page, limit)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e(TAG, "Get merchants error: ${e.message}", e)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getMerchantById(id: String): Flow<ApiResponse<GetMerchantsByIdResponse>> {
        return flow {
            try {
                val response = apiService.getMerchantById(id)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e(TAG, "Get merchant by ID error: ${e.message}", e)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun updateMerchant(id: String, request: MerchantData): Flow<ApiResponse<UpdateMerchantResponse>> {
        return flow {
            try {
                val response = apiService.updateMerchant(id, request)
                emit(ApiResponse.Success(response))
            } catch (e: Exception) {
                if (e is HttpException) {
                    val errorResponse = e.response()?.errorBody()?.string()
                    emit(ApiResponse.Error(errorResponse ?: e.toString()))
                } else {
                    emit(ApiResponse.Error(e.toString()))
                }
                Log.e(TAG, "Update merchant error: ${e.message}", e)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun deleteMerchant(id: String): Flow<ApiResponse<Unit>> {
        return flow {
            try {
                val response = apiService.deleteMerchant(id)
                when (response.code()) {
                    204, 200 -> {
                        Log.d(TAG, "Delete merchant success with code: ${response.code()}")
                        emit(ApiResponse.Success(Unit))
                    }
                    400 -> {
                        Log.e(TAG, "Delete merchant failed: Bad Request")
                        emit(ApiResponse.Error("Permintaan tidak valid"))
                    }
                    404 -> {
                        Log.e(TAG, "Delete merchant failed: Merchant not found")
                        emit(ApiResponse.Error("Merchant tidak ditemukan"))
                    }
                    403 -> {
                        Log.e(TAG, "Delete merchant failed: Forbidden")
                        emit(ApiResponse.Error("Anda tidak memiliki akses untuk menghapus merchant ini"))
                    }
                    else -> {
                        Log.e(TAG, "Delete merchant failed with code: ${response.code()}")
                        emit(ApiResponse.Error("Gagal menghapus merchant (${response.code()})"))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Delete merchant error: ${e.message}", e)
                emit(ApiResponse.Error("Gagal menghapus merchant"))
            }
        }.flowOn(Dispatchers.IO)
    }

    /////////////////////////////////////////////////////////////////////////////// File
    suspend fun uploadFile(file: MultipartBody.Part): Flow<ApiResponse<FileUploadResponse>> {
        return flow {
            try {
                val response = apiService.uploadFile(file)
                if (response.isSuccessful && response.body() != null) {
                    val urls = response.body()!!
                    emit(ApiResponse.Success(FileUploadResponse(urls)))
                } else {
                    emit(ApiResponse.Error("Upload failed"))
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e(TAG, "Upload error: ${e.message}", e)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun deleteFile(fileId: String): Flow<ApiResponse<Boolean>> {
        return flow {
            try {
                val response = apiService.deleteFile(fileId)
                if (response.isSuccessful) {
                    emit(ApiResponse.Success(true))
                } else {
                    emit(ApiResponse.Error(response.message() ?: "Unknown error"))
                }
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.toString()))
                Log.e("FileRemoteDataSource", "Delete error: ${e.message}", e)
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
