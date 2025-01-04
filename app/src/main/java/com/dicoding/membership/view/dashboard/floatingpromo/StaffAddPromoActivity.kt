package com.dicoding.membership.view.dashboard.floatingpromo

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.core.data.source.Resource
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityAdminAddPromoBinding
import com.dicoding.membership.view.dashboard.MainActivity
import com.dicoding.membership.view.dialog.GlobalTwoButtonDialog
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import com.dicoding.membership.view.status.StatusTemplate
import com.dicoding.membership.view.status.StatusTemplateActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class StaffAddPromoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAddPromoBinding

    private val staffAddPromoViewModel: StaffAddPromoViewModel by viewModels()

    private var isImageSelected = false

    private lateinit var dots: Array<ImageView>

    private lateinit var promoImagesAdapter: PromoImagesAdapter

    private val selectedImages = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAddPromoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTipeMemberDropdown()

        setupCategoryPromoDropdown()

        isButtonEnabled(false)

        validateToken()

        handleEditText()

        handleMenuButton()

        setupImagePicker()

//        setupSubmitButton()

        setupRecyclerView()
    }

    private fun validateToken() {
        staffAddPromoViewModel.getRefreshToken().observe(this) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(supportFragmentManager, "Token Expired Dialog")
            }
        }
    }

    private fun handleMenuButton() {
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }


    private fun setupTipeMemberDropdown() {
        val tipeMitraOptions = arrayOf("Platinum", "Gold", "Silver")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tipeMitraOptions)
        binding.acTipeMember.setAdapter(adapter)
    }

    private fun setupCategoryPromoDropdown() {
        val categoryPromoOptions = arrayOf("Discount", "Buy One Get One", "Cashback", "Flash Sale", "Bundle Offer")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryPromoOptions)
        binding.acCategoryPromo.setAdapter(adapter)
    }

    private fun isButtonEnabled(isEnabled: Boolean) {
        binding.btnSimpan.isEnabled = isEnabled
    }

    private fun handleEditText() {
        binding.acTipeMember.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }
            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.acCategoryPromo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }
            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.edAddPromo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }
            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.edDeskripsiPromo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }
            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding.edSyaratKetentuan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })
        binding.edStartDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }
            override fun afterTextChanged(p0: Editable?) {
                checkForms()
            }
        })

        binding.edEndDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }
            override fun afterTextChanged(p0: Editable?) {
                checkForms()
            }
        })

// Keep the date picker click listeners separate
        binding.edStartDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)

                    // Format tanggal ke bentuk yang diinginkan
                    val dateFormat = SimpleDateFormat("EEE, dd MMMM yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)
                    Log.d("DatePicker", "Selected Date: $formattedDate")

                    binding.edStartDate.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        binding.edEndDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)

                    // Format tanggal ke bentuk yang diinginkan
                    val dateFormat = SimpleDateFormat("EEE, dd MMMM yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)
                    Log.d("DatePicker", "Selected Date: $formattedDate")

                    binding.edEndDate.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        binding.edMaxUse.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun setupImagePicker() {
        binding.ivPromo.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(intent, "Select Pictures"), IMAGE_PICK_CODE)
        }
    }

    private fun setupDotIndicators() {
        binding.layoutDots.removeAllViews()
        dots = Array(selectedImages.size) { _ ->
            val dot = ImageView(this)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 0, 8, 0)
            }
            dot.layoutParams = params
            dot.setImageResource(R.drawable.icons_dot_inactive)
            binding.layoutDots.addView(dot)
            dot
        }
        // Set dot pertama sebagai active
        if (dots.isNotEmpty()) {
            dots[0].setImageResource(R.drawable.icons_dot_active)
        }
    }

    private fun setupRecyclerView() {
        promoImagesAdapter = PromoImagesAdapter()
        binding.rvPromoSelected.apply {
            layoutManager = LinearLayoutManager(this@StaffAddPromoActivity,
                LinearLayoutManager.HORIZONTAL, false)
            adapter = promoImagesAdapter

            // Tambahkan scroll listener
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val position = layoutManager.findFirstVisibleItemPosition()
                    updateDotIndicator(position)
                }
            })
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


    private fun checkForms() {
        binding.apply {
            val namapromo = edAddPromo.text.toString()
            val tipemember = acTipeMember.text.toString()
            val categorypromo = acCategoryPromo.text.toString()
            val deskripsipromo = edDeskripsiPromo.text.toString()
            val syaratketentuan = edSyaratKetentuan.text.toString()
            val maxuse = edMaxUse.text.toString()
            val startDate = edStartDate.text.toString()
            val endDate = edEndDate.text.toString()

//            // Validasi Tipe Mitra
//            if (tipemember.isEmpty()) {
//                acTipeMember.error = "Tipe mitra harus dipilih"
//            } else {
//                acTipeMember.error = null
//            }
//            // Validasi Tipe Mitra
//            if (categorypromo.isEmpty()) {
//                acCategoryPromo.error = "Tipe mitra harus dipilih"
//            } else {
//                acCategoryPromo.error = null
//            }

            // Validasi Max Use
            if (maxuse.isEmpty()) {
                edMaxUse.error = "Maksimum penggunaan tidak boleh kosong"
            } else {
                edMaxUse.error = null
            }

            // Validasi Tanggal
            if (startDate.isEmpty()) {
                edStartDate.error = "Tanggal mulai tidak boleh kosong"
            } else {
                edStartDate.error = null
            }

            if (endDate.isEmpty()) {
                edEndDate.error = "Tanggal berakhir tidak boleh kosong"
            } else {
                // Check if endDate is before startDate
                try {
                    val dateFormat = SimpleDateFormat("EEE, dd MMMM yyyy", Locale.getDefault())
                    val parsedStartDate = dateFormat.parse(startDate)
                    val parsedEndDate = dateFormat.parse(endDate)

                    if (parsedStartDate != null && parsedEndDate != null &&
                        parsedEndDate.before(parsedStartDate)) {
                        edEndDate.error = "Tanggal berakhir tidak boleh kurang dari tanggal mulai"
                    } else {
                        edEndDate.error = null
                    }
                } catch (e: Exception) {
                    edEndDate.error = "Format tanggal tidak valid"
                }
            }

            // Validasi Nama Promo
            if (namapromo.isEmpty()) {
                edAddPromo.error = "Nama promo tidak boleh kosong"
            } else if (namapromo.length > 50) {
                edAddPromo.error = "Nama promo maksimal 50 karakter"
            } else if (!namapromo.matches(Regex("^[a-zA-Z0-9 ]+$"))) {
                edAddPromo.error = "Nama promo hanya boleh huruf dan angka"
            } else {
                edAddPromo.error = null
            }

            // Validasi Deskripsi Promo
            if (deskripsipromo.isEmpty()) {
                edDeskripsiPromo.error = "Deskripsi promo tidak boleh kosong"
            } else if (deskripsipromo.length > 200) {
                edDeskripsiPromo.error = "Deskripsi promo maksimal 200 karakter"
            } else {
                edDeskripsiPromo.error = null
            }

            // Validasi Syarat Ketentuan
            if (syaratketentuan.isEmpty()) {
                edSyaratKetentuan.error = "Syarat ketentuan tidak boleh kosong"
            } else if (syaratketentuan.length > 200) {
                edSyaratKetentuan.error = "Syarat ketentuan maksimal 200 karakter"
            } else {
                edSyaratKetentuan.error = null
            }

            // Enable button jika semua validasi terpenuhi
            isButtonEnabled(
                        tipemember.isNotEmpty() &&
                                categorypromo.isNotEmpty() &&
                        namapromo.isNotEmpty() &&
                        namapromo.length <= 50 &&
                        namapromo.matches(Regex("^[a-zA-Z0-9 ]+$")) &&
                        deskripsipromo.isNotEmpty() &&
                        deskripsipromo.length <= 200 &&
                        syaratketentuan.isNotEmpty() &&
                        syaratketentuan.length <= 200 &&
                                maxuse.isNotEmpty() &&
                                startDate.isNotEmpty() &&
                                endDate.isNotEmpty() &&
                                edEndDate.error == null &&
                        isImageSelected
            )
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) {
                // Handle multiple images
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    selectedImages.add(imageUri)
                }
                isImageSelected = true
                binding.ivPromo.visibility = View.GONE
                binding.rvPromoSelected.visibility = View.VISIBLE
                binding.layoutDots.visibility = View.VISIBLE

                // Update RecyclerView
                selectedImages.forEach { uri ->
                    promoImagesAdapter.addImage(uri)
                }
                setupDotIndicators()
            } else if (data?.data != null) {
                // Handle single image
                val imageUri = data.data!!
                selectedImages.add(imageUri)
                isImageSelected = true
                binding.ivPromo.visibility = View.GONE
                binding.rvPromoSelected.visibility = View.VISIBLE
                binding.layoutDots.visibility = View.VISIBLE
                promoImagesAdapter.addImage(imageUri)
                setupDotIndicators()
            }
            checkForms()
        }
    }

    fun onImagesEmpty() {
        isImageSelected = false
        selectedImages.clear()
        binding.ivPromo.visibility = View.VISIBLE
        binding.rvPromoSelected.visibility = View.GONE
        binding.layoutDots.visibility = View.GONE
        binding.layoutDots.removeAllViews()
        checkForms()
    }

    fun updateDotsAfterDeletion() {
        setupDotIndicators()
        if (selectedImages.isNotEmpty()) {
            updateDotIndicator(0)
        }
    }

    private fun submitForm() {
        val name = binding.edAddPromo.text.toString()
        val memberType = binding.acTipeMember.text.toString()
        val category = binding.acCategoryPromo.text.toString()
        val description = binding.edDeskripsiPromo.text.toString()

        // Convert TnC string to list
        val tncList = binding.edSyaratKetentuan.text.toString()
            .split(";")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val maxUse = binding.edMaxUse.text.toString().toIntOrNull() ?: 0

        // Format dates
        val dateFormat = SimpleDateFormat("EEE, dd MMMM yyyy", Locale.getDefault())
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

        try {
            val startDate = dateFormat.parse(binding.edStartDate.text.toString())?.let {
                isoFormat.format(it)
            } ?: throw Exception("Invalid start date")

            val endDate = dateFormat.parse(binding.edEndDate.text.toString())?.let {
                isoFormat.format(it)
            } ?: throw Exception("Invalid end date")

            // Get token and create promo
            staffAddPromoViewModel.getRefreshToken().observe(this) { token ->
                if (token.isNotEmpty()) {
                    staffAddPromoViewModel.createPromo(
                        name = name,
                        token = "647f4a1e8a5c3f9b9fef5678", // Will be set by backend
                        category = category,
                        detail = description,
                        pictures = listOf(), // Will be handled separately
                        tnc = tncList,
                        startDate = startDate,
                        endDate = endDate,
                        memberType = memberType,
                        merchantId = "648f5a2b9a6d4e0c9bef6789", // Will be set by backend
                        maximalUse = maxUse,
                        used = 0, // Will be set by backend
                        isActive = false // Will be set by backend
                    ).observe(this) { result ->
                        when(result) {
                            is Resource.Success -> {
                                hideLoading()
                                showToast("Promo berhasil dibuat")
                                navigateToStatus()
                            }
                            is Resource.Loading -> {
                                showLoading()
                            }
                            is Resource.Message -> {
                                hideLoading()
                                showToast(result.message ?: "Unknown message")
                            }
                            is Resource.Error -> {
                                hideLoading()
                                showToast(result.message ?: "Terjadi kesalahan")
                            }
                            else -> {
                                showToast(result.message ?: "Unknown message")
                            }
                        }
                    }
                } else {
                    showToast("Token tidak valid")
                }
            }

        } catch (e: Exception) {
            showToast("Format tanggal tidak valid: ${e.message}")
        }
    }

//    private fun setupSubmitButton() {
//        binding.btnSimpan.setOnClickListener {
//            if (binding.btnSimpan.isEnabled) {
//                // Proses submit form
//                val dialog = GlobalTwoButtonDialog().apply {
//                    setDialogTitle("Konfirmasi Promo")
//                    setDialogMessage("Apakah Anda yakin ingin membuat promo ini?")
//                    setOnYesClickListener {
//                        // Proses submit form ketika user menekan Ya
//                        submitForm()
//                    }
//                    setOnNoClickListener {
//                        // Optional: tambahkan aksi ketika user menekan Tidak
//                    }
//                }
//                dialog.show(supportFragmentManager, "ConfirmationDialog")
//            } else {
//                binding.apply {
//                    val namapromo = edAddPromo.text.toString()
//                    val tipemember = acTipeMember.text.toString()
//                    val categorypromo = acCategoryPromo.text.toString()
//                    val deskripsipromo = edDeskripsiPromo.text.toString()
//                    val syaratketentuan = edSyaratKetentuan.text.toString()
//                    val maxuse = edMaxUse.text.toString()
//                    val startDate = edStartDate.text.toString()
//                    val endDate = edEndDate.text.toString()
//
//                    var message = "Mohon lengkapi:"
//
//                    if (tipemember.isEmpty()) message += "\n- Tipe Member"
//                    if (categorypromo.isEmpty()) message += "\n- Kategori Promo"
//                    if (namapromo.isEmpty()) message += "\n- Nama Promo"
//                    if (deskripsipromo.isEmpty()) message += "\n- Deskripsi Promo"
//                    if (syaratketentuan.isEmpty()) message += "\n- Syarat dan Ketentuan"
//                    if (maxuse.isEmpty()) message += "\n- Maksimal Penggunaan"
//                    if (startDate.isEmpty()) message += "\n- Tanggal Mulai"
//                    if (endDate.isEmpty()) message += "\n- Tanggal Berakhir"
//                    if (!isImageSelected || selectedImages.isEmpty()) message += "\n- Gambar Promo"
//
//                    // Tambahan validasi khusus
//                    if (namapromo.isNotEmpty() && !namapromo.matches(Regex("^[a-zA-Z0-9 ]+$"))) {
//                        message += "\n- Nama promo hanya boleh huruf dan angka"
//                    }
//                    if (namapromo.length > 50) {
//                        message += "\n- Nama promo maksimal 50 karakter"
//                    }
//                    if (deskripsipromo.length > 200) {
//                        message += "\n- Deskripsi promo maksimal 200 karakter"
//                    }
//                    if (syaratketentuan.length > 200) {
//                        message += "\n- Syarat ketentuan maksimal 200 karakter"
//                    }
//
//                    // Validasi tanggal
//                    if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
//                        try {
//                            val dateFormat = SimpleDateFormat("EEE, dd MMMM yyyy", Locale.getDefault())
//                            val parsedStartDate = dateFormat.parse(startDate)
//                            val parsedEndDate = dateFormat.parse(endDate)
//
//                            if (parsedStartDate != null && parsedEndDate != null &&
//                                parsedEndDate.before(parsedStartDate)) {
//                                message += "\n- Tanggal berakhir tidak boleh kurang dari tanggal mulai"
//                            }
//                        } catch (e: Exception) {
//                            message += "\n- Format tanggal tidak valid"
//                        }
//                    }
//
//                    if (message != "Mohon lengkapi:") {
//                        // Gunakan AlertDialog untuk menampilkan pesan
//                        AlertDialog.Builder(this@StaffAddPromoActivity)
//                            .setTitle("Peringatan")
//                            .setMessage(message)
//                            .setPositiveButton("OK") { dialog, _ ->
//                                dialog.dismiss()
//                            }
//                            .show()
//                    }
//                }
//            }
//        }
//    }


    private fun navigateToStatus() {
        val statusTemplate = StatusTemplate(
            title = "Promo Berhasil Dibuat",
            description = "Promo telah berhasil dibuat dan akan segera aktif sesuai dengan tanggal yang telah ditentukan",
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

    private fun showLoading() {
        binding.apply {
            btnSimpan.isEnabled = false
            // Add progress bar visibility if you have one
            progressBar.visibility = View.VISIBLE
            loadingOverlay.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        binding.apply {
            btnSimpan.isEnabled = true
            // Add progress bar visibility if you have one
            // progressBar.visibility = View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}