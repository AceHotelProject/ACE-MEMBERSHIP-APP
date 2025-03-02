package com.dicoding.membership.view.dashboard.promo.detail.detailpromo.detailmitrapromo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.merchants.model.GetMerchantByIdDomain
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityDetailMitraPromoBinding
import com.dicoding.membership.view.dashboard.promo.PromoAdapter
import com.dicoding.membership.view.dashboard.promo.PromoFragment
import com.dicoding.membership.view.dashboard.promo.detail.detailpromo.PromoDetailActivity
import com.dicoding.membership.view.dashboard.promo.detail.detailpromo.PromoImageAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailMitraPromoActivity : AppCompatActivity() {

    private var _binding: ActivityDetailMitraPromoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailMitraPromoViewModel by viewModels()

    private lateinit var dots: Array<ImageView>
    private lateinit var imageAdapter: PromoImageAdapter

    private lateinit var promoAdapter: PromoAdapter

    companion object {
        const val EXTRA_MERCHANT_ID = "extra_merchant_id"
        const val EXTRA_MERCHANT_NAME = "extra_merchant_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailMitraPromoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val merchantId = intent.getStringExtra(EXTRA_MERCHANT_ID)
        if (merchantId != null) {
            loadMerchantData(merchantId)
        } else {
//            finish()
        }

        setupView()
        setupPromoRecyclerView()
        observePromos()
    }

    private fun loadMerchantData(merchantId: String) {
        Log.d("MitraFragment", "Loading merchant data for ID: $merchantId")
        viewModel.getMerchantsById(merchantId).observe(this) { result ->
            when(result) {
                is Resource.Success -> {
                    hideLoading()
                    result.data?.let { merchant ->
                        binding.apply {
                            Log.d("MitraFragment", "Successfully loaded merchant: ${result.data?.name}")
                            bindDataToLayout(merchant)
                        }
                    }
                }
                is Resource.Loading -> showLoading()
                is Resource.Error -> {
                    hideLoading()
                    Log.e("MitraFragment", "Failed to load merchant: ${result.message}")
                }
                else -> { }
            }
        }
    }

    private fun bindDataToLayout(merchant: GetMerchantByIdDomain) {
        binding.apply {

            imageAdapter = PromoImageAdapter(this@DetailMitraPromoActivity)
            rvPromoSelected.adapter = imageAdapter
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(rvPromoSelected)


            rvPromoSelected.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                    if (position != -1) {
                        updateDotIndicator(position)
                    }
                }
            })

            if (merchant.picturesUrl.isNotEmpty()) {
                imageAdapter.submitList(merchant.picturesUrl)
                setupDotIndicators(merchant.picturesUrl.size)
            }

            tvDetailCategory.text = merchant.merchantType
            tvPromoName.text = merchant.name
            tvDeskripsi.text = merchant.detail

            Log.d("MerchantDetail", "Created At: ${merchant.createdAt}")
            Log.d("MerchantDetail", "Point: ${merchant.point}")
            Log.d("MerchantDetail", "Refferal Point: ${merchant.refferalPoint}")
        }
    }

    private fun setupDotIndicators(count: Int) {
        binding.layoutDots.removeAllViews()
        dots = Array(count) { _ ->
            ImageView(this).apply {
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

    private fun setupView() {
        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    private fun setupPromoRecyclerView() {
        promoAdapter = PromoAdapter()

        // Setup untuk member promo recycler view
        binding.rvPromoMitra.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = promoAdapter
        }

        // Set click callback untuk navigasi ke detail
        promoAdapter.setOnItemClickCallback(object : PromoAdapter.OnItemClickCallback {
            override fun onItemClicked(data: PromoDomain) {
                navigateToDetail(data)
            }
        })
    }

    private fun navigateToDetail(promo: PromoDomain) {
        val intent = Intent(this, PromoDetailActivity::class.java).apply {
            putExtra(PromoDetailActivity.EXTRA_PROMO, data)
            putExtra(PromoDetailActivity.EXTRA_PROMO, promo)
            putExtra(PromoDetailActivity.EXTRA_SOURCE, PromoFragment.PROMO_SOURCE_MITRA)
        }
        startActivity(intent)
    }

    private fun observePromos() {
        val merchantName = intent.getStringExtra(EXTRA_MERCHANT_NAME)
        if (!merchantName.isNullOrEmpty()) {
            Log.d("DetailMitraPromo", "Setting merchant name: $merchantName")
            viewModel.setMerchantName(merchantName)
        }

        lifecycleScope.launch {
            // Observe LoadState dari adapter
            promoAdapter.loadStateFlow.collectLatest { loadState ->
                when (loadState.refresh) {
                    is LoadState.Loading -> {
                        showLoading()
                        Log.d("DetailMitraPromo", "Promo Loading")
                    }
                    is LoadState.NotLoading -> {
                        hideLoading()
                        Log.d("DetailMitraPromo", "Promo Ready")
                    }
                    is LoadState.Error -> {
                        hideLoading()
                        Log.e("DetailMitraPromo", "Promo Error: ${(loadState.refresh as LoadState.Error).error.message}")
                    }
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.promos.collectLatest { pagingData ->
                    Log.d("DetailMitraPromo", "Received paging data")
                    promoAdapter.submitData(pagingData)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            scrollLayout.visibility = View.GONE
        }
    }

    private fun hideLoading() {
        binding.apply {
            progressBar.visibility = View.GONE
            scrollLayout.visibility = View.VISIBLE
        }
    }
}