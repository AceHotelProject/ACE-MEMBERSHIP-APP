package com.dicoding.membership.view.dashboard.promo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.core.utils.constants.UserRole
import com.dicoding.core.utils.constants.mapToUserRole
import com.dicoding.membership.databinding.FragmentPromoBinding
import com.dicoding.membership.view.dashboard.promo.detail.detailpromo.PromoDetailActivity
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PromoFragment : Fragment() {

    private var _binding: FragmentPromoBinding? = null
    private val viewModel: PromoViewModel by viewModels()
    private val binding get() = _binding!!

    private lateinit var ajuanPromoAdapter: PromoAdapter
    private lateinit var promoMitraAdapter: PromoAdapter

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

        setupRecyclerViews()

        observePromos()

//        setupRecyclerView()
//
//        lifecycleScope.launch {
//            promoViewModel.getStories("default_filter", false).collectLatest { pagingData ->
//                storyPagingAdapter.submitData(pagingData)
//            }
//        }
    }

    private fun setupRecyclerViews() {
        // Setup Ajuan Promo adapter
        ajuanPromoAdapter = PromoAdapter().apply {
            setOnItemClickCallback(object : PromoAdapter.OnItemClickCallback {
                override fun onItemClicked(data: PromoDomain) {
                    navigateToDetail(data, PROMO_SOURCE_AJUAN)
                }
            })
        }

        // Setup Promo Mitra adapter
        promoMitraAdapter = PromoAdapter().apply {
            setOnItemClickCallback(object : PromoAdapter.OnItemClickCallback {
                override fun onItemClicked(data: PromoDomain) {
                    navigateToDetail(data, PROMO_SOURCE_MITRA)
                }
            })
        }

        // Attach adapters to RecyclerViews
        binding.rvAjuanPromo.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,  // Set orientation to horizontal
                false
            )
            adapter = ajuanPromoAdapter
        }

        binding.rvPromoMitra.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = promoMitraAdapter
        }
    }

    private fun navigateToDetail(data: PromoDomain, source: String) {
        val intent = Intent(requireContext(), PromoDetailActivity::class.java).apply {
            putExtra(PromoDetailActivity.EXTRA_PROMO, data)
            putExtra(PromoDetailActivity.EXTRA_SOURCE, source)
        }
        startActivity(intent)
    }

    private fun observePromos() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Menjalankan dua proses asinkron secara paralel
            val proposalPromosDeferred = async {
                viewModel.getProposalPromos().observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Resource.Success -> {
                            hideLoading()
                            binding.tvAjuanPromo.visibility = View.VISIBLE
                            binding.tvPromoMitra.visibility = View.VISIBLE
                            result.data?.results?.let { promos ->
                                Log.d("PromoFragment", "Proposal Promos size: ${promos.size}")
                                ajuanPromoAdapter.submitList(promos)
                                Log.d("PromoFragment", "List submitted to adapter")
                            } ?: Log.d("PromoFragment", "Proposal Promos is null")
                        }
                        is Resource.Loading -> {
                            showLoading()
                            Log.d("PromoFragment", "Proposal Promos Loading")
                        }
                        is Resource.Error -> {
                            hideLoading()
                            showError(result.message)
                            Log.e("PromoFragment", "Error: ${result.message}")
                        }
                        else -> {
                            hideLoading()
                            showError("Terjadi kesalahan")
                            Log.e("PromoFragment", "Unknown error")
                        }
                    }
                }
            }

            val promosDeferred = async {
                viewModel.getPromos().collectLatest { pagingData ->
                    Log.d("PromoFragment", "Testing Paging")
                    // Handle states here (like loading, success, etc.)
                    promoMitraAdapter.submitData(pagingData)
                    // You can also observe the paging state here
                    promoMitraAdapter.addLoadStateListener { loadState ->
                        when (loadState.refresh) {
                            is LoadState.Loading -> {
                                showLoading() // Show loading spinner
                                Log.d("PromoFragment", "Paging Loading")
                            }
                            is LoadState.Error -> {
                                hideLoading()
                                showError((loadState.refresh as LoadState.Error).error.localizedMessage)
                                Log.e("PromoFragment", "Paging Error")
                            }
                            is LoadState.NotLoading -> {
                                hideLoading()
                                Log.d("PromoFragment", "Paging Not Loading")
                            }
                        }
                    }
                }
            }

            // Menunggu hingga kedua proses selesai
            proposalPromosDeferred.await()
            promosDeferred.await()
        }
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

            Log.d("PromoFragment", "Current User Role: ${mockUserRole.name}")

//            Use This For Real
//            setupFabVisibility(userRole)

            ajuanPromoAdapter.setUserRole(mockUserRole)
            promoMitraAdapter.setUserRole(mockUserRole)

            Log.d("PromoFragment", "Role set to adapters: ${mockUserRole.name}")
        }
    }

    private fun setupUserVisibility(userRole: UserRole) {
        when (userRole) {
            UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST -> {
                binding.rvPromoCategory.visibility = View.GONE
                binding.tvAjuanPromo.visibility = View.GONE
                binding.rvAjuanPromo.visibility = View.VISIBLE
                binding.tvPromoMitra.visibility = View.GONE
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

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        binding.apply {
            progressBar.visibility = View.GONE
        }
    }

    private fun showError(message: String?) {
        Toast.makeText(requireContext(), message ?: "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val PROMO_SOURCE_AJUAN = "ajuan_promo"
        const val PROMO_SOURCE_MITRA = "mitra_promo"
    }
}