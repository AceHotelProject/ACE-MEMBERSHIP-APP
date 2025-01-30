package com.dicoding.membership.view.dashboard.history

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.dicoding.core.utils.constants.UserRole
import com.dicoding.core.utils.constants.mapToUserRole
import com.dicoding.membership.R
import com.dicoding.membership.databinding.FragmentHistoryBinding
import com.dicoding.membership.view.dashboard.history.historydetailpoin.pencarian.PencarianPoinActivity
import com.dicoding.membership.view.dashboard.history.historydetailriwayat.pencarian.PencarianMemberActivity
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val historyViewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validateToken()

        checkUserRole()

        setupButtonListeners()

        setupPageChangeCallback()

    }

    private fun validateToken() {
        historyViewModel.getRefreshToken().observe(viewLifecycleOwner) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(parentFragmentManager, "Token Expired Dialog")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkUserRole() {
        historyViewModel.getUser().observe(viewLifecycleOwner) { loginDomain ->
            val userRole = mapToUserRole(loginDomain.user.role)

//            Testing
            val mockUserRole = UserRole.ADMIN
            setupFabVisibility(mockUserRole)
            setupViewPager(mockUserRole)

//            Use This For Real
//            setupFabVisibility(userRole)

            Log.d("HomeFragment", "User Role: ${userRole.display}")
        }
    }

    private fun setupFabVisibility(userRole: UserRole) {
        when (userRole) {
            UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST -> {

            }
            UserRole.MEMBER, UserRole.NONMEMBER -> {

            }
            else -> {

            }
        }
    }

    private fun setupViewPager(userRole: UserRole) {
        val adapter = HistoryPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        // Setup visibility dan akses berdasarkan role
        when (userRole) {
            UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST -> {
                binding.btnPromo.visibility = View.VISIBLE
                binding.btnTransferPoin.visibility = View.VISIBLE
                binding.btnMember.visibility = View.VISIBLE
                // Default ke tab pertama
                binding.viewPager.currentItem = 0
            }
            UserRole.MEMBER, UserRole.NONMEMBER -> {
                binding.btnPromo.visibility = View.VISIBLE
                binding.btnTransferPoin.visibility = View.VISIBLE
                binding.btnMember.visibility = View.GONE
                // Member hanya bisa lihat promo
                binding.viewPager.currentItem = 0
                binding.viewPager.isUserInputEnabled = false // Disable swipe
            }
            else -> {
                binding.btnPromo.visibility = View.GONE
                binding.btnTransferPoin.visibility = View.GONE
                binding.btnMember.visibility = View.GONE
                binding.viewPager.visibility = View.GONE
            }
        }
    }

    private fun setupButtonListeners() {
        binding.btnPromo.setOnClickListener {
            onPromoButtonClick()
        }

        binding.btnTransferPoin.setOnClickListener {
            onTransferPoinButtonClick()
        }

        binding.btnMember.setOnClickListener {
            onMemberButtonClick()
        }
    }

    private fun onPromoButtonClick() {
        binding.viewPager.currentItem = 0
        updateButtonStates(binding.btnPromo, binding.btnTransferPoin, binding.btnMember)
    }

    private fun onTransferPoinButtonClick() {
        binding.viewPager.currentItem = 1
        updateButtonStates(binding.btnTransferPoin, binding.btnPromo, binding.btnMember)
    }

    private fun onMemberButtonClick() {
        binding.viewPager.currentItem = 2
        updateButtonStates(binding.btnMember, binding.btnPromo, binding.btnTransferPoin)
    }

    private fun updateButtonStates(activeButton: Button, vararg inactiveButtons: Button) {
        setButtonState(activeButton, true)
        inactiveButtons.forEach { setButtonState(it, false) }
    }

    private fun setupPageChangeCallback() {
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        updateButtonStates(binding.btnPromo, binding.btnTransferPoin, binding.btnMember)
                        binding.btnSearch.setOnClickListener {
                            // Navigate to promo search
                        }
                    }
                    1 -> {
                        updateButtonStates(binding.btnTransferPoin, binding.btnPromo, binding.btnMember)
                        binding.btnSearch.setOnClickListener {
                            // Navigate to poin search
                            val intent = Intent(requireContext(), PencarianPoinActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    2 -> {
                        updateButtonStates(binding.btnMember, binding.btnPromo, binding.btnTransferPoin)
                        binding.btnSearch.setOnClickListener {
                            // Navigate to member search
                            val intent = Intent(requireActivity(), PencarianMemberActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
        })
    }

    private fun setButtonState(button: Button, isActive: Boolean) {
        if (isActive) {
            button.setBackgroundResource(R.drawable.custom_button)
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        } else {
            button.setBackgroundResource(R.drawable.button_no_border)
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
