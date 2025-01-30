package com.dicoding.membership.view.dashboard.history.historydetailpromo.promosearch

import android.content.Context
import android.content.Intent
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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivitySearchPromoBinding
import com.dicoding.membership.view.dashboard.history.historydetailpromo.promosearchfilter.PromoFilterBottomSheet
import com.dicoding.membership.view.dashboard.history.promo.PromoHistoryAdapter
import com.dicoding.membership.view.dashboard.promo.PromoAdapter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchPromoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isFromHistory = intent.getBooleanExtra(EXTRA_FROM_HISTORY, false)

        setupViews()

        setupAdapters()

        setupRecyclerView()

        setupSearchInput()

        setupSwipeRefresh()

        validateToken()

        observeData()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.apply {
            setOnRefreshListener {
                // Reset search query
                binding.edSearchBar.setText("")
                viewModel.setSearchQuery("")

                // Refresh adapter
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
        // Setup close button
        binding.btnClose.setOnClickListener {
            onBackPressed()
        }

        // Setup filter button
        binding.btnFilter.setOnClickListener {
            // Implement filter logic here
            showFilterBottomSheet()
        }
    }

    private fun showFilterBottomSheet() {
        val filterBottomSheet = PromoFilterBottomSheet().apply {

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

    private fun setupAdapters() {
        if (isFromHistory) {
            historyAdapter = PromoHistoryAdapter()
            binding.apply {
                rvPromo.visibility = View.GONE
                rvPromoHistory.visibility = View.VISIBLE
            }
        } else {
            promoAdapter = PromoAdapter().apply {
                setOnItemClickCallback(object : PromoAdapter.OnItemClickCallback {
                    override fun onItemClicked(data: PromoDomain) {
                        val intent = Intent(this@PromoSearchActivity, PromoDetailActivity::class.java).apply {
                            putExtra(PromoDetailActivity.EXTRA_PROMO, data)
                        }
                        startActivity(intent)
                    }
                })
            }
            binding.apply {
                rvPromo.visibility = View.VISIBLE
                rvPromoHistory.visibility = View.GONE
            }
        }
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
        binding.edSearchBar.apply {
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    hint = ""
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
                    // Sembunyikan keyboard
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                    v.clearFocus()
                    return@setOnEditorActionListener true
                }
                false
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    // Set query dan refresh data
                    viewModel.setSearchQuery(s?.toString() ?: "")
                }
            })
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

                        // Tambahkan observer untuk filter status
                        launch {
                            viewModel.selectedStatus.collect { status ->
                                historyAdapter.refresh()
                            }
                        }

                        // Tambahkan observer untuk filter kategori
                        launch {
                            viewModel.selectedCategory.collect { category ->
                                historyAdapter.refresh()
                            }
                        }

                        // Tambahkan observer untuk filter tanggal
                        launch {
                            viewModel.selectedDate.collect { date ->
                                historyAdapter.refresh()
                            }
                        }

                        historyAdapter.addLoadStateListener { loadState ->
                            Log.d("PromoSearch", "LoadState: ${loadState.refresh}")
                            Log.d("PromoSearch", "ItemCount: ${historyAdapter.itemCount}")

                            // Immediately set swipeRefresh to false
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

                        // Tambahkan observer untuk filter status
                        launch {
                            viewModel.selectedStatus.collect { status ->
                                promoAdapter.refresh()
                            }
                        }

                        // Tambahkan observer untuk filter kategori
                        launch {
                            viewModel.selectedCategory.collect { category ->
                                promoAdapter.refresh()
                            }
                        }

                        // Tambahkan observer untuk filter tanggal
                        launch {
                            viewModel.selectedDate.collect { date ->
                                promoAdapter.refresh()
                            }
                        }

                        promoAdapter.addLoadStateListener { loadState ->
                            Log.d("PromoSearch", "LoadState: ${loadState.refresh}")
                            Log.d("PromoSearch", "ItemCount: ${promoAdapter.itemCount}")

                            // Immediately set swipeRefresh to false
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

    private fun handleLoadState(loadState: CombinedLoadStates) {
        // Debug logs
        println("LoadState: ${loadState.refresh}")
        println("ItemCount: ${if (isFromHistory) historyAdapter.itemCount else promoAdapter.itemCount}")
        println("State: ${loadState.refresh::class.simpleName}")

        when (loadState.refresh) {
            is LoadState.Loading -> {
                showLoading(true)
                showEmptyState(false)
            }
            is LoadState.Error -> {
                showLoading(false)
                binding.swipeRefresh.isRefreshing = false
                showError((loadState.refresh as LoadState.Error).error.message)

                val adapter = if (isFromHistory) historyAdapter else promoAdapter
                showEmptyState(adapter.itemCount == 0)
            }
            is LoadState.NotLoading -> {
                showLoading(false)
                binding.swipeRefresh.isRefreshing = false

                // Cek empty state berdasarkan endOfPaginationReached dan itemCount
                val adapter = if (isFromHistory) historyAdapter else promoAdapter
                val isEmpty = adapter.itemCount == 0 &&
                        (loadState.refresh as LoadState.NotLoading).endOfPaginationReached

                println("Showing empty state: $isEmpty")
                showEmptyState(isEmpty)
            }
        }
    }

    private fun showEmptyState(show: Boolean) {
        binding.apply {
            tvTidakAdaRiwayat.visibility = if (show) View.VISIBLE else View.GONE

            if (isFromHistory) {
                rvPromoHistory.visibility = if (show) View.GONE else View.VISIBLE
                // Pastikan rv_promo selalu GONE
                rvPromo.visibility = View.GONE
            } else {
                rvPromo.visibility = if (show) View.GONE else View.VISIBLE
                // Pastikan rv_promo_history selalu GONE
                rvPromoHistory.visibility = View.GONE
            }

            // Set text sesuai dengan jenis data
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
            // Tampilkan progress bar hanya jika tidak dalam swipe refresh
            if (!swipeRefresh.isRefreshing) {
                progressBar.visibility = if (show) View.VISIBLE else View.GONE
            } else {
                progressBar.visibility = View.GONE
            }

            // Atur visibility RecyclerView
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