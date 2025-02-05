package com.dicoding.membership.view.dashboard.promo.detail.detailpromo

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.core.data.source.Resource
import com.dicoding.core.domain.promo.model.PromoDomain
import com.dicoding.core.domain.promo.model.PromoHistoryDomain
import com.dicoding.core.utils.ImageUtils.reduceFileImage
import com.dicoding.core.utils.ImageUtils.uriToFile
import com.dicoding.core.utils.constants.UserRole
import com.dicoding.core.utils.constants.mapToUserRole
import com.dicoding.membership.R
import com.dicoding.membership.core.utils.DateUtils
import com.dicoding.membership.databinding.ActivityPromoDetailBinding
import com.dicoding.membership.view.dashboard.floatingpromo.StaffAddPromoActivity
import com.dicoding.membership.view.dashboard.history.historydetailpromo.HistoryDetailPromoActivity.Companion.PROMO_SOURCE_HISTORY
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

    private lateinit var dots: Array<ImageView>
    private lateinit var imageAdapter: PromoImageAdapter

    companion object {
        const val EXTRA_PROMO = "extra_promo"
        const val EXTRA_SOURCE = "extra_source"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPromoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when (val source = intent.getStringExtra(EXTRA_SOURCE)) {
            PROMO_SOURCE_HISTORY -> {
                val promoHistory = intent.getParcelableExtra<PromoHistoryDomain>(EXTRA_PROMO)
                if (promoHistory != null) {
                    setupHistoryView(promoHistory)
                } else {
                    finish()
                }
            }
            else -> {
                val promoDomain = intent.getParcelableExtra<PromoDomain>(EXTRA_PROMO)
                if (promoDomain != null) {
                    setupView(promoDomain, source)
                    validateToken()
                    checkUserRole(promoDomain)
                } else {
                    finish()
                }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkUserRole(promo: PromoDomain) {
        viewModel.getUser().observe(this) { loginDomain ->
            val userRole = mapToUserRole(loginDomain.user.role)

//            Testing
            val mockUserRole = UserRole.ADMIN
            setupUserVisibility(mockUserRole, promo)

//            Use This For Real
//            setupFabVisibility(userRole)

            Log.d("PromoDetailActivity", "User Role: ${userRole.display}")
        }
    }

    private fun setupUserVisibility(userRole: UserRole, promo: PromoDomain?) {
        when (userRole) {
            UserRole.ADMIN, UserRole.MITRA -> {
                binding.layoutItemDetailPromo.visibility = View.GONE
                binding.btnPakai.visibility = View.GONE
            }
            UserRole.RECEPTIONIST -> {
                binding.layoutItemDetailPromo.visibility = View.GONE
                binding.btnPakai.visibility = View.GONE
                binding.btnMenu.visibility = View.GONE
                binding.btnSetuju.visibility = View.GONE
                binding.btnTolak.visibility = View.GONE
            }
            UserRole.USER -> {
                if (!promo?.token.isNullOrEmpty()) {
                    // Token tersedia, tampilkan layout dan isi token
                    binding.layoutItemDetailPromo.visibility = View.VISIBLE
                    binding.tvPromoCode.text = promo?.token
                    Log.d("PromoDetailActivity", "Token tersedia, layout tampil")
                } else {
                    // Token kosong, sembunyikan layout
                    binding.layoutItemDetailPromo.visibility = View.GONE
                    Log.d("PromoDetailActivity", "Token kosong, layout disembunyikan")
                }
                binding.btnPakai.visibility = View.VISIBLE
                binding.btnMenu.visibility = View.GONE
                binding.btnSetuju.visibility = View.GONE
                binding.btnTolak.visibility = View.GONE
            }
            UserRole.NONMEMBER -> {
                binding.layoutItemDetailPromo.visibility = View.GONE
                binding.btnPakai.visibility = View.GONE
                binding.btnMenu.visibility = View.GONE
                binding.btnSetuju.visibility = View.GONE
                binding.btnTolak.visibility = View.GONE
            }
            else -> {
                binding.layoutItemDetailPromo.visibility = View.GONE
                binding.btnPakai.visibility = View.GONE
                binding.btnSetuju.visibility = View.GONE
                binding.btnTolak.visibility = View.GONE
                binding.btnMenu.visibility = View.GONE
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

    private fun setupHistoryView(history: PromoHistoryDomain) {
        binding.apply {
            // Setup RecyclerView with PagerSnapHelper for horizontal snap scrolling
            imageAdapter = PromoImageAdapter(this@PromoDetailActivity)
            rvPromoSelected.adapter = imageAdapter

            // Add PagerSnapHelper for snapping behavior
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(rvPromoSelected)

            // Add scroll listener for dot indicator updates
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

            // Submit images and setup dots if available
            if (history.promoPictures.isNotEmpty()) {
                imageAdapter.submitList(history.promoPictures)
                setupDotIndicators(history.promoPictures.size)
            }

            // Bind text data
            tvDetailCategory.text = history.promoCategory
            tvPromoName.text = history.promoName
            tvOleh.text = "oleh ${history.merchantName}"
            tvDeskripsi.text = history.promoDetail
            tvPromoTitle.text = "Detail Riwayat Promo"

            // Setup TnC
            if (!history.promoTnc.isNullOrEmpty()) {
                rvSyaratDanKetentuan.layoutManager = LinearLayoutManager(this@PromoDetailActivity)
                rvSyaratDanKetentuan.adapter = SyaratDanKetentuanAdapter(history.promoTnc)
            }

            // Setup visibility untuk history view
            btnSetuju.visibility = View.GONE
            btnTolak.visibility = View.GONE
            btnPakai.visibility = View.GONE
            btnMenu.visibility = View.GONE

            // Tampilkan token jika ada
            if (!history.tokenCode.isNullOrEmpty()) {
                layoutItemDetailPromo.visibility = View.VISIBLE
                tvPromoCode.text = history.tokenCode
            } else {
                layoutItemDetailPromo.visibility = View.GONE
            }
        }

        binding.btnClose.setOnClickListener {
            handleBackNavigation()
        }
    }

    private fun bindDataToLayout(promo: PromoDomain?, source: String?) {
        binding.apply {
            // Setup RecyclerView with PagerSnapHelper
            imageAdapter = PromoImageAdapter(this@PromoDetailActivity)
            rvPromoSelected.adapter = imageAdapter
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(rvPromoSelected)

            // Add scroll listener
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

            // Submit images and setup dots
            if (promo?.pictures?.isNotEmpty() == true) {
                imageAdapter.submitList(promo.pictures)
                setupDotIndicators(promo.pictures.size)
            }

            tvDetailCategory.text = promo?.category ?: "Kategori Tidak Tersedia"
            tvPromoName.text = promo?.name ?: "Nama Promo Tidak Tersedia"
            tvOleh.text = "oleh ${promo?.category ?: "Admin"}"
            tvDeskripsi.text = promo?.detail ?: "Deskripsi Tidak Tersedia"

            Log.d("PromoDetail", "TnC List: ${promo?.tnc}")
            Log.d("PromoDetail", "TnC is null? ${promo?.tnc == null}")
            Log.d("PromoDetail", "TnC is empty? ${promo?.tnc?.isEmpty()}")

            if (!promo?.tnc.isNullOrEmpty()) {
                Log.d("PromoDetail", "Setting up TnC adapter with ${promo?.tnc?.size} items")
                rvSyaratDanKetentuan.layoutManager = LinearLayoutManager(this@PromoDetailActivity)
                rvSyaratDanKetentuan.adapter = SyaratDanKetentuanAdapter(promo?.tnc ?: emptyList()).also {
                    Log.d("PromoDetail", "Adapter created and set")
                }
            } else {
                Log.d("PromoDetail", "TnC list is null or empty")
            }
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

    private fun setupAjuanPromoView(promo: PromoDomain?) {
        with(binding) {
//
            btnSetuju.visibility = View.VISIBLE
            btnTolak.visibility = View.VISIBLE
            btnPakai.visibility = View.GONE
            btnMenu.visibility = View.GONE

            tvPromoTitle.text = "Ajuan Promo"

            btnSetuju.setOnClickListener {
                promo?.id?.let { promoId ->
                    showConfirmationDialog(
                        "Konfirmasi Promo",
                        "Apakah Anda yakin ingin menyetujui promo ini?",
                        onConfirm = { handleActivatePromoResepsionis(promoId) }
                    )
                }
            }

            btnTolak.setOnClickListener {
                promo?.id?.let { promoId ->
                    showConfirmationDialog(
                        "Tolak Promo",
                        "Apakah Anda yakin ingin menolak promo ini?",
                        onConfirm = { handleDeletePromo(promoId) }
                    )
                }
            }
        }
    }

    private fun handleBackNavigation() {
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

    private fun handleActivatePromoUser(promoId: String) {
        viewModel.getRefreshToken().observe(this) { token ->
            if (token.isNotEmpty()) {
                showLoading()
                viewModel.activatePromoUser(promoId).observe(this) { result ->
                    when (result) {
                        is Resource.Success -> {
                            hideLoading()
                            showToast("Promo berhasil digunakan")
                            navigateToStatus(
                                title = "Promo Digunakan",
                                description = "Promo telah berhasil diaktifkan segera hubungi resepsionis untuk menggunakan promo sesuai dengan tanggal yang telah ditentukan",
                                tokenCode = result.data?.tokenCode ?: "",
                                activationDate = result.data?.activationDate ?: "",
                                showCoupon = true
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

    private fun handleActivatePromoResepsionis(promoId: String) {
        viewModel.getRefreshToken().observe(this) { token ->
            if (token.isNotEmpty()) {
                showLoading()
                viewModel.activatePromoResepsionis(promoId).observe(this) { result ->
                    when (result) {
                        is Resource.Success -> {
                            hideLoading()
                            showToast("Promo berhasil disetujui")
                            navigateToStatus(
                                title = "Promo Disetujui",
                                description = "Promo telah berhasil disetujui dan akan segera aktif sesuai dengan tanggal yang telah ditentukan",
                                tokenCode = result.data?.tokenCode ?: "",
                                activationDate = result.data?.activationDate ?: "",
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
                        description = "Promo telah berhasil ditolak dan tidak akan ditampilkan",
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

    private fun setupPromoMitraView(promo: PromoDomain?) {
        with(binding) {

            btnSetuju.visibility = View.GONE
            btnTolak.visibility = View.GONE
            btnPakai.visibility = View.VISIBLE
            btnMenu.visibility = View.VISIBLE

            tvPromoTitle.text = "Detail Promo"

            btnPakai.setOnClickListener {
                promo?.id?.let { promoId ->
                    showConfirmationDialog(
                        "Gunakan Promo",
                        "Apakah Anda yakin ingin menggunakan promo ini",
                        onConfirm = { handleActivatePromoUser(promoId) }
                    )
                }
            }

            btnMenu.setOnClickListener { view ->
                val popup = PopupMenu(this@PromoDetailActivity, view)
                popup.menuInflater.inflate(R.menu.menu_promo_detail, popup.menu)

                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_edit_promo -> {
                            val intent = Intent(this@PromoDetailActivity, StaffAddPromoActivity::class.java).apply {
                                putExtra(StaffAddPromoActivity.EXTRA_IS_EDIT, true)  // Mode edit
                                putExtra(StaffAddPromoActivity.EXTRA_PROMO_DATA, promo)
                            }
                            startActivity(intent)
                            true
                        }
                        R.id.menu_delete_promo -> {
                            promo?.id?.let { promoId ->
                                showConfirmationDialog(
                                    "Hapus Promo",
                                    "Apakah Anda yakin ingin menghapus promo ini?",
                                    onConfirm = { handleDeletePromo(promoId) }
                                )
                            }
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
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
