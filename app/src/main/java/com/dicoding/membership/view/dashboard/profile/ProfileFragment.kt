package com.dicoding.membership.view.dashboard.profile

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.dicoding.core.utils.constants.UserRole
import com.dicoding.core.utils.constants.mapToUserRole
import com.dicoding.membership.R
import com.dicoding.membership.databinding.FragmentProfileBinding
import com.dicoding.membership.databinding.FragmentPromoBinding
import com.dicoding.membership.view.dashboard.promo.PromoViewModel
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validateToken()

        checkUserRole()
        
    }

    private fun validateToken() {
        profileViewModel.getRefreshToken().observe(viewLifecycleOwner) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(parentFragmentManager, "Token Expired Dialog")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkUserRole() {
        profileViewModel.getUser().observe(viewLifecycleOwner) { loginDomain ->
            val userRole = mapToUserRole(loginDomain.user.role)

//            Testing
            val mockUserRole = UserRole.ADMIN
            setupUserVisibility(mockUserRole)

//            Use This For Real
//            setupFabVisibility(userRole)

            Log.d("HomeFragment", "User Role: ${userRole.display}")
        }
    }

    private fun setupUserVisibility(userRole: UserRole) {
        when (userRole) {
            UserRole.ADMIN -> {
                binding.lnCardMemberCategory.visibility = View.GONE
                binding.clGunakanPromo.visibility = View.GONE
                binding.layoutMembershipku.visibility = View.GONE
                binding.layoutManajemenMitra.visibility = View.VISIBLE
            }
            UserRole.MITRA, UserRole.RECEPTIONIST ->{
                binding.lnCardMemberCategory.visibility = View.GONE
                binding.clGunakanPromo.visibility = View.GONE
                binding.layoutMembershipku.visibility = View.GONE
                binding.layoutManajemenMitra.visibility = View.GONE
            }
            UserRole.MEMBER -> {
                binding.lnCardMemberCategory.visibility = View.VISIBLE
                binding.clGunakanPromo.visibility = View.VISIBLE
                binding.layoutMembershipku.visibility = View.VISIBLE
                binding.layoutManajemenMitra.visibility = View.GONE

                binding.lnBackgroundMenu.background = null
            }
            UserRole.NONMEMBER -> {
                binding.lnCardMemberCategory.visibility = View.VISIBLE
                binding.tvCardCategoryMember.visibility = View.GONE
                binding.tvExpiryDate.visibility = View.GONE
                binding.clGunakanPromo.visibility = View.VISIBLE
                binding.layoutMembershipku.visibility = View.VISIBLE
                binding.layoutManajemenMitra.visibility = View.GONE

                binding.lnCardMemberCategory.setBackgroundResource(R.drawable.background_big_non_member)
                binding.lnBackgroundMenu.background = null
            }
            else -> {
                binding.lnCardMemberCategory.visibility = View.GONE
                binding.clGunakanPromo.visibility = View.GONE
                binding.layoutProfilDiri.visibility = View.GONE
                binding.layoutMembershipku.visibility = View.GONE
                binding.layoutPoinku.visibility = View.GONE
                binding.layoutManajemenMitra.visibility = View.GONE
                binding.layoutReferralku.visibility = View.GONE
                binding.layoutCustomerService.visibility = View.GONE
                binding.layoutKeluar.visibility = View.GONE
            }
        }
    }
}