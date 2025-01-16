package com.dicoding.membership.view.dashboard.home.member.mreferral

import androidx.lifecycle.ViewModel
import com.dicoding.core.domain.user.usecase.UserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeMemberReferralViewModel @Inject constructor(
    private val userUseCase: UserUseCase
): ViewModel(){

}