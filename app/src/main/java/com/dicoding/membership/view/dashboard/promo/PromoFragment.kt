package com.dicoding.membership.view.dashboard.promo

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
import com.dicoding.membership.databinding.FragmentPromoBinding
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PromoFragment : Fragment() {

    private var _binding: FragmentPromoBinding? = null
    private val binding get() = _binding!!

    private val promoViewModel: PromoViewModel by viewModels()
//    private lateinit var storyPagingAdapter: StoryPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPromoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validateToken()

        checkUserRole()

//        setupRecyclerView()
//
//        lifecycleScope.launch {
//            promoViewModel.getStories("default_filter", false).collectLatest { pagingData ->
//                storyPagingAdapter.submitData(pagingData)
//            }
//        }
    }

    private fun validateToken() {
        promoViewModel.getRefreshToken().observe(viewLifecycleOwner) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(parentFragmentManager, "Token Expired Dialog")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkUserRole() {
        promoViewModel.getUser().observe(viewLifecycleOwner) { loginDomain ->
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
            UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST -> {
                binding.rvPromoCategory.visibility = View.GONE
                binding.tvAjuanPromo.visibility = View.VISIBLE
                binding.rvAjuanPromo.visibility = View.VISIBLE
                binding.tvPromoMitra.visibility = View.VISIBLE
                binding.rvPromoMitra.visibility = View.VISIBLE
            }
            UserRole.MEMBER, UserRole.NONMEMBER -> {
                binding.rvPromoCategory.visibility = View.VISIBLE
                binding.tvAjuanPromo.visibility = View.GONE
                binding.rvAjuanPromo.visibility = View.GONE
                binding.tvPromoMitra.visibility = View.GONE
                binding.rvPromoMitra.visibility = View.VISIBLE
            }
            else -> {
                binding.rvPromoCategory.visibility = View.GONE
                binding.tvAjuanPromo.visibility = View.GONE
                binding.rvAjuanPromo.visibility = View.GONE
                binding.tvPromoMitra.visibility = View.GONE
                binding.rvPromoMitra.visibility = View.GONE
            }
        }
    }

//    private fun setupRecyclerView() {
//        storyPagingAdapter = StoryPagingAdapter()
//
//
//        storyPagingAdapter.setOnItemClickCallback(object : StoryPagingAdapter.OnItemClickCallback {
//            override fun onItemClicked(context: Context, story: StoryDomainTester) {
//            }
//        })
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}