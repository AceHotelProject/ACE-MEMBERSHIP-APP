package com.dicoding.membership.view.dashboard.admin.addmitra.addpegawai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.core.data.source.remote.response.merchants.CreateMerchantRequest
import com.dicoding.core.data.source.remote.response.merchants.MerchantData
import com.dicoding.core.data.source.remote.response.merchants.UserData
import com.dicoding.core.domain.auth.usecase.AuthUseCase
import com.dicoding.core.domain.merchants.usecase.MerchantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddPegawaiViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val merchantUseCase: MerchantUseCase
) : ViewModel() {

    fun getUser() = authUseCase.getUser().asLiveData()

    fun getRefreshToken() = authUseCase.getRefreshToken().asLiveData()

    fun updateMerchant(
        id: String,
        merchantData: MerchantData,
        ownerData: UserData,
        receptionistData: UserData
    ) = merchantUseCase.updateMerchant(
        id = id,
        request = CreateMerchantRequest(
            merchantData = merchantData,
            ownerData = ownerData,
            receptionistData = receptionistData
        )
    ).asLiveData()

    fun createMerchant(
        merchantData: MerchantData,
        ownerData: UserData,
        receptionistData: UserData
    ) = merchantUseCase.createMerchant(
        CreateMerchantRequest(
            merchantData = merchantData,
            ownerData = ownerData,
            receptionistData = receptionistData
        )
    ).asLiveData()
}