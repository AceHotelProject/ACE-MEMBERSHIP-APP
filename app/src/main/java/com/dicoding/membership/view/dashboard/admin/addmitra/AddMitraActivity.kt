package com.dicoding.membership.view.dashboard.admin.addmitra

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.remote.response.merchants.MerchantData
import com.dicoding.core.domain.merchants.model.MerchantResultDomain
import com.dicoding.core.utils.ImageUtils
import com.dicoding.membership.R
import com.dicoding.membership.core.utils.showToast
import com.dicoding.membership.databinding.ActivityAddMitraBinding
import com.dicoding.membership.view.dashboard.admin.addmitra.addpegawai.AddPegawaiActivity
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddMitraActivity : AppCompatActivity() {

    private var _binding: ActivityAddMitraBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddMitraViewModel by viewModels()

    private var isEditMode = false
    private var merchantData: MerchantResultDomain? = null

    private var isImageSelected = false
    val selectedImages = mutableListOf<Uri>()
    private lateinit var merchantImagesAdapter: MitraImagesAdapter
    private lateinit var dots: Array<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddMitraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        validateToken()

        setupMode()

        setupImagePicker()

        setupRecyclerView()

        setupMerchantTypeDropdown()

        handleEditText()
    }

    private fun validateToken() {
        viewModel.getRefreshToken().observe(this) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(supportFragmentManager, "Token Expired Dialog")
            }
        }
    }

    private fun setupMode() {
        isEditMode = intent.getBooleanExtra(EXTRA_IS_EDIT, false)

        if (isEditMode) {
            binding.tvMitraTitle.text = "Edit Mitra"
            merchantData = intent.getParcelableExtra(EXTRA_MERCHANT_DATA)
            merchantData?.let { merchant ->
                populateForm(merchant)
            }
        } else {
            binding.tvMitraTitle.text = "Tambah Mitra"
        }
    }

    private fun setupImagePicker() {
        binding.ivMerchant.setOnClickListener {
            checkAndRequestPermissions()
        }
    }

    private fun checkAndRequestPermissions() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    openImagePicker()  // Tambahkan ini
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED),
                        PERMISSION_REQUEST_CODE
                    )
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    openImagePicker()  // Tambahkan ini
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                        PERMISSION_REQUEST_CODE
                    )
                }
            }
            else -> {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    openImagePicker()  // Tambahkan ini
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSION_REQUEST_CODE
                    )
                }
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), IMAGE_PICK_CODE)
    }

    private fun setupMerchantTypeDropdown() {
        val merchantTypes = arrayOf("Hotel", "Penginapan", "Market", "Restoran", "Hiburan", "Sekolah", "Kesehatan", "Pariwisata", "Gym")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, merchantTypes)
        binding.addMitraTipeMitra.setAdapter(adapter)
    }

    private fun setupRecyclerView() {
        merchantImagesAdapter = MitraImagesAdapter()
        binding.rvMerchantSelected.apply {
            layoutManager = LinearLayoutManager(
                this@AddMitraActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = merchantImagesAdapter

            // Scroll listener untuk dot indicator
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val position = layoutManager.findFirstVisibleItemPosition()
                    updateDotIndicator(position)
                }
            })
        }
        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    private fun updateImageViews() {
        binding.apply {
            ivMerchant.visibility = if (selectedImages.isEmpty()) View.VISIBLE else View.GONE
            rvMerchantSelected.visibility = if (selectedImages.isEmpty()) View.GONE else View.VISIBLE
            layoutDots.visibility = if (selectedImages.isEmpty()) View.GONE else View.VISIBLE
            if (selectedImages.isNotEmpty()) {
                setupDotIndicators()
            }
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

    private fun handleImageUpload(uri: Uri) {
        lifecycleScope.launch {
            viewModel.uploadFile(uri, this@AddMitraActivity).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { upload ->
                            selectedImages.add(Uri.parse(upload.fileUrl))
                            merchantImagesAdapter.addImage(Uri.parse(upload.fileUrl))
                            isImageSelected = true
                            updateImageViews()
                            checkForms()
                        }
                        hideLoading()
                    }
                    is Resource.Loading -> showLoading()
                    is Resource.Error -> {
                        hideLoading()
                        showToast("Upload failed: ${result.message}")
                    }
                    else -> hideLoading()
                }
            }
        }
    }

    fun deleteImage(fileUrl: String, position: Int) {
        viewModel.deleteFile(fileUrl).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    merchantImagesAdapter.removeImage(position)
                    selectedImages.removeAt(position)

                    if (selectedImages.isEmpty()) {
                        onImagesEmpty()
                    } else {
                        updateDotsAfterDeletion()
                    }
                    hideLoading()
                }
                is Resource.Loading -> showLoading()
                is Resource.Error -> {
                    hideLoading()
                    showToast("Failed to delete image: ${result.message}")
                }
                else -> hideLoading()
            }
        }.launchIn(lifecycleScope)
    }

    private fun showLoading() {
        binding.apply {
            // Add progress bar visibility if you have one
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            data?.let { intent ->
                if (intent.clipData != null) {
                    val count = intent.clipData!!.itemCount
                    for (i in 0 until count) {
                        val uri = intent.clipData!!.getItemAt(i).uri
                        handleImageUpload(uri)
                    }
                } else if (intent.data != null) {
                    handleImageUpload(intent.data!!)
                }
            }
        }
    }

    fun updateDotsAfterDeletion() {
        setupDotIndicators()
        if (selectedImages.isNotEmpty()) {
            updateDotIndicator(0)
        }
    }


    fun onImagesEmpty() {
        isImageSelected = false
        selectedImages.clear()
        binding.ivMerchant.visibility = View.VISIBLE
        binding.rvMerchantSelected.visibility = View.GONE
        binding.layoutDots.visibility = View.GONE
        binding.layoutDots.removeAllViews()
        checkForms()
    }

    private fun populateForm(merchant: MerchantResultDomain) {
        with(binding) {
            // Populate edit texts
            addMitraNama.setText(merchant.name)
            addMitraTipeMitra.setText(merchant.merchantType)
            addMitraDeskripsiMitra.setText(merchant.detail)

            // Handle images
            if (merchant.picturesUrl.isNotEmpty()) {
                isImageSelected = true
                ivMerchant.visibility = View.GONE
                rvMerchantSelected.visibility = View.VISIBLE
                layoutDots.visibility = View.VISIBLE

                selectedImages.clear()
                merchant.picturesUrl.forEach { imageUrl ->
                    try {
                        val uri = Uri.parse(imageUrl)
                        selectedImages.add(uri)
                        merchantImagesAdapter.addImage(uri)
                    } catch (e: Exception) {
                        Log.e("MerchantEdit", "Error parsing image URL: $e")
                    }
                }
                setupDotIndicators()
            }
        }
    }

    private fun handleEditText() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkForms()
            }
            override fun afterTextChanged(p0: Editable?) {
                checkForms()
            }
        }

        with(binding) {
            addMitraNama.addTextChangedListener(textWatcher)
            addMitraTipeMitra.addTextChangedListener(textWatcher)
            addMitraDeskripsiMitra.addTextChangedListener(textWatcher)

            addMitraLanjutkanBtn.setOnClickListener {
                if (validateForms()) {
                    val merchantData = MerchantData(
                        name = addMitraNama.text.toString(),
                        merchantType = addMitraTipeMitra.text.toString(),
                        detail = addMitraDeskripsiMitra.text.toString(),
                        picturesUrl = selectedImages.map { it.toString() }
                    )

                    val intent = Intent(this@AddMitraActivity, AddPegawaiActivity::class.java).apply {
                        putExtra(AddPegawaiActivity.EXTRA_IS_EDIT, isEditMode)
                        putExtra(AddPegawaiActivity.EXTRA_MERCHANT_DATA, merchantData)
                        if (isEditMode) {
                            putExtra(AddPegawaiActivity.EXTRA_MERCHANT_ID, this@AddMitraActivity.merchantData?.id)
                        }
                    }
                    startActivity(intent)
                } else {
                    showToast("Mohon lengkapi semua form")
                }
            }
        }
    }

    private fun validateForms(): Boolean {
        with(binding) {
            return addMitraNama.text.toString().isNotEmpty() &&
                    addMitraTipeMitra.text.toString().isNotEmpty() &&
                    addMitraDeskripsiMitra.text.toString().isNotEmpty()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun checkForms() {
        binding.addMitraLanjutkanBtn.isEnabled = validateForms()
    }

    companion object {
        const val EXTRA_IS_EDIT = "extra_is_edit"
        const val EXTRA_MERCHANT_DATA = "extra_merchant_data"
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_REQUEST_CODE = 123
    }
}