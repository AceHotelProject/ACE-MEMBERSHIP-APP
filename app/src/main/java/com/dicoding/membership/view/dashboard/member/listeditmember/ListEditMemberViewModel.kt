package com.dicoding.membership.view.dashboard.member.listeditmember

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.remote.response.membership.MembershipListResponse
import com.dicoding.core.domain.membership.model.Membership
import com.dicoding.core.domain.membership.usecase.MembershipUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListEditMemberViewModel @Inject constructor(
    private val membershipUseCase: MembershipUseCase
) : ViewModel() {

    private val _memberships = MutableLiveData<Resource<MembershipListResponse>>()
    val memberships: LiveData<Resource<MembershipListResponse>> = _memberships

    fun deleteMembership(id: String) {
        viewModelScope.launch {
            membershipUseCase.deleteMembership(id)
                .onStart {
                    //Log.d("ViewModelDeleteDebug","Loading..")
                }
                .catch { _ ->
                    //Log.d("ViewModelDeleteDebug","In resource error")
                }
                .collect { _ ->
                    Log.d("ViewModelDeleteDebug", "Success result")
                    kotlinx.coroutines.delay(200)
                    getMemberships()
                }
        }
    }

    fun getMemberships() {
        viewModelScope.launch {
            Log.d("Debug", "ViewModel: getMemberships called")
            membershipUseCase.getAllMemberships()
                .collect { result ->
                    Log.d("Debug", "ViewModel: received result $result")
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { response ->
                                // For now it is sorted with the cheapest to more expensive
                                val sortedResults = response.results.sortedBy { it.price ?: Int.MAX_VALUE }

                                val sortedResponse = response.copy(results = sortedResults)
                                _memberships.value = Resource.Success(sortedResponse)
                            } ?: run {
                                _memberships.value = result
                            }
                        }
                        else -> _memberships.value = result
                    }
                }
        }
    }
}