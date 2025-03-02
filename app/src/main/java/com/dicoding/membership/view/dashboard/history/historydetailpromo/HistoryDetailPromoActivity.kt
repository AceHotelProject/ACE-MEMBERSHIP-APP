package com.dicoding.membership.view.dashboard.history.historydetailpromo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.domain.promo.model.PromoHistoryDomain
import com.dicoding.core.utils.constants.UserRole
import com.dicoding.core.utils.constants.mapToUserRole
import com.dicoding.membership.R
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
    private var currentUserRole: UserRole = UserRole.MEMBER

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailPromoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        validateToken()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun validateToken() {
        viewModel.getRefreshToken().observe(this) { token ->
            if (token.isEmpty()) {
                TokenExpiredDialog().show(supportFragmentManager, "Token Expired Dialog")
            } else {
                setupInitialNavigation()
            }
        }
    }

    @SuppressLint("LongLogTag")
    private fun setupInitialNavigation() {
       viewModel.getUser().observe(this) { loginDomain ->
            val userRole = mapToUserRole(loginDomain.user.role)

            //            Testing
//            val mockUserRole = UserRole.MEMBER

            val finalUserRole = when (userRole) {
                UserRole.USER -> {
                    if (loginDomain.user.isMember) {
                        UserRole.MEMBER
                    } else {
                        UserRole.NONMEMBER
                    }
                }
                UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST -> userRole
                else -> userRole
            }

           currentUserRole = finalUserRole
            Log.d("HistoryDetailPromoActivity", "User Role: ${finalUserRole.display}")

           getDataHistory()
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
                    navigateToDetail(data)
                }
            })

            tvStatus.text = history.status
            tvMitra.text = history.merchantName

            history.draftDate.let { tvDraftedDate.text = formatDate(it) }
            history.validatedDate.let { tvValidatedDate.text = formatDate(it) }
            history.activationDate.let { tvActivationDate.text = formatDate(it) }
            history.redeemedDate?.let { tvRedeemedDate.text = formatDate(it) }

            setupVisibilityBasedOnStatus(history)
        }

        binding.btnClose.setOnClickListener {
            handleBackNavigation()
        }
    }

    private fun setupVisibilityBasedOnStatus(history: PromoHistoryDomain) {
        val statusLayout = binding.root.findViewById<View>(R.id.tvs_status).parent as View
        val redeemedDateLayout = binding.root.findViewById<View>(R.id.tvs_redeemed_date).parent as View
        val activationDateLayout = binding.root.findViewById<View>(R.id.tvs_activation_date).parent as View
        val draftedDateLayout = binding.root.findViewById<View>(R.id.tvs_drafted_date).parent as View
        val validatedDateLayout = binding.root.findViewById<View>(R.id.tvs_validated_date).parent as View
        val verifikatorLayout = binding.root.findViewById<View>(R.id.tvs_verivikator).parent as View
        val mitraLayout = binding.root.findViewById<View>(R.id.tvs_mitra).parent as View

        redeemedDateLayout.visibility = View.GONE
        activationDateLayout.visibility = View.GONE
        draftedDateLayout.visibility = View.GONE
        validatedDateLayout.visibility = View.GONE

        statusLayout.visibility = View.VISIBLE
        mitraLayout.visibility = View.VISIBLE
        verifikatorLayout.visibility = View.VISIBLE

        when (history.status.lowercase()) {
            "draft" -> {
                draftedDateLayout.visibility = View.VISIBLE
                binding.tvVerivikator.text = history.draftedByName
                binding.tvsVerivikator.text = "Diajukan oleh:"
            }
            "valid" -> {
                validatedDateLayout.visibility = View.VISIBLE
                binding.tvVerivikator.text = history.validatedByName
                binding.tvsVerivikator.text = "Divalidasi oleh:"
            }
            "active" -> {
                activationDateLayout.visibility = View.VISIBLE
                binding.tvVerivikator.text = history.activatedByName
                binding.tvsVerivikator.text = "Diaktifkan oleh:"
            }
            "redeemed" -> {
                redeemedDateLayout.visibility = View.VISIBLE
                activationDateLayout.visibility = View.VISIBLE
                binding.tvVerivikator.text = history.redeemedByName ?: "-"
                binding.tvsVerivikator.text = "Ditukarkan oleh:"
            }
        }
    }


    private fun navigateToDetail(data: PromoHistoryDomain) {
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