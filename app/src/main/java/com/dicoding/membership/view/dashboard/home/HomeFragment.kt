package com.dicoding.membership.view.dashboard.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dicoding.core.utils.constants.UserRole
import com.dicoding.core.utils.constants.mapToUserRole
import com.dicoding.membership.databinding.FragmentHomeBinding
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validateToken()

        checkUserRole()
    }

    private fun validateToken() {
        homeViewModel.getRefreshToken().observe(viewLifecycleOwner) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(parentFragmentManager, "Token Expired Dialog")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkUserRole() {
        homeViewModel.getUser().observe(viewLifecycleOwner) { loginDomain ->
            val userRole = mapToUserRole(loginDomain.user.role)

//            Testing
            val mockUserRole = UserRole.ADMIN
            setupLinearVisibility(mockUserRole)

//            Use This For Real
//            setupFabVisibility(userRole)

            Log.d("HomeFragment", "User Role: ${userRole.display}")
        }
    }

    private fun setupLinearVisibility(userRole: UserRole) {
        when (userRole) {
            UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST -> {
                binding.linearLayoutMember.visibility = View.GONE
                binding.linearLayoutNonMember.visibility = View.GONE
            }
            UserRole.MEMBER -> {
                binding.linearLayoutMember.visibility = View.VISIBLE
                binding.linearLayoutNonMember.visibility = View.GONE
            }
            UserRole.NONMEMBER -> {
                binding.linearLayoutMember.visibility = View.GONE
                binding.linearLayoutNonMember.visibility = View.VISIBLE
            }
            else -> {
                binding.linearLayoutMember.visibility = View.GONE
                binding.linearLayoutNonMember.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}