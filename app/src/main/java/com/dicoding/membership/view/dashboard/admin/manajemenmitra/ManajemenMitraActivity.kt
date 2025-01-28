package com.dicoding.membership.view.dashboard.admin.manajemenmitra

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.merchants.model.MerchantResultDomain
import com.dicoding.membership.R
import com.dicoding.membership.core.utils.DateUtils
import com.dicoding.membership.core.utils.showToast
import com.dicoding.membership.databinding.ActivityManajemenMitraBinding
import com.dicoding.membership.view.dashboard.MainActivity
import com.dicoding.membership.view.dashboard.admin.addmitra.AddMitraActivity
import com.dicoding.membership.view.dashboard.admin.detailmitra.DetailMitraActivity
import com.dicoding.membership.view.dialog.GlobalTwoButtonDialog
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import com.dicoding.membership.view.status.StatusTemplate
import com.dicoding.membership.view.status.StatusTemplateActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManajemenMitraActivity : AppCompatActivity() {

    private var _binding: ActivityManajemenMitraBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ManajemenMitraViewModel by viewModels()
    private lateinit var merchantAdapter: MerchantPagingAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityManajemenMitraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSwipeRefresh()

        validateToken()

        setupRecyclerView()

        observeMerchants()
    }


    private fun validateToken() {
        viewModel.getRefreshToken().observe(this) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(supportFragmentManager, "Token Expired Dialog")
            }
        }
    }

    private fun setupRecyclerView() {
        merchantAdapter = MerchantPagingAdapter().apply {
            setOnMenuClickCallback(object : MerchantPagingAdapter.OnMenuClickCallback {
                override fun onMenuClicked(view: View, data: MerchantResultDomain) {
                    val popup = PopupMenu(this@ManajemenMitraActivity, view)
                    popup.menuInflater.inflate(R.menu.menu_merchant, popup.menu)

                    popup.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.menu_update_merchant -> {
                                val intent = Intent(this@ManajemenMitraActivity, AddMitraActivity::class.java).apply {
                                    putExtra(AddMitraActivity.EXTRA_IS_EDIT, true)
                                    putExtra(AddMitraActivity.EXTRA_MERCHANT_DATA, data)
                                }
                                startActivity(intent)
                                true
                            }
                            R.id.menu_delete_merchant -> {
                                showConfirmationDialog(
                                    "Tolak Promo",
                                    "Apakah Anda yakin ingin menolak promo ini?",
                                    onConfirm = { handleDeleteMerchant(data.id) }
                                )
                                true
                            }
                            R.id.menu_detail_merchant -> {
                                val intent = Intent(this@ManajemenMitraActivity, DetailMitraActivity::class.java).apply {
                                    putExtra(DetailMitraActivity.EXTRA_MERCHANT_DATA, data)
                                }
                                startActivity(intent)
                                true
                            }
                            else -> false
                        }
                    }
                    popup.show()
                }
            })

            setOnItemClickCallback(object : MerchantPagingAdapter.OnItemClickCallback {
                override fun onItemClicked(data: MerchantResultDomain) {
                    Log.d("ManajemenMitra", "Saving merchant ID: ${data.id}")
                    viewModel.saveMerchantId(data.id).observe(this@ManajemenMitraActivity) { success ->
                        if (success) {
                            Log.d("ManajemenMitra", "Successfully saved merchant ID: ${data.id}")
                        } else {
                            Log.e("ManajemenMitra", "Failed to save merchant ID: ${data.id}")
                        }
                    }
                }
            })
        }

        binding.adminManajemenMitraRecyclerview.apply {
            layoutManager = LinearLayoutManager(this@ManajemenMitraActivity)
            adapter = merchantAdapter
        }

        binding.btnAddMitra.setOnClickListener {
            val intent = Intent(this, AddMitraActivity::class.java)
            startActivity(intent)
        }
        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    private fun showConfirmationDialog(
        title: String,
        message: String,
        onConfirm: () -> Unit
    ) {
        GlobalTwoButtonDialog().apply {
            setDialogTitle(title)
            setDialogMessage(message)
            setOnYesClickListener {
                onConfirm()
            }
        }.show(supportFragmentManager, "ConfirmationDialog")
    }

    private fun handleDeleteMerchant(id: String) {
        showLoading()
        viewModel.deleteMerchant(id).observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    hideLoading()
                    showToast("Merchant berhasil dihapus")
                    navigateToStatus(
                        title = "Hapus Berhasil",
                        description = "Mitra telah berhasil dihapus dari bisinis",
                        showCoupon = false
                    )
                }
                is Resource.Error -> {
                    hideLoading()
                    showToast(result.message ?: "Terjadi kesalahan")
                }
                is Resource.Loading -> {
                    showLoading()
                }
                else -> {
                    hideLoading()
                    showToast("Terjadi kesalahan")
                }
            }
        }
    }

    private fun navigateToStatus(
        title: String,
        description: String,
        tokenCode: String = "",
        activationDate: String = "",
        showCoupon : Boolean
    ) {

        val formattedDate = DateUtils.convertToIndonesianDateTime(activationDate)

        val statusTemplate = StatusTemplate(
            title = title,
            description = description,
            showCoupon = showCoupon,
            buttonText = "Selesai",
            promoCode = tokenCode,
            expiryTime = formattedDate
        )

        val intent = Intent(this, StatusTemplateActivity::class.java).apply {
            putExtra(StatusTemplateActivity.EXTRA_STATUS_TEMPLATE, statusTemplate)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        finish()
    }

    private fun observeMerchants() {
        // Observe Paging Data
        lifecycleScope.launch {
            viewModel.getMerchants().collectLatest { pagingData ->
                Log.d("ManajemenMitraActivity", "Received paging data")
                merchantAdapter.submitData(pagingData)
            }
        }

        // Observe Load States in separate coroutine
        lifecycleScope.launch {
            merchantAdapter.loadStateFlow
                .distinctUntilChanged { old, new ->
                    old.refresh == new.refresh
                }
                .collectLatest { loadStates ->
                    when (loadStates.refresh) {
                        is LoadState.Loading -> {
                            showLoading()
                            binding.clBody.visibility = View.GONE  // Sembunyikan ScrollView
                            Log.d("ManajemenMitraActivity", "Loading merchants")
                        }
                        is LoadState.Error -> {
                            hideLoading()
                            binding.clBody.visibility = View.VISIBLE  // Tampilkan ScrollView
                            binding.swipeRefresh.isRefreshing = false
                            val error = (loadStates.refresh as LoadState.Error).error
                            showError(error.localizedMessage)
                            Log.e("ManajemenMitraActivity", "Error loading merchants: ${error.message}")
                        }
                        is LoadState.NotLoading -> {
                            hideLoading()
                            binding.clBody.visibility = View.VISIBLE  // Tampilkan ScrollView
                            binding.swipeRefresh.isRefreshing = false
                            Log.d("ManajemenMitraActivity", "Merchants loaded successfully")
                        }
                    }
                }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            // Langsung set isRefreshing ke false agar loading indicator tidak muncul
            binding.swipeRefresh.isRefreshing = false
            // Refresh data
            merchantAdapter.refresh()
        }
    }

//    private fun navigateToDetail(data: MerchantResultDomain) {
//        val intent = Intent(this, HomeFragment::class.java).apply {
//            putExtra(EXTRA_MERCHANT, data)
//        }
//        startActivity(intent)
//    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showError(message: String?) {
        Toast.makeText(
            this,
            message ?: "Terjadi kesalahan",
            Toast.LENGTH_SHORT
        ).show()
    }


    companion object {
        const val EXTRA_MERCHANT = "extra_merchant"
    }
}