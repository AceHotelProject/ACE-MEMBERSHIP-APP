package com.dicoding.membership.view.dashboard.admin.addpromo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.membership.databinding.ActivityAdminAddPromoBinding
import com.dicoding.membership.databinding.ActivityRedeemPromoCodeBinding

class AdminAddPromoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminAddPromoBinding
    private var isImageSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAddPromoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTipeMitraDropdown()
        isButtonEnabled(false)
        handleEditText()
        setupImagePicker()
    }

    private fun setupTipeMitraDropdown() {
        val tipeMitraOptions = arrayOf("Restoran", "Cafe", "Hotel", "Retail", "Entertainment")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tipeMitraOptions)
        binding.acTipeMitra.setAdapter(adapter)
    }

    private fun isButtonEnabled(isEnabled: Boolean) {
        binding.btnSimpan.isEnabled = isEnabled

        // Tampilkan toast jika button diklik tapi gambar belum dipilih
        if (!isEnabled && !isImageSelected) {
            binding.btnSimpan.setOnClickListener {
                Toast.makeText(this, "Bukti pembayaran harus diupload", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleEditText() {
        binding.edMitraName.addTextChangedListener(object : TextWatcher {
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
        binding.acTipeMitra.addTextChangedListener(object : TextWatcher {
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
        binding.edAddPromo.addTextChangedListener(object : TextWatcher {
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
        binding.edDeskripsiPromo.addTextChangedListener(object : TextWatcher {
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
        binding.edSyaratKetentuan.addTextChangedListener(object : TextWatcher {
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
    }

    private fun setupImagePicker() {
        binding.ivPromo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }
    }

    private fun checkForms() {
        binding.apply {
            val namamitra = edMitraName.text.toString()
            val tipemitra = acTipeMitra.text.toString()
            val namapromo = edAddPromo.text.toString()
            val deskripsipromo = edDeskripsiPromo.text.toString()
            val syaratketentuan = edSyaratKetentuan.text.toString()

            // Validasi Nama Mitra
            if (namamitra.isEmpty()) {
                edMitraName.error = "Nama mitra tidak boleh kosong"
            } else if (namamitra.length > 20) {
                edMitraName.error = "Nama mitra maksimal 20 karakter"
            } else if (!namamitra.matches(Regex("^[a-zA-Z0-9 ]+$"))) {
                edMitraName.error = "Nama mitra hanya boleh huruf dan angka"
            } else {
                edMitraName.error = null
            }

            // Validasi Tipe Mitra
            if (tipemitra.isEmpty()) {
                acTipeMitra.error = "Tipe mitra harus dipilih"
            } else {
                acTipeMitra.error = null
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
                namamitra.isNotEmpty() &&
                        namamitra.length <= 20 &&
                        namamitra.matches(Regex("^[a-zA-Z0-9 ]+$")) &&
                        tipemitra.isNotEmpty() &&
                        namapromo.isNotEmpty() &&
                        namapromo.length <= 50 &&
                        namapromo.matches(Regex("^[a-zA-Z0-9 ]+$")) &&
                        deskripsipromo.isNotEmpty() &&
                        deskripsipromo.length <= 200 &&
                        syaratketentuan.isNotEmpty() &&
                        syaratketentuan.length <= 200 &&
                        isImageSelected
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                binding.ivPromo.setImageURI(uri)
                isImageSelected = true
                checkForms()
            }
        }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}