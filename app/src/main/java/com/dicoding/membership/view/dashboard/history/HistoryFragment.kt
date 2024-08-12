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
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = HistoryPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        binding.btnPromo.setOnClickListener {
            binding.viewPager.currentItem = 0
            setButtonState(binding.btnPromo, true)
            setButtonState(binding.btnTransferPoin, false)
        }

        binding.btnTransferPoin.setOnClickListener {
            binding.viewPager.currentItem = 1
            setButtonState(binding.btnPromo, false)
            setButtonState(binding.btnTransferPoin, true)
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        setButtonState(binding.btnPromo, true)
                        setButtonState(binding.btnTransferPoin, false)
                    }
                    1 -> {
                        setButtonState(binding.btnPromo, false)
                        setButtonState(binding.btnTransferPoin, true)
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
