package com.dicoding.membership.view.dashboard.home.member.mregister

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.membership.databinding.ActivityHomeMemberRegisterBinding
import com.dicoding.membership.view.template.StatusTemplateActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeMemberRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeMemberRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeMemberRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isButtonEnabled(false)
        handleEditText()
        handleButtonRegister()
    }

    private fun handleEditText() {
        binding.edRegisterName.addTextChangedListener(object : TextWatcher {
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

        binding.edRegisterNik.addTextChangedListener(object : TextWatcher {
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

        binding.edRegisterPhone.addTextChangedListener(object : TextWatcher {
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

        binding.edRegisterAddress.addTextChangedListener(object : TextWatcher {
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

    private fun checkForms() {
        binding.apply {
            val name = edRegisterName.text.toString()
            val nik = edRegisterNik.text.toString()
            val phone = edRegisterPhone.text.toString()
            val address = edRegisterAddress.text.toString()

            // Name validation (not empty and minimum length)
            if (name.length < 3) {
                layoutRegisterName.error = "Nama harus minimal 3 karakter"
            } else {
                layoutRegisterName.error = null
            }

            // NIK validation (16 digits)
            if (nik.length != 16) {
                layoutRegisterNik.error = "NIK harus 16 digit"
            } else {
                layoutRegisterNik.error = null
            }

            // Phone validation (minimum length and starts with proper prefix)
            if (!isValidPhoneNumber(phone)) {
                layoutRegisterPhone.error = "Format nomor telepon tidak valid"
            } else {
                layoutRegisterPhone.error = null
            }

            // Address validation (not empty and minimum length)
            if (address.length < 10) {
                layoutRegisterAddress.error = "Alamat terlalu pendek"
            } else {
                layoutRegisterAddress.error = null
            }

            // Enable button only if all fields are valid
            isButtonEnabled(
                name.length >= 3 &&
                        nik.length == 16 &&
                        isValidPhoneNumber(phone) &&
                        address.length >= 10
            )
        }
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        // Basic Indonesian phone number validation
        // Starts with 08 and has 10-13 digits
        return phone.matches(Regex("^08\\d{8,11}$"))
    }

    private fun isButtonEnabled(isEnabled: Boolean) {
        binding.btnRegister.isEnabled = isEnabled
    }

    private fun showLoading(isLoading: Boolean) {
        // Add ProgressBar to your layout if needed
        // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun handleButtonRegister() {
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, StatusTemplateActivity::class.java).apply {
                putExtra(StatusTemplateActivity.EXTRA_TITLE, "Membership Berhasil")
                putExtra(StatusTemplateActivity.EXTRA_DESCRIPTION, "Membership Berhasil Membership Berhasil Membership Berhasil Membership Berhasil Membership Berhasil Membership Berhasil ")
                putExtra(StatusTemplateActivity.EXTRA_SHOW_COUPON, false)
            }
            startActivity(intent)
        }
    }

}