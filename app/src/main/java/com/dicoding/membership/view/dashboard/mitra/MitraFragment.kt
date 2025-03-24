package com.dicoding.membership.view.dashboard.mitra

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.core.data.source.Resource
import com.dicoding.core.utils.constants.UserRole
import com.dicoding.core.utils.constants.mapToUserRole
import com.dicoding.membership.R
import com.dicoding.membership.databinding.FragmentMitraBinding
import com.dicoding.membership.view.dashboard.admin.addmitra.AddMitraActivity
import com.dicoding.membership.view.dashboard.admin.manajemenmitra.MerchantPagingAdapter
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MitraFragment : Fragment() {

    private var _binding: FragmentMitraBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MitraViewModel by viewModels()

    private lateinit var dots: Array<ImageView>
    private lateinit var imageAdapter: MitraImageAdapter

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

        checkUserRole()

        setupImageAdapter()

        handleMenuButton()

        setupSwipeRefresh()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkUserRole() {
        viewModel.getUser().observe(viewLifecycleOwner) { loginDomain ->
            val userRole = mapToUserRole(loginDomain.user.role)

            Log.d("MitraFragment", "User Data: ${loginDomain.user.role}")
            Log.d("MitraFragment", "Merchant ID Data: ${loginDomain.user.merchantId}")

////            Testing
//            val mockUserRole = UserRole.RECEPTIONIST
//            setupUserLogic(mockUserRole)

//            True
            val finalUserRole = when (userRole) {
                UserRole.USER -> {
                    // If the role is USER, check isMember status
                    if (loginDomain.user.isMember) {
                        UserRole.MEMBER
                    } else {
                        UserRole.NONMEMBER
                    }
                }
                // For other roles, keep them as is
                UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST -> userRole
                else -> userRole // Handle any other cases
            }

            setupUserLogic(finalUserRole)

            Log.d("MitraFragment", "Current User Role: ${finalUserRole.name}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupUserLogic(userRole: UserRole) {
        when (userRole) {
            UserRole.USER, UserRole.MEMBER, UserRole.NONMEMBER -> {
                Log.d("MitraFragment", "User is ${userRole.name}, skipping merchant data loading")
            }
            UserRole.RECEPTIONIST, UserRole.MITRA -> {
                viewModel.getUser().observe(viewLifecycleOwner) { loginDomain ->
                    if (loginDomain.user.merchantId?.id.isNullOrEmpty()) {
                        Log.e("MitraFragment", "Merchant ID not found")
                    } else {
                        loadMerchantData(loginDomain.user.merchantId?.id.toString())
                    }
                }
            }
            UserRole.ADMIN -> {
                validateTokenAndProceed()
            }
            else -> {
                Log.e("MitraFragment", "Invalid user role")
            }
        }
    }

    private fun setupImageAdapter() {
        imageAdapter = MitraImageAdapter(requireContext())

        binding.apply {

            rvMitraSelected.adapter = imageAdapter
            rvMitraSelected.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(rvMitraSelected)

            rvMitraSelected.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                    if (position != -1) {
                        updateDotIndicator(position)
                    }
                }
            })
        }
    }

    private fun handleMenuButton() {
        binding.tvStatistik.setOnClickListener {
//            val intent = Intent(requireActivity(), StatistikActivity::class.java)
//            startActivity(intent)
        }
    }

    private fun validateTokenAndProceed() {
        viewModel.getRefreshToken().observe(viewLifecycleOwner) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(parentFragmentManager, "Token Expired Dialog")
            } else {
                checkSavedMerchant()
            }
        }
    }

    private fun checkSavedMerchant() {
        viewModel.getMerchantId().observe(viewLifecycleOwner) { savedId ->
            if (savedId.isEmpty()) {
                getFirstMerchant()
            } else {
                loadMerchantData(savedId)
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            // Dapatkan ID merchant yang telah disimpan atau sedang aktif
            viewModel.getMerchantId().observe(viewLifecycleOwner) { merchantId ->
                if (merchantId.isNotEmpty()) {
                    loadMerchantData(merchantId)
                } else {
                    binding.swipeRefreshLayout.isRefreshing = false
                    // Jika tidak ada merchant ID yang tersimpan, refresh dengan mendapatkan merchant pertama
                    getFirstMerchant()
                }
            }
        }

        // Kustomisasi warna loading indicator jika diperlukan
        binding.swipeRefreshLayout.setColorSchemeResources(
            R.color.orange_100,
            android.R.color.holo_green_light,
            android.R.color.holo_blue_light
        )
    }

    private fun getFirstMerchant() {
        lifecycleScope.launch {
            Log.d("MitraFragment", "Starting getFirstMerchant")
            val tempAdapter = MerchantPagingAdapter()
            var isDataEmpty = true

            // Launch untuk loadState
            lifecycleScope.launch {
                tempAdapter.loadStateFlow.collect { loadState ->
                    Log.d("MitraFragment", "Load state: ${loadState.refresh}")
                    when (loadState.refresh) {
                        is LoadState.NotLoading -> {
                            val firstMerchant = tempAdapter.snapshot().firstOrNull()
                            if (firstMerchant != null) {
                                isDataEmpty = false
                                Log.d("MitraFragment", "Found first merchant: ${firstMerchant.id}")
                                viewModel.saveMerchantId(firstMerchant.id).observe(viewLifecycleOwner) { saved ->
                                    if (saved) loadMerchantData(firstMerchant.id)
                                }
                            } else {
                                Log.d("MitraFragment", "No merchant data found, navigating to AddMitraActivity")
                                navigateToAddMitra()
                            }
                        }
                        is LoadState.Error -> {
                            val error = (loadState.refresh as LoadState.Error).error
                            Log.e("MitraFragment", "Error loading merchants: ${error.message}")

                            // Instead of navigating, show a toast message
                            val errorMessage = if (error is retrofit2.HttpException && error.code() == 401) {
                                "Authentication failed. Please login again."
                            } else {
                                "Failed to load merchant data: ${error.message ?: "Unknown error"}"
                            }
                            showErrorMessage(errorMessage)
                        }
                        else -> {
                            // Loading state, do nothing
                        }
                    }
                }
            }

            // Launch untuk paging data
            viewModel.getMerchants().collect { pagingData ->
                Log.d("MitraFragment", "Submitting paging data")
                tempAdapter.submitData(pagingData)
            }
        }
    }

    private fun showErrorMessage(message: String) {
        context?.let {
            android.widget.Toast.makeText(it, message, android.widget.Toast.LENGTH_LONG).show()
        }
    }

    private fun navigateToAddMitra() {
        val intent = Intent(requireActivity(), AddMitraActivity::class.java)
        startActivity(intent)
        // Optional: finish current activity if you don't want to keep it in the back stack
        // requireActivity().finish()
    }

    private fun loadMerchantData(merchantId: String) {
        Log.d("MitraFragment", "Loading merchant data for ID: $merchantId")

        // Tampilkan loading terlebih dahulu
        showLoading()

        // Gunakan collection untuk memantau status loading kedua request
        val loadingStates = mutableMapOf(
            "merchantData" to true,
            "statistics" to true
        )

        // Fungsi untuk memeriksa apakah semua loading telah selesai
        fun checkAllLoaded() {
            if (loadingStates.values.none { it }) {
                hideLoading()
            }
        }

        viewModel.getMerchantsById(merchantId).observe(viewLifecycleOwner) { result ->
            when(result) {
                is Resource.Success -> {
                    loadingStates["merchantData"] = false
                    result.data?.let { merchant ->
                        binding.apply {
                            Log.d("MitraFragment", "Successfully loaded merchant: ${result.data?.name}")
                            if (merchant.picturesUrl.isNotEmpty()) {
                                imageAdapter.submitList(merchant.picturesUrl)
                                setupDotIndicators(merchant.picturesUrl.size)
                            }
                            tvMitraType.text = merchant.merchantType
                            tvMitraName.text = merchant.name
                            tvMitraDescription.text = merchant.detail
                            // Update other UI elements
                        }
                    }
                    checkAllLoaded()
                }
                is Resource.Loading -> {
                    loadingStates["merchantData"] = true
                    showLoading()
                }
                is Resource.Error -> {
                    loadingStates["merchantData"] = false
                    Log.e("MitraFragment", "Failed to load merchant: ${result.message}")
                    checkAllLoaded()
                }
                else -> {
                    loadingStates["merchantData"] = false
                    checkAllLoaded()
                }
            }
        }

        viewModel.getMerchantStatistic(merchantId).observe(viewLifecycleOwner) { result ->
            when(result) {
                is Resource.Success -> {
                    loadingStates["statistics"] = false
                    result.data?.let { statistic ->
                        binding.apply {
                            Log.d("MitraFragment", "Successfully loaded merchant statistics")
                            tvMitraPromo.text = statistic.totalPromo.toString()
                            tvMitraPromoUse.text = statistic.totalPromoUsed.toString()
                            tvMitraPoin.text = statistic.totalPoint.toString()
                            tvMitraPoinTerima.text = statistic.pointIn.toString()
                            tvMitraPoinTransfer.text = statistic.pointOut.toString()
                        }
                    }
                    checkAllLoaded()
                }
                is Resource.Loading -> {
                    loadingStates["statistics"] = true
                    showLoading()
                }
                is Resource.Error -> {
                    loadingStates["statistics"] = false
                    Log.e("MitraFragment", "Failed to load merchant statistics: ${result.message}")
                    checkAllLoaded()
                }
                else -> {
                    loadingStates["statistics"] = false
                    checkAllLoaded()
                }
            }
        }
    }

    private fun setupDotIndicators(count: Int) {
        binding.layoutDots.removeAllViews()
        dots = Array(count) { _ ->
            ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 0, 8, 0)
                }
                setImageResource(R.drawable.icons_dot_inactive)
                binding.layoutDots.addView(this)
            }
        }
        if (dots.isNotEmpty()) {
            dots[0].setImageResource(R.drawable.icons_dot_active)
        }
    }

    private fun updateDotIndicator(position: Int) {
        dots.forEachIndexed { index, dot ->
            dot.setImageResource(
                if (index == position) R.drawable.icons_dot_active
                else R.drawable.icons_dot_inactive
            )
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            loadingOverlay.visibility = View.VISIBLE
            layoutMitra.visibility = View.GONE

            if (swipeRefreshLayout.isRefreshing) {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }


    private fun hideLoading() {
        binding.apply {
            progressBar.visibility = View.GONE
            loadingOverlay.visibility = View.GONE
            layoutMitra.visibility = View.VISIBLE

            if (swipeRefreshLayout.isRefreshing) {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}