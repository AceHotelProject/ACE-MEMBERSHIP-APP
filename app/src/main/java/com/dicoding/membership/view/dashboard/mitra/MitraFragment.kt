package com.dicoding.membership.view.dashboard.mitra

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dicoding.membership.databinding.FragmentMitraBinding
import com.dicoding.membership.view.dashboard.MainActivity
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MitraFragment : Fragment() {

    private var _binding: FragmentMitraBinding? = null
    private val binding get() = _binding!!

    private val mitraViewModel: MitraViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMitraBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validateToken()

        handleMenuButton()
    }

    private fun validateToken() {
        mitraViewModel.getRefreshToken().observe(viewLifecycleOwner) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(parentFragmentManager, "Token Expired Dialog")
            }
        }
    }

    private fun handleMenuButton() {
        binding.tvStatistik.setOnClickListener {
//            val intent = Intent(requireActivity(), StatistikActivity::class.java)
//            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}