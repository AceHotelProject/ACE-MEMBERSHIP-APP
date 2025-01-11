package com.dicoding.membership.view.dashboard.promo.detail.detailpromo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.core.utils.constants.UserRole
import com.dicoding.core.utils.constants.mapToUserRole
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityPromoDetailBinding
import com.dicoding.membership.view.dashboard.MainActivity
import com.dicoding.membership.view.dashboard.promo.PromoFragment
import com.dicoding.membership.view.dialog.GlobalTwoButtonDialog
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import com.dicoding.membership.view.status.StatusTemplate
import com.dicoding.membership.view.status.StatusTemplateActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PromoDetailActivity : AppCompatActivity() {

    private var _binding: ActivityPromoDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PromoDetailViewModel by viewModels()

    companion object {
        const val EXTRA_PROMO = "extra_promo"
        const val EXTRA_SOURCE = "extra_source"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPromoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val promo = intent.getParcelableExtra<PromoDomain>(EXTRA_PROMO)
        val source = intent.getStringExtra(EXTRA_SOURCE)

        setupView(promo, source)

        validateToken()

        checkUserRole()
    }

    private fun validateToken() {
        viewModel.getRefreshToken().observe(this) { token ->
            if (token.isEmpty()) {
                // Since we're in an Activity, we need to use supportFragmentManager
                TokenExpiredDialog().show(supportFragmentManager, "Token Expired Dialog")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkUserRole() {
        viewModel.getUser().observe(this) { loginDomain ->
            val userRole = mapToUserRole(loginDomain.user.role)

//            Testing
            val mockUserRole = UserRole.ADMIN
            setupUserVisibility(mockUserRole)

//            Use This For Real
//            setupFabVisibility(userRole)

            Log.d("PromoDetailActivity", "User Role: ${userRole.display}")
        }
    }

    private fun setupUserVisibility(userRole: UserRole) {
        when (userRole) {
            UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST -> {
                binding.layoutItemDetailPromo.visibility = View.GONE
            }
            UserRole.MEMBER -> {
                binding.layoutItemDetailPromo.visibility = View.VISIBLE
            }
            else -> {
                binding.layoutItemDetailPromo.visibility = View.GONE
            }
        }
    }

    private fun setupView(promo: PromoDomain?, source: String?) {

        bindDataToLayout(promo, source)

        binding.btnClose.setOnClickListener {
            handleBackNavigation()
        }

        when (source) {
            PromoFragment.PROMO_SOURCE_AJUAN -> {
                setupAjuanPromoView(promo)
            }
            PromoFragment.PROMO_SOURCE_MITRA -> {
                setupPromoMitraView(promo)
            }
        }
    }

    private fun bindDataToLayout(promo: PromoDomain?, source: String?) {
        binding.apply {
            // Menampilkan gambar promo jika ada
            if (promo?.pictures?.isNotEmpty() == true) {
                Glide.with(this@PromoDetailActivity)
                    .load(promo.pictures[0]) // Ambil gambar pertama
                    .into(ivImage)
            } else {
                ivImage.setImageResource(R.drawable.item_dashboard_promo_default) // Gambar default jika tidak ada
            }

            // Menampilkan kategori promo
            tvDetailCategory.text = promo?.category ?: "Kategori Tidak Tersedia"

            // Menampilkan detail promo
            tvPromoName.text = promo?.name ?: "Nama Promo Tidak Tersedia"

            // Menampilkan siapa yang membuat promo
            tvOleh.text = "oleh ${promo?.category ?: "Admin"}"

            // Menampilkan detail promo
            tvDeskripsi.text = promo?.detail ?: "Deskripsi Tidak Tersedia"

            // Menampilkan syarat dan ketentuan, bisa menggunakan RecyclerView atau TextView biasa
            Log.d("PromoDetail", "TnC List: ${promo?.tnc}")
            Log.d("PromoDetail", "TnC is null? ${promo?.tnc == null}")
            Log.d("PromoDetail", "TnC is empty? ${promo?.tnc?.isEmpty()}")

            if (!promo?.tnc.isNullOrEmpty()) {
                Log.d("PromoDetail", "Setting up TnC adapter with ${promo?.tnc?.size} items")
                // Pastikan RecyclerView memiliki LayoutManager
                rvSyaratDanKetentuan.layoutManager = LinearLayoutManager(this@PromoDetailActivity)
                rvSyaratDanKetentuan.adapter = SyaratDanKetentuanAdapter(promo?.tnc ?: emptyList()).also {
                    Log.d("PromoDetail", "Adapter created and set")
                }
            } else {
                Log.d("PromoDetail", "TnC list is null or empty")
            }
        }
    }

    private fun setupAjuanPromoView(promo: PromoDomain?) {
        with(binding) {
            // Visibility settings for Ajuan Promo
            btnSetuju.visibility = View.VISIBLE
            btnTolak.visibility = View.VISIBLE
            btnPakai.visibility = View.GONE

            // Setup UI elements
            tvPromoTitle.text = "Ajuan Promo"

            // Click listeners
            btnSetuju.setOnClickListener {
                promo?.id?.let { promoId ->
                    showConfirmationDialog(
                        "Konfirmasi Promo",
                        "Apakah Anda yakin ingin menyetujui promo ini?",
                        onConfirm = { handleActivatePromo(promoId) }
                    )
                }
            }

            btnTolak.setOnClickListener {
                promo?.id?.let { promoId ->
                    showConfirmationDialog(
                        "Hapus Promo",
                        "Apakah Anda yakin ingin menolak promo ini?",
                        onConfirm = { handleDeletePromo(promoId) }
                    )
                }
            }
        }
    }

    private fun handleBackNavigation() {
        val intent = Intent(this@PromoDetailActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
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

    private fun handleActivatePromo(promoId: String) {
        viewModel.getRefreshToken().observe(this) { token ->
            if (token.isNotEmpty()) {
                showLoading()
                viewModel.activatePromo(promoId).observe(this) { result ->
                    when (result) {
                        is Resource.Success -> {
                            hideLoading()
                            showToast("Promo berhasil disetujui")
                            navigateToStatus(
                                title = "Promo Dibuat",
                                description = "Promo telah berhasil disetujui dan akan segera aktif sesuai dengan tanggal yang telah ditentukan"
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
        }
    }

    private fun handleDeletePromo(promoId: String) {
        showLoading()
        viewModel.deletePromo(promoId).observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    hideLoading()
                    showToast("Promo berhasil ditolak")
                    navigateToStatus(
                        title = "Hapus Berhasil",
                        description = "Promo telah berhasil ditolak dan tidak akan ditampilkan"
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
        description: String
    ) {
        val statusTemplate = StatusTemplate(
            title = title,
            description = description,
            showCoupon = false,
            buttonText = "Selesai"
        )

        val intent = Intent(this, StatusTemplateActivity::class.java).apply {
            putExtra(StatusTemplateActivity.EXTRA_STATUS_TEMPLATE, statusTemplate)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        finish()
    }

    private fun setupPromoMitraView(promo: PromoDomain?) {
        with(binding) {
            // Visibility settings for Promo Mitra
            btnSetuju.visibility = View.GONE
            btnTolak.visibility = View.GONE
            btnPakai.visibility = View.VISIBLE

            // Setup UI elements
            tvPromoTitle.text = "Detail Promo"

            btnPakai.setOnClickListener {
                promo?.id?.let { promoId ->
                    showConfirmationDialog(
                        "Gunakan Promo",
                        "Apakah Anda yakin ingin menggunakan promo ini",
                        onConfirm = { handleDeletePromo(promoId) }
                    )
                }
            }
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            loadingOverlay.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        binding.apply {
            progressBar.visibility = View.GONE
            loadingOverlay.visibility = View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
