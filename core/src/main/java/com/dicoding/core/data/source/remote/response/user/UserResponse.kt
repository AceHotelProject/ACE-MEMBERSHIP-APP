    package com.dicoding.core.data.source.remote.response.user

    import com.google.gson.Gson
    import com.google.gson.JsonElement
    import com.google.gson.annotations.SerializedName

    data class MerchantUserResponse(
        @SerializedName("point")
        val point: Int = 0,
        @SerializedName("refferalPoint")
        val refferalPoint: Int = 0,
        @SerializedName("id")
        val id: String = ""
    )

    sealed class MerchantIdResponse {
        data class MerchantData(
            val point: Int = 0,
            val refferalPoint: Int = 0,
            val id: String = ""
        ) : MerchantIdResponse()

        data class MerchantString(
            val id: String
        ) : MerchantIdResponse()
    }


    data class UserResponse(
        @SerializedName("id")
        val id: String? = null,

        @SerializedName("name")
        val name: String? = null,

        @SerializedName("email")
        val email: String? = null,

        @SerializedName("isEmailVerified")
        val isEmailVerified: Boolean = false,

        @SerializedName("isNumberVerified")
        val isNumberVerified: Boolean = false,

        @SerializedName("isPhoneVerified")
        val isPhoneVerified: Boolean = false,

        @SerializedName("isValidated")
        val isValidated: Boolean = false,

        @SerializedName("phone")
        val phone: String? = null,

        @SerializedName("address")
        val address: String? = null,

        @SerializedName("citizenNumber")
        val citizenNumber: String? = null,

        @SerializedName("pathKTP")
        val pathKTP: String? = null,

        @SerializedName("role")
        val role: String = "user",

        @SerializedName("merchantId")
        private val _merchantId: JsonElement? = null,

        @SerializedName("androidId")
        val androidId: String? = null,

        @SerializedName("memberType")
        val memberType: String? = null,

        @SerializedName("couponUsed")
        val couponUsed: List<String> = emptyList(),

        @SerializedName("point")
        val point: Int = 0,

        @SerializedName("refferalPoint")
        val refferalPoint: Int = 0,

        @SerializedName("subscriptionStartDate")
        val subscriptionStartDate: String? = null,

        @SerializedName("subscriptionEndDate")
        val subscriptionEndDate: String? = null,

        @SerializedName("createdAt")
        val createdAt: String? = null
    ){
        val merchantId: MerchantIdResponse?
            get() = when {
                _merchantId == null -> null
                _merchantId.isJsonObject -> {
                    try {
                        val merchantObj = Gson().fromJson(_merchantId, MerchantUserResponse::class.java)
                        MerchantIdResponse.MerchantData(
                            point = merchantObj.point,
                            refferalPoint = merchantObj.refferalPoint,
                            id = merchantObj.id
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                _merchantId.isJsonPrimitive -> MerchantIdResponse.MerchantString(
                    id = _merchantId.asString
                )
                else -> null
            }
    }

    data class UserListResponse(
        @SerializedName("results")
        val data: List<UserResponse> = emptyList(),

        @SerializedName("page")
        val page: Int = 1,

        @SerializedName("limit")
        val limit: Int = 10,

        @SerializedName("totalPages")
        val totalPages: Int = 1,

        @SerializedName("totalResults")
        val totalResults: Int = 0
    )