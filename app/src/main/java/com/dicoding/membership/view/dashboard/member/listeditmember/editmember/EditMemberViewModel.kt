package com.dicoding.membership.view.dashboard.member.listeditmember.editmember

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//changed the resource
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.membership.model.Membership
import com.dicoding.core.domain.membership.usecase.MembershipUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EditMemberViewModel @Inject constructor(
    private val membershipUseCase: MembershipUseCase
) : ViewModel() {

    private val _membershipState = MutableStateFlow<Resource<Membership>?>(null)
    val membershipState: StateFlow<Resource<Membership>?> = _membershipState.asStateFlow()

    fun createMembership(type: String, duration: Int, price: Int, tnc: List<String>) {
        viewModelScope.launch {
            _membershipState.value = Resource.Loading()
            membershipUseCase.createMembership(type, duration, price, tnc)
                .collect { result ->
                    _membershipState.value = result
                }
        }
    }

    fun updateMembership(id: String, type: String, duration: Int, price: Int, tnc: List<String>) {
        viewModelScope.launch {
            membershipUseCase.updateMembership(
                id = id,
                type = type,
                duration = duration,
                price = price,
                tnc = tnc
            ).collect { result ->
                _membershipState.value = result  // This is correct way to update StateFlow
            }
        }
    }
}