package com.dicoding.membership.view.dashboard.floatingvalidasi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityRedeemPromoCodeBinding
import com.dicoding.membership.databinding.ActivityRegisterBinding
import com.dicoding.membership.databinding.ActivityValidasiBinding
import com.dicoding.membership.view.dashboard.MainActivity

class ValidasiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityValidasiBinding
    private var isImageSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityValidasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isButtonEnabled(false)
        handleEditText()
        handleMenuButton()
        setupImagePicker()
    }

    private fun isButtonEnabled(isEnabled: Boolean) {
        binding.btnContinue.isEnabled = isEnabled

        if (!isEnabled && !isImageSelected) {
            binding.btnContinue.setOnClickListener {
                Toast.makeText(this, "Bukti pembayaran harus diupload", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleEditText() {
        binding.tiKode.addTextChangedListener(object : TextWatcher {
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

    private fun handleMenuButton() {
        binding.btnClose.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun setupImagePicker() {
        binding.ivBuktipembayaran.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }
    }

    private fun checkForms() {
        binding.apply {
            val kodepengguna = tiKode.text.toString()

            // Validasi Kode Pengguna
            if (kodepengguna.isEmpty()) {
                binding.tiKode.error = "Kode pengguna tidak boleh kosong"
            } else if (kodepengguna.length > 8) {
                binding.tiKode.error = "Kode pengguna maksimal 8 karakter"
            } else {
                binding.tiKode.error = null
            }

            // Enable button jika semua validasi terpenuhi
            isButtonEnabled(
                kodepengguna.isNotEmpty() &&
                        kodepengguna.length <= 8 &&
                        isImageSelected
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                binding.ivBuktipembayaran.setImageURI(uri)
                isImageSelected = true
                checkForms()
            }
        }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }

}