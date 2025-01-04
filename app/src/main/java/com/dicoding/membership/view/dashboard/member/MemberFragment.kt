package com.dicoding.membership.view.dashboard.member

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.dicoding.membership.R
import com.dicoding.membership.databinding.FragmentMemberBinding
import com.dicoding.membership.databinding.FragmentMitraBinding
import com.dicoding.membership.view.dashboard.member.listeditmember.ListEditMemberActivity
import com.dicoding.membership.view.dashboard.mitra.MitraViewModel
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MemberFragment : Fragment() {

    private var _binding: FragmentMemberBinding? = null
    private val binding get() = _binding!!

    private val memberViewModel: MemberViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validateToken()

        handleMenuButton()
    }

    private fun validateToken() {
        memberViewModel.getRefreshToken().observe(viewLifecycleOwner) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(parentFragmentManager, "Token Expired Dialog")
            }
        }
    }

    private fun handleMenuButton() {
        binding.layoutEditMembership.setOnClickListener {
            val intent = Intent(requireActivity(), ListEditMemberActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}