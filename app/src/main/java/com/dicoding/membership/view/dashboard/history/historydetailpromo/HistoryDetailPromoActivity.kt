package com.dicoding.membership.view.dashboard.history.historydetailpromo

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.domain.promo.model.PromoHistoryDomain
import com.dicoding.membership.databinding.ActivityHistoryDetailPromoBinding
import com.dicoding.membership.view.dashboard.promo.PromoAdapter
import com.dicoding.membership.view.dashboard.promo.detail.detailpromo.PromoDetailActivity
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class HistoryDetailPromoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryDetailPromoBinding
    private val viewModel: HistoryDetailPromoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailPromoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        validateToken()

        getDataHistory()
    }

    private fun validateToken() {
        viewModel.getRefreshToken().observe(this) { token ->
            if (token.isEmpty()) {
                TokenExpiredDialog().show(supportFragmentManager, "Token Expired Dialog")
            }
        }
    }

    private fun getDataHistory(){
        val history = intent.getParcelableExtra<PromoHistoryDomain>("PROMO_HISTORY")
        if (history != null) {
            bindPromoHistory(history)
        } else {
            finish()
        }
    }

    private fun bindPromoHistory(history: PromoHistoryDomain) {
        binding.apply {
            // Setup RecyclerView with existing PromoAdapter
            val promoAdapter = PromoAdapter()
            rvPromoMitra.apply {
                layoutManager = LinearLayoutManager(this@HistoryDetailPromoActivity)
                adapter = promoAdapter
            }

            // Submit history data
            promoAdapter.submitHistoryList(listOf(history))

            // Setup click listener
            promoAdapter.setOnItemClickCallback(object : PromoAdapter.OnItemClickCallback {
                override fun onItemClickedHistory(data: PromoHistoryDomain) {
                    // Handle history item click if needed
                    navigateToDetail(data, PROMO_SOURCE_HISTORY)
                }
            })

            tvStatus.text = history.status
            tvActivationDate.text = formatDate(history.activationDate)
            tvVerivikator.text = history.userName
            tvMitra.text = history.merchantName
        }

        binding.btnClose.setOnClickListener {
            handleBackNavigation()
        }
    }

    private fun navigateToDetail(data: PromoHistoryDomain, source: String) {
        val intent = Intent(this, PromoDetailActivity::class.java).apply {
            putExtra(PromoDetailActivity.EXTRA_PROMO, data)
            putExtra(PromoDetailActivity.EXTRA_SOURCE, PROMO_SOURCE_HISTORY)
        }
        startActivity(intent)
    }

    private fun handleBackNavigation() {
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun formatDate(isoDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val outputFormat = SimpleDateFormat("HH:mm, dd MMMM yyyy", Locale("id"))
            outputFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

            val date = inputFormat.parse(isoDate)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            isoDate
        }
    }

    companion object {
        const val PROMO_SOURCE_HISTORY = "history"
    }
}