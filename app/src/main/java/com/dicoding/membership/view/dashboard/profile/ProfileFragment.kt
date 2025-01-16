package com.dicoding.membership.view.dashboard.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.auth.model.UserDomain
import com.dicoding.core.domain.user.model.User
import com.dicoding.membership.R
import com.dicoding.membership.core.utils.isInternetAvailable
import com.dicoding.membership.databinding.FragmentProfileBinding
import com.dicoding.membership.view.dashboard.home.member.mlevel.HomeMemberLevelActivity
import com.dicoding.membership.view.dashboard.profile.detail.detail.ProfileDetailActivity
import com.dicoding.membership.view.dashboard.profile.detail.poinku.ProfileDetailPoinkuActivity
import com.dicoding.membership.view.dashboard.profile.detail.referralku.ProfileDetailReferralkuActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeUserData()
        viewModel.getUserData()
    }

    private fun setupClickListeners() {
        binding.layoutProfilDiri.setOnClickListener {
            viewModel.userData.value?.let { loginDomain ->
                Log.d("ProfileFragment", "User ID: ${loginDomain.user.id}")
                val intent = Intent(requireContext(), ProfileDetailActivity::class.java).apply {
                    putExtra(ProfileDetailActivity.EXTRA_USER_ID, loginDomain.user.id)
                }
                startActivity(intent)
            }
        }
        binding.layoutReferralku.setOnClickListener {
            viewModel.userData.value?.let { loginDomain ->
                val intent = Intent(requireContext(), ProfileDetailReferralkuActivity::class.java).apply {
                    putExtra(ProfileDetailReferralkuActivity.EXTRA_USER_ID, loginDomain.user.id)
                }
                startActivity(intent)
            }
        }
        binding.layoutMembershipku.setOnClickListener {
            viewModel.userData.value?.let { loginDomain ->
                //use case untuk membership, untuk sementara ke arah join member
                val intent = Intent(requireContext(), HomeMemberLevelActivity::class.java).apply {
                    putExtra(HomeMemberLevelActivity.EXTRA_USER_ID, loginDomain.user.id)
                }
                startActivity(intent)
            }
        }
        binding.layoutPoinku.setOnClickListener {
            viewModel.userData.value?.let { loginDomain ->
                //use case untuk poinku, untuk sementara data passed = user id
                val intent = Intent(requireContext(), ProfileDetailPoinkuActivity::class.java). apply {
                    putExtra(ProfileDetailPoinkuActivity.EXTRA_USER_ID, loginDomain.user.id)
                }
                startActivity(intent)
            }
        }

    }

    private fun observeUserData() {
        viewModel.userData.observe(viewLifecycleOwner) { loginDomain ->
            updateUI(loginDomain.user)
        }
    }

    private fun updateUI(user: UserDomain) {
        with(binding) {

            // Show/hide admin specific layouts based on role

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}