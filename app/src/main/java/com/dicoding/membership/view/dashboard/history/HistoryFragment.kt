package com.dicoding.membership.view.dashboard.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.dicoding.membership.R
import com.dicoding.membership.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()

        setupButtonListeners()

        setupPageChangeCallback()

    }

    private fun setupViewPager() {
        val adapter = HistoryPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter
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
                    0 -> updateButtonStates(binding.btnPromo, binding.btnTransferPoin, binding.btnMember)
                    1 -> updateButtonStates(binding.btnTransferPoin, binding.btnPromo, binding.btnMember)
                    2 -> updateButtonStates(binding.btnMember, binding.btnPromo, binding.btnTransferPoin)
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
