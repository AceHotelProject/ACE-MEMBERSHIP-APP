package com.dicoding.membership.view.dashboard.member.listeditmember.editmember

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dicoding.core.data.source.Resource
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityDetailMemberBinding
import com.dicoding.membership.databinding.ActivityEditMemberBinding
import com.dicoding.membership.view.dialog.GlobalTwoButtonDialog
import com.dicoding.membership.view.status.StatusTemplate
import com.dicoding.membership.view.status.StatusTemplateActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditMemberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditMemberBinding
    private val viewModel: EditMemberViewModel by viewModels()
    private var membershipId: String? = null
    private var screenTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Data passed: Title & button
        screenTitle = intent.getStringExtra("SCREEN_TITLE") ?: "Edit Membership"  // Default value if null
        val buttonText = intent.getStringExtra("BUTTON_TEXT") ?: "Simpan"  // Default value if null
        binding.detailTitle.text = screenTitle
        binding.btnAdd.text = buttonText

        // Data passed: membership ID
        membershipId = intent.getStringExtra("MEMBERSHIP_ID")

        setupBackButton()
        isButtonEnabled(false)
        setupSubmitButton()
        handleEditText()
        observeMembershipState()
    }

    private fun setupBackButton() {
        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    private fun setupSubmitButton() {
        binding.btnAdd.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun showConfirmationDialog() {
        val dialog = GlobalTwoButtonDialog().apply {
            setDialogTitle("Konfirmasi Data")
            setDialogMessage("Apakah anda sudah yakin dengan data yang sudah anda isi?")
            setOnYesClickListener {
                showLoading(true)
                submitData()
            }
            setOnNoClickListener {
                // Dialog will automatically dismiss
            }
        }
        dialog.show(supportFragmentManager, "confirmation_dialog")
    }

    private fun submitData() {
        val type = binding.tiTipeMember.text.toString()
        val duration = binding.tiTipeBulan.text.toString().toInt()
        val price = binding.tiTipeHarga.text.toString().toInt()
        val tncString = binding.tiTipeSyaratKetentuan.text.toString()
        val tncList = if (tncString.contains(";")) {
            tncString.split(";").map { it.trim() }.filter { it.isNotEmpty() }
        } else {
            listOf(tncString.trim())
        }
        Log.d("TnC Debug", "TnC List: $tncList")

        when (binding.btnAdd.text) {
            "Simpan" -> {
                membershipId?.let { id ->
                    viewModel.updateMembership(id, type, duration, price, tncList)
                } ?: showError("Membership ID not found")
            }
            "Tambah" -> {
                viewModel.createMembership(type, duration, price, tncList)
            }
        }
    }

    private fun observeMembershipState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.membershipState.collect { resource ->
                    when (resource) {
                        null -> {
                            // Initial state, form is ready for input
                            binding.btnAdd.isEnabled = true
                            showLoading(false)
                        }
                        is Resource.Loading -> {
                            binding.btnAdd.isEnabled = false
                            Log.d("DebugMember", "Loading..")
                            showLoading(true)
                        }
                        is Resource.Success -> {
                            showLoading(false)
                            var statusTemplate = StatusTemplate("", "", false, "", "", "")
                            if(screenTitle=="Tambah Membership"){
                                statusTemplate = StatusTemplate(
                                    title = "Berhasil dibuat",
                                    description = "Data membership telah berhasil dibuat",
                                    showCoupon = false,
                                    promoCode = "",
                                    expiryTime = "",
                                    buttonText = "Selesai"
                                )
                            } else {
                                statusTemplate = StatusTemplate(
                                    title = "Perbaharui Berhasil",
                                    description = "Data membership telah berhasil disimpan",
                                    showCoupon = false,
                                    promoCode = "",
                                    expiryTime = "",
                                    buttonText = "Selesai"
                                )
                            }


                            val intent = Intent(this@EditMemberActivity, StatusTemplateActivity::class.java).apply {
                                putExtra(StatusTemplateActivity.EXTRA_STATUS_TEMPLATE, statusTemplate)
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            }
                            startActivity(intent)
                            finish()
                        }
                        is Resource.Error -> {
                            showLoading(false)
                            binding.btnAdd.isEnabled = true
                            showError(resource.message ?: "An error occurred")
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun isButtonEnabled(isEnabled: Boolean) {
        binding.btnAdd.isEnabled = isEnabled
    }

    private fun handleEditText() {
        binding.tiTipeMember.addTextChangedListener(object : TextWatcher {
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

        binding.tiTipeBulan.addTextChangedListener(object : TextWatcher {
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

        binding.tiTipeHarga.addTextChangedListener(object : TextWatcher {
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

        binding.tiTipeSyaratKetentuan.addTextChangedListener(object : TextWatcher {
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

//        binding.tiTipePotonganHarga.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                checkForms()
//            }
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                checkForms()
//            }
//            override fun afterTextChanged(p0: Editable?) {
//                checkForms()
//            }
//        })
    }

    private fun checkForms() {
        binding.apply {
            val tipemember = tiTipeMember.text.toString()
            val bulanberlaku = tiTipeBulan.text.toString()
            val hargamember = tiTipeHarga.text.toString()
            val syaratketentuan = tiTipeSyaratKetentuan.text.toString()
            //val potonganharga = tiTipePotonganHarga.text.toString()

            // Validasi Tipe Member
            if (tipemember.isEmpty()) {
                binding.tiTipeMember.error = "Tipe member tidak boleh kosong"
            } else if (tipemember.length > 20) {
                binding.tiTipeMember.error = "Tipe member maksimal 20 karakter"
            } else if (!tipemember.matches(Regex("^[a-zA-Z ]+$"))) {
                binding.tiTipeMember.error = "Tipe member hanya boleh huruf"
            } else {
                binding.tiTipeMember.error = null
            }

            // Validasi Bulan Berlaku
            if (bulanberlaku.isEmpty()) {
                binding.tiTipeBulan.error = "Bulan berlaku tidak boleh kosong"
            } else {
                try {
                    val bulanValue = bulanberlaku.toInt()
                    if (bulanValue <= 0) {
                        binding.tiTipeBulan.error = "Bulan berlaku tidak boleh 0 atau negatif"
                    } else if (bulanValue > 60) {
                        binding.tiTipeBulan.error = "Bulan berlaku maksimal 60"
                    } else {
                        binding.tiTipeBulan.error = null
                    }
                } catch (e: NumberFormatException) {
                    binding.tiTipeBulan.error = "Bulan berlaku harus berupa angka"
                }
            }

            // Validasi Harga Member
            if (hargamember.isEmpty()) {
                binding.tiTipeHarga.error = "Harga member tidak boleh kosong"
            } else {
                try {
                    val hargaValue = hargamember.toInt()
                    if (hargaValue <= 0) {
                        binding.tiTipeHarga.error = "Harga member tidak boleh 0 atau negatif"
                    } else if (hargaValue > 50000000) {
                        binding.tiTipeHarga.error = "Harga member maksimal 50.000.000"
                    } else {
                        binding.tiTipeHarga.error = null
                    }
                } catch (e: NumberFormatException) {
                    binding.tiTipeHarga.error = "Harga member harus berupa angka"
                }
            }

            // Validasi Syarat dan Ketentuan
            if (syaratketentuan.isEmpty()) {
                binding.tiTipeSyaratKetentuan.error = "Syarat dan ketentuan tidak boleh kosong"
            } else if (syaratketentuan.length > 200) {
                binding.tiTipeSyaratKetentuan.error = "Syarat dan ketentuan maksimal 200 karakter"
            } else {
                binding.tiTipeSyaratKetentuan.error = null
            }

            // Validasi Potongan Harga
//            if (potonganharga.isEmpty()) {
//                binding.tiTipePotonganHarga.error = "Potongan harga tidak boleh kosong"
//            } else {
//                try {
//                    val potonganValue = potonganharga.toInt()
//                    if (potonganValue <= 0) {
//                        binding.tiTipePotonganHarga.error = "Potongan harga tidak boleh 0 atau negatif"
//                    } else if (potonganValue > 1000000) {
//                        binding.tiTipePotonganHarga.error = "Potongan harga maksimal 1.000.000"
//                    } else {
//                        binding.tiTipePotonganHarga.error = null
//                    }
//                } catch (e: NumberFormatException) {
//                    binding.tiTipePotonganHarga.error = "Potongan harga harus berupa angka"
//                }
//            }

            // Enable button jika semua validasi terpenuhi
            isButtonEnabled(
                tipemember.isNotEmpty() && tipemember.length <= 20 && tipemember.matches(Regex("^[a-zA-Z ]+$")) &&
                        bulanberlaku.isNotEmpty() && try { bulanberlaku.toInt() in 1..60 } catch (e: NumberFormatException) { false } &&
                        hargamember.isNotEmpty() && try { hargamember.toInt() in 1..50000000 } catch (e: NumberFormatException) { false } &&
                        syaratketentuan.isNotEmpty() && syaratketentuan.length <= 200
                        //potonganharga.isNotEmpty() && try { potonganharga.toInt() in 1..1000000 } catch (e: NumberFormatException) { false }
            )
        }
    }

    private fun showLoading(boolean: Boolean) {
        binding.loadingOverlay.visibility = if(boolean) View.VISIBLE else View.GONE
        binding.btnClose.visibility = if(boolean) View.GONE else View.VISIBLE
        binding.detailTitle.visibility = if(boolean) View.GONE else View.VISIBLE
        binding.mainSv.visibility = if(boolean) View.GONE else View.VISIBLE

    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}