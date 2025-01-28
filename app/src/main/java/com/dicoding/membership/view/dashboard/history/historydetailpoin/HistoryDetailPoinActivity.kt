package com.dicoding.membership.view.dashboard.history.poin.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.points.model.PointHistory
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityHistoryDetailPoinBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class HistoryDetailPoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryDetailPoinBinding
    private val viewModel: HistoryDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailPoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pointHistory = intent.getParcelableExtra<PointHistory>("point_history")
        val isReceiving = intent.getBooleanExtra("is_receiving", false)

        pointHistory?.let { history ->
            setupUI(history, isReceiving)
            setupObserver(history, isReceiving)
            viewModel.getOtherUserData(history, isReceiving)
        }

        binding.btnClose.setOnClickListener { finish() }
    }

    private fun setupObserver(history: PointHistory, isReceiving: Boolean) {
        lifecycleScope.launch {
            viewModel.userData.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // You could add loading state for email and phone
                        binding.tvDataEmail.text = "Loading..."
                        binding.tvDataPhone.text = "Loading..."
                    }
                    is Resource.Success -> {
                        resource.data?.let { user ->
                            binding.tvDataEmail.text = user.email
                            binding.tvDataPhone.text = user.phone ?: "-"
                        }
                    }
                    is Resource.Error -> {
                        // Handle error state
                        binding.tvDataEmail.text = "empty@gmail.com"
                        binding.tvDataPhone.text = "08000000000"
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setupUI(history: PointHistory, isReceiving: Boolean) {
        with(binding) {
            // Status texts
            tvTransferStatus.text = if (isReceiving) "Terima Berhasil" else "Transfer Berhasil"
            tvTransferMessage.text = if (isReceiving)
                "Poin Anda telah berhasil diterima"
            else
                "Poin Anda telah berhasil ditransfer"

            // Amount layout styling
            llTransferAmount.setBackgroundResource(
                if (isReceiving) R.drawable.custom_item_poin_success
                else R.drawable.custom_item_poin_success_grey
            )
            ivIcon.backgroundTintList = ContextCompat.getColorStateList(
                this@HistoryDetailPoinActivity,
                if (isReceiving) R.color.green else R.color.black
            )
            tvTransferAmount.apply {
                text = history.amount.toString()
                setTextColor(
                    ContextCompat.getColor(
                        this@HistoryDetailPoinActivity,
                        if (isReceiving) R.color.green else R.color.black
                    )
                )
            }

            // Data section
            tvDataStatus.text = if (isReceiving) "Terima Poin" else "Transfer Poin"

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val outputFormat = SimpleDateFormat("HH:mm, dd MMMM yyyy", Locale("id"))
            outputFormat.timeZone = TimeZone.getDefault()

            val date = inputFormat.parse(history.createdAt)
            tvDataDate.text = date?.let { outputFormat.format(it) }

            tvDataUser.text = if (isReceiving) "Pemberi:" else "Penerima:"
            tvDataUserData.text = if (isReceiving) history.from.name else history.to.name

            tvDataNotes.text = history.notes
        }
    }
}