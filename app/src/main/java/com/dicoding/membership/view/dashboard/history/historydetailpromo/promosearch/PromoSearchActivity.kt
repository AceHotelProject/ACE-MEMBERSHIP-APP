package com.dicoding.membership.view.dashboard.history.historydetailpromo.promosearch

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.core.domain.promo.model.PromoHistoryDomain
import com.dicoding.core.utils.constants.UserRole
import com.dicoding.core.utils.constants.mapToUserRole
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivitySearchPromoBinding
import com.dicoding.membership.view.dashboard.history.historydetailpromo.HistoryDetailPromoActivity
import com.dicoding.membership.view.dashboard.history.historydetailpromo.promosearchfilter.PromoFilterBottomSheet
import com.dicoding.membership.view.dashboard.history.promo.PromoHistoryAdapter
import com.dicoding.membership.view.dashboard.promo.PromoAdapter
import com.dicoding.membership.view.dashboard.promo.PromoFragment
import com.dicoding.membership.view.dashboard.promo.detail.detailpromo.PromoDetailActivity
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PromoSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchPromoBinding
    private val viewModel: PromoSearchViewModel by viewModels()

    private var isFromHistory = false
    private lateinit var promoAdapter: PromoAdapter
    private lateinit var historyAdapter: PromoHistoryAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchPromoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isFromHistory = intent.getBooleanExtra(EXTRA_FROM_HISTORY, false)

        checkUserRole()

//        setupViews()
//
//        setupAdapters()
//
//        setupRecyclerView()
//
//        setupSearchInput()
//
//        setupSwipeRefresh()
//
//        validateToken()
//
//        observeData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkUserRole() {
        viewModel.getUser().observe(this) { loginDomain ->
            val userRole = mapToUserRole(loginDomain.user.role)

//            Testing
            val mockUserRole = UserRole.MEMBER
            setupUserVisibility(mockUserRole)

            setupViews()

            setupAdapters(mockUserRole)

            setupRecyclerView()

            setupSearchInput()

            setupSwipeRefresh()

            when (mockUserRole) {
                UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST, UserRole.MEMBER, UserRole.USER -> {
                    validateToken()
                    observeData()
                }
                else -> {
                    Log.d("PromoSearchActivity", "Unauthorized role: ${mockUserRole.name}, skipping data load")
                }
            }

            Log.d("HistoryPromoFragment", "Current User Role: ${mockUserRole.name}")
        }
    }

    private fun setupUserVisibility(userRole: UserRole) {
        when (userRole) {
            UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST, UserRole.USER, UserRole.MEMBER -> {
                binding.apply {
                    swipeRefresh.visibility = View.VISIBLE
                    layoutNonMember.visibility = View.GONE
                }
            }
            UserRole.NONMEMBER -> {
                binding.apply {
                    swipeRefresh.visibility = View.GONE
                    layoutNonMember.visibility = View.VISIBLE
                }
            }
            else -> {
                binding.apply {
                    swipeRefresh.visibility = View.GONE
                    layoutNonMember.visibility = View.GONE
                }
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.apply {
            setOnRefreshListener {
                binding.edSearchBar.setText("")
                viewModel.setSearchQuery("")

                if (isFromHistory) {
                    historyAdapter.refresh()
                } else {
                    promoAdapter.refresh()
                }
            }
            setColorSchemeResources(R.color.orange_100)
        }
    }

    private fun setupViews() {
        binding.btnClose.setOnClickListener {
            onBackPressed()
        }

        viewModel.getUser().observe(this) { loginDomain ->
//            val userRole = mapToUserRole(loginDomain.user.role)

            val userRole = UserRole.MEMBER

            binding.btnFilter.setOnClickListener {
                when (userRole) {
                    UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST, UserRole.MEMBER, UserRole.USER -> {
                        showFilterBottomSheet()
                    }
                    else -> {
                        Toast.makeText(
                            this,
                            "Silakan daftar membership terlebih dahulu",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun showFilterBottomSheet() {
        val filterBottomSheet = PromoFilterBottomSheet().apply {

            arguments = Bundle().apply {
                putBoolean("isFromHistory", isFromHistory)
            }

            setOnFilterSelectedListener { filterType, selectedValue ->
                when (filterType) {
                    PromoFilterBottomSheet.FilterType.DATE -> {
                        viewModel.setDate(selectedValue)
                        refreshData()
                    }
                    PromoFilterBottomSheet.FilterType.STATUS -> {
                        viewModel.setStatus(selectedValue)
                        refreshData()
                    }
                    PromoFilterBottomSheet.FilterType.CATEGORY -> {
                        viewModel.setCategory(selectedValue)
                        refreshData()
                    }
                }
            }
        }
        filterBottomSheet.show(supportFragmentManager, PromoFilterBottomSheet.TAG)
    }

    private fun refreshData() {
        if (isFromHistory) {
            historyAdapter.refresh()
        } else {
            promoAdapter.refresh()
        }
    }

    private fun setupAdapters(userRole: UserRole) {
        if (isFromHistory) {
            historyAdapter = PromoHistoryAdapter().apply {
                onItemClickCallback = object : PromoHistoryAdapter.OnItemClickCallback {
                    override fun onItemClicked(history: PromoHistoryDomain) {
                        when (userRole) {
                            UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST, UserRole.MEMBER,UserRole.USER -> {
                                navigateToDetailActivity(history)
                            }
                            else -> {
                                Toast.makeText(
                                    this@PromoSearchActivity,
                                    "Silakan daftar membership terlebih dahulu",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
            binding.apply {
                rvPromo.visibility = View.GONE
                rvPromoHistory.visibility = View.VISIBLE
            }
        } else {
            promoAdapter = PromoAdapter().apply {
                setOnItemClickCallback(object : PromoAdapter.OnItemClickCallback {
                    override fun onItemClicked(promo: PromoDomain) {
                        when (userRole) {
                            UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST, UserRole.MEMBER, UserRole.USER -> {
                                val intent = Intent(this@PromoSearchActivity, PromoDetailActivity::class.java).apply {
                                    putExtra(PromoDetailActivity.EXTRA_PROMO, promo)
                                    putExtra(PromoDetailActivity.EXTRA_SOURCE, PromoFragment.PROMO_SOURCE_MITRA)
                                }
                                startActivity(intent)
                            }
                            else -> {
                                Toast.makeText(
                                    this@PromoSearchActivity,
                                    "Silakan daftar membership terlebih dahulu",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                })
            }
            binding.apply {
                rvPromo.visibility = View.VISIBLE
                rvPromoHistory.visibility = View.GONE
            }
        }
    }

    private fun navigateToDetailActivity(history: PromoHistoryDomain) {
        val intent = Intent(this, HistoryDetailPromoActivity::class.java).apply {
            putExtra("PROMO_HISTORY", history)
        }
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        // Setup hanya untuk RecyclerView yang sesuai
        if (isFromHistory) {
            binding.rvPromoHistory.apply {
                layoutManager = LinearLayoutManager(this@PromoSearchActivity)
                adapter = historyAdapter
            }
        } else {
            binding.rvPromo.apply {
                layoutManager = LinearLayoutManager(this@PromoSearchActivity)
                adapter = promoAdapter
            }
        }
    }

    private fun setupSearchInput() {
        viewModel.getUser().observe(this) { loginDomain ->
//            val userRole = mapToUserRole(loginDomain.user.role)

            val userRole = UserRole.MEMBER

            binding.edSearchBar.apply {
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        when (userRole) {
                            UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST, UserRole.MEMBER, UserRole.USER -> {
                                hint = ""
                            }
                            else -> {
                                clearFocus()
                                Toast.makeText(
                                    this@PromoSearchActivity,
                                    "Silakan daftar membership terlebih dahulu",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else if (text.isNullOrEmpty()) {
                        hint = "Masukan pencarian"
                    }
                }

                setOnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event?.action == KeyEvent.ACTION_DOWN &&
                        event.keyCode == KeyEvent.KEYCODE_ENTER
                    ) {
                        when (userRole) {
                            UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST, UserRole.MEMBER, UserRole.USER -> {
                                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(v.windowToken, 0)
                                v.clearFocus()
                            }
                            else -> {
                                Toast.makeText(
                                    this@PromoSearchActivity,
                                    "Silakan daftar membership terlebih dahulu",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        return@setOnEditorActionListener true
                    }
                    false
                }

                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                    override fun afterTextChanged(s: Editable?) {
                        when (userRole) {
                            UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST, UserRole.MEMBER -> {
                                viewModel.setSearchQuery(s?.toString() ?: "")
                            }
                            else -> {
                                if (!s.isNullOrEmpty()) {
                                    Toast.makeText(
                                        this@PromoSearchActivity,
                                        "Silakan daftar membership terlebih dahulu",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    setText("") // Clear text for non-member
                                    clearFocus()
                                }
                            }
                        }
                    }
                })
            }
        }
    }

    private fun validateToken() {
        viewModel.getRefreshToken().observe(this) { token ->
            if (token.isEmpty()) {
                TokenExpiredDialog().show(supportFragmentManager, "Token Expired Dialog")
            }
        }
    }

    private fun observeData() {
        viewModel.getRefreshToken().observe(this) { token ->
            if (token.isNotEmpty()) {
                lifecycleScope.launch {
                    if (isFromHistory) {
                        launch {
                            viewModel.promoHistory.collect { pagingData ->
                                historyAdapter.submitData(pagingData)
                            }
                        }

                        launch {
                            viewModel.selectedStatus.collect { status ->
                                historyAdapter.refresh()
                            }
                        }

                        launch {
                            viewModel.selectedCategory.collect { category ->
                                historyAdapter.refresh()
                            }
                        }

                        launch {
                            viewModel.selectedDate.collect { date ->
                                historyAdapter.refresh()
                            }
                        }

                        historyAdapter.addLoadStateListener { loadState ->
                            Log.d("PromoSearch", "LoadState: ${loadState.refresh}")
                            Log.d("PromoSearch", "ItemCount: ${historyAdapter.itemCount}")

                            binding.swipeRefresh.isRefreshing = false

                            when (loadState.refresh) {
                                is LoadState.Loading -> {
                                    Log.d("PromoSearch", "State: Loading")
                                    showLoading(true)
                                    showEmptyState(false)
                                }
                                is LoadState.Error -> {
                                    Log.d("PromoSearch", "State: Error")
                                    showLoading(false)
                                    showError((loadState.refresh as LoadState.Error).error.message)
                                }
                                is LoadState.NotLoading -> {
                                    Log.d("PromoSearch", "State: NotLoading")
                                    showLoading(false)
                                    if (historyAdapter.itemCount == 0) {
                                        Log.d("PromoSearch", "Showing empty state")
                                        showEmptyState(true)
                                    } else {
                                        Log.d("PromoSearch", "Hiding empty state")
                                        showEmptyState(false)
                                    }
                                }
                            }
                        }
                    } else {
                        launch {
                            viewModel.promos.collect { pagingData ->
                                promoAdapter.submitData(pagingData)
                            }
                        }

                        launch {
                            viewModel.selectedStatus.collect { status ->
                                promoAdapter.refresh()
                            }
                        }

                        launch {
                            viewModel.selectedCategory.collect { category ->
                                promoAdapter.refresh()
                            }
                        }

                        launch {
                            viewModel.selectedDate.collect { date ->
                                promoAdapter.refresh()
                            }
                        }

                        promoAdapter.addLoadStateListener { loadState ->
                            Log.d("PromoSearch", "LoadState: ${loadState.refresh}")
                            Log.d("PromoSearch", "ItemCount: ${promoAdapter.itemCount}")

                            binding.swipeRefresh.isRefreshing = false

                            when (loadState.refresh) {
                                is LoadState.Loading -> {
                                    Log.d("PromoSearch", "State: Loading")
                                    showLoading(true)
                                    showEmptyState(false)
                                }
                                is LoadState.Error -> {
                                    Log.d("PromoSearch", "State: Error")
                                    showLoading(false)
                                    showError((loadState.refresh as LoadState.Error).error.message)
                                }
                                is LoadState.NotLoading -> {
                                    Log.d("PromoSearch", "State: NotLoading")
                                    showLoading(false)
                                    if (promoAdapter.itemCount == 0) {
                                        Log.d("PromoSearch", "Showing empty state")
                                        showEmptyState(true)
                                    } else {
                                        Log.d("PromoSearch", "Hiding empty state")
                                        showEmptyState(false)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                showError("Token kosong, silakan login ulang.")
            }
        }
    }

    private fun showEmptyState(show: Boolean) {
        binding.apply {
            tvTidakAdaRiwayat.visibility = if (show) View.VISIBLE else View.GONE

            if (isFromHistory) {
                rvPromoHistory.visibility = if (show) View.GONE else View.VISIBLE

                rvPromo.visibility = View.GONE
            } else {
                rvPromo.visibility = if (show) View.GONE else View.VISIBLE

                rvPromoHistory.visibility = View.GONE
            }

            if (show) {
                tvTidakAdaRiwayat.text = if (isFromHistory) {
                    "Tidak Ada Riwayat"
                } else {
                    "Tidak Ada Promo"
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.apply {
            if (!swipeRefresh.isRefreshing) {
                progressBar.visibility = if (show) View.VISIBLE else View.GONE
            } else {
                progressBar.visibility = View.GONE
            }

            if (isFromHistory) {
                if (!show && !swipeRefresh.isRefreshing) {
                    rvPromoHistory.visibility = View.VISIBLE
                } else if (show && !swipeRefresh.isRefreshing) {
                    rvPromoHistory.visibility = View.GONE
                }
            } else {
                if (!show && !swipeRefresh.isRefreshing) {
                    rvPromo.visibility = View.VISIBLE
                } else if (show && !swipeRefresh.isRefreshing) {
                    rvPromo.visibility = View.GONE
                }
            }
        }
    }

    private fun showError(message: String?) {
        Toast.makeText(this, message ?: "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_FROM_HISTORY = "extra_from_history"
    }
}