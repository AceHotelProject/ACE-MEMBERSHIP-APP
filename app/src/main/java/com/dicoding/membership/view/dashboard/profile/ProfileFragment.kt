package com.dicoding.membership.view.dashboard.profile

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dicoding.core.domain.auth.model.UserDomain
import com.dicoding.core.domain.membership.model.MembershipLocal
import com.dicoding.core.utils.constants.UserRole
import com.dicoding.core.utils.constants.mapToUserRole
import com.dicoding.membership.R
import com.dicoding.membership.databinding.FragmentProfileBinding
import com.dicoding.membership.view.dashboard.admin.manajemenmitra.ManajemenMitraActivity
import com.dicoding.membership.view.dashboard.home.member.mlevel.HomeMemberLevelActivity
import com.dicoding.membership.view.dashboard.profile.detail.detail.ProfileDetailActivity
import com.dicoding.membership.view.dashboard.profile.detail.membershipku.ProfileDetailMembershipkuActivity
import com.dicoding.membership.view.dashboard.profile.detail.poinku.ProfileDetailPoinkuActivity
import com.dicoding.membership.view.dashboard.profile.detail.referralku.ProfileDetailReferralkuActivity
import com.dicoding.membership.view.dialog.GlobalTwoButtonDialog
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import com.dicoding.membership.view.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

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


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeUserData()
        observeMembershipData()
        validateToken()

        checkUserRole()

        viewModel.getUserData()

    }

    private fun observeMembershipData() {
        viewModel.activeMembership.observe(viewLifecycleOwner) { membership ->
            if (membership != null) {
                updateMembershipCard(membership)
            } else {
                // Set default appearance when no membership data is available
                binding.membershipkuTier.text = "Gabung Member"
                binding.membershipkuExpiry.text = "Nikmati banyak promo menarik"
                binding.membershipCardLayout.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.orange_card_half
                )
            }
        }
    }

    private fun updateMembershipCard(membership: MembershipLocal) {
        // Update membership type/tier text
        binding.membershipkuTier.text = membership.type ?: "Gabung Member"

        // Update expiry date
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id"))
        val expiryDateText = membership.expiryDate?.let { "Exp ${dateFormat.format(it)}" }
            ?: "Nikmati banyak promo menarik"
        binding.membershipkuExpiry.text = expiryDateText

        // Load image for membership card background
        membership.image?.getOrNull(1)?.let { imageUrl ->
            Log.d("ProfileFragment", "Loading membership image: $imageUrl")

            Glide.with(requireContext())
                .load(imageUrl)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        binding.membershipCardLayout.background = resource

                        // Adjust height based on image aspect ratio if needed
                        val width = resource.intrinsicWidth
                        val height = resource.intrinsicHeight
                        val aspectRatio = height.toFloat() / width.toFloat()

                        binding.membershipCardLayout.post {
                            val layoutWidth = binding.membershipCardLayout.width
                            val newHeight = (layoutWidth * aspectRatio).toInt()

                            // Use the dimension resource instead of hardcoded value
                            val minHeight = resources.getDimensionPixelSize(R.dimen.min_card_height)
                            val params = binding.membershipCardLayout.layoutParams
                            params.height = Math.max(newHeight, minHeight)
                            binding.membershipCardLayout.layoutParams = params
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        binding.membershipCardLayout.background = ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.orange_card_half
                        )
                    }
                })
        } ?: run {
            // Fallback if no image is available
            binding.membershipCardLayout.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.orange_card_half
            )
        }

        // If you have coupon info in your membership data, update that too
        membership.remainingCoupons?.let { remainingCoupons ->
            binding.tvCouponCount.text = "$remainingCoupons kupon"
        }
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
                val intent = Intent(requireContext(), ProfileDetailMembershipkuActivity::class.java).apply {
                    putExtra(ProfileDetailMembershipkuActivity.EXTRA_USER_ID, loginDomain.user.id)
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
        binding.layoutManajemenMitra.setOnClickListener {
            viewModel.userData.value?.let {
                val intent = Intent(requireContext(), ManajemenMitraActivity::class.java)
                startActivity(intent)
            }
        }
        binding.layoutKeluar.setOnClickListener {
            GlobalTwoButtonDialog().apply {
                setDialogTitle("Keluar Aplikasi")
                setDialogMessage("Apakah Anda yakin ingin keluar dari akun Anda?")
                setOnYesClickListener {
                    handleLogout()
                }
                setOnNoClickListener {
                    // Optional: tambahkan action ketika user menekan tidak
                }
            }.show(requireActivity().supportFragmentManager, "ConfirmationDialog")
        }
        // Add this to your setupClickListeners method in ProfileFragment.kt
        binding.membershipCardView.setOnClickListener {
            // Check if active membership data exists in local database
            if (viewModel.activeMembership.value == null) {
                // No active membership in local database, redirect to membership plans
                viewModel.userData.value?.let { loginDomain ->
                    val intent = Intent(requireContext(), HomeMemberLevelActivity::class.java).apply {
                        putExtra(HomeMemberLevelActivity.EXTRA_USER_ID, loginDomain.user.id)
                    }
                    startActivity(intent)
                }
            }
        }
    }

    private fun handleLogout() {
        showLoading(true)

        // First clear membership data
        viewModel.clearMembershipData()

        viewModel.getUser().observe(viewLifecycleOwner) { user ->
            user?.let {
                viewModel.deleteUser(it)
                viewModel.deleteAllData()

                showLoading(false)

                val intent = Intent(requireActivity(), WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    private fun showLoading(boolean: Boolean) {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            loadingOverlay.visibility = View.VISIBLE
        }
    }

    private fun validateToken() {
        viewModel.getRefreshToken().observe(viewLifecycleOwner) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(parentFragmentManager, "Token Expired Dialog")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkUserRole() {
        viewModel.getUser().observe(viewLifecycleOwner) { loginDomain ->
            val userRole = mapToUserRole(loginDomain.user.role)

//            Testing
            val mockUserRole = UserRole.ADMIN
            //setupUserVisibility(userRole)

//            Use This For Real
//            setupFabVisibility(userRole)

            Log.d("HomeFragment", "User Role: ${userRole.display}")
        }
    }

    private fun setupUserVisibility(userRole: UserRole) {
        when (userRole) {
            UserRole.ADMIN -> {
                binding.membershipCardView.visibility = View.GONE
                binding.clGunakanPromo.visibility = View.GONE
                binding.layoutMembershipku.visibility = View.GONE
                binding.layoutManajemenMitra.visibility = View.VISIBLE
            }
            UserRole.MITRA, UserRole.RECEPTIONIST ->{
                binding.membershipCardView.visibility = View.GONE
                binding.clGunakanPromo.visibility = View.GONE
                binding.layoutMembershipku.visibility = View.GONE
                binding.layoutManajemenMitra.visibility = View.GONE
            }
            UserRole.MEMBER -> {
                binding.membershipCardView.visibility = View.VISIBLE
                binding.clGunakanPromo.visibility = View.VISIBLE
                binding.layoutMembershipku.visibility = View.VISIBLE
                binding.layoutManajemenMitra.visibility = View.GONE

                binding.lnBackgroundMenu.background = null
            }
            UserRole.NONMEMBER -> {
                binding.membershipCardView.visibility = View.VISIBLE
                binding.clGunakanPromo.visibility = View.VISIBLE
                binding.layoutMembershipku.visibility = View.VISIBLE
                binding.layoutManajemenMitra.visibility = View.GONE

                binding.lnBackgroundMenu.background = null
            }
            else -> {
                binding.membershipCardView.visibility = View.GONE
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

    private fun observeUserData() {
        viewModel.userData.observe(viewLifecycleOwner) { loginDomain ->
            updateUI(loginDomain.user)
        }
    }

    private fun updateUI(user: UserDomain) {
        // Show/hide admin specific layouts based on role
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}