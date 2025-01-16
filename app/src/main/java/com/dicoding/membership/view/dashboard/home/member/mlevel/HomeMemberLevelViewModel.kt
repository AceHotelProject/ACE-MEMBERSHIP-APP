package com.dicoding.membership.view.dashboard.home.member.mlevel

import androidx.lifecycle.ViewModel
import com.dicoding.core.domain.membership.usecase.MembershipUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeMemberLevelViewModel @Inject constructor (
    private val membershipUseCase: MembershipUseCase
): ViewModel(){

}