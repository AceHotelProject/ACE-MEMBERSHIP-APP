package com.dicoding.membership.view.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.core.data.source.Resource
import com.dicoding.membership.R
import com.dicoding.core.utils.isInternetAvailable
import com.dicoding.membership.core.utils.showToast
import com.dicoding.membership.databinding.ActivityLoginBinding
import com.dicoding.membership.view.dashboard.MainActivity
import com.dicoding.membership.view.verification.VerificationActivity
import dagger.hilt.android.AndroidEntryPoint
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.UUID

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        logDeviceInfo()

        isButtonEnabled(false)

        setupActionBar()

        handleEditText()

        handleButtonLogin()
    }

    private fun setupActionBar() {
        supportActionBar?.hide()
    }

    @SuppressLint("ClickableViewAccessibility", "UseCompatLoadingForDrawables")
    private fun handleEditText() {
        binding.edLoginEmail.addTextChangedListener(object : TextWatcher {
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

        binding.edLoginPass.addTextChangedListener(object : TextWatcher {
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

        binding.layoutLoginPass.setEndIconOnClickListener {
            if (binding.edLoginPass.transformationMethod == PasswordTransformationMethod.getInstance()) {
                binding.edLoginPass.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.layoutLoginPass.endIconDrawable = getDrawable(R.drawable.icons_no_see_pass)
            } else {
                binding.edLoginPass.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.layoutLoginPass.endIconDrawable = getDrawable(R.drawable.icons_see_pass)
            }

            binding.edLoginPass.setSelection(binding.edLoginPass.text!!.length)
        }
    }

    private fun checkForms() {
        binding.apply {
            val email = edLoginEmail.text.toString()
            val pass = edLoginPass.text.toString()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.layoutLoginEmail.error = getString(R.string.wrong_email_format)
            } else {
                binding.layoutLoginEmail.error = null
            }

            when {
                pass.isEmpty() -> {
                    binding.layoutLoginPass.error = "Password tidak boleh kosong"
                }
//                pass.length < 8 -> {
//                    binding.layoutLoginPass.error = getString(R.string.wrong_password_format)
//                }
//                !pass.any { it.isDigit() } -> {
//                    binding.layoutLoginPass.error = "Password harus mengandung angka"
//                }
//                !pass.any { it.isUpperCase() } -> {
//                    binding.layoutLoginPass.error = "Password harus mengandung huruf besar"
//                }
//                !pass.any { it.isLowerCase() } -> {
//                    binding.layoutLoginPass.error = "Password harus mengandung huruf kecil"
//                }
//                !pass.any { !it.isLetterOrDigit() } -> {
//                    binding.layoutLoginPass.error = "Password harus mengandung karakter khusus"
//                }
                else -> {
                    binding.layoutLoginPass.error = null
                }
            }

            isButtonEnabled(
                email.isNotEmpty()
                        && pass.isNotEmpty()
//                        && isPasswordValid(pass)
                        && Patterns.EMAIL_ADDRESS.matcher(email).matches()
            )
        }
    }

    private fun isButtonEnabled(isEnabled: Boolean) {
        binding.btnLogin.isEnabled = isEnabled
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

    private fun handleButtonLogin() {
        binding.btnLogin.setOnClickListener {

            hideKeyboard()

            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPass.text.toString()
            val androidId = getDeviceIdentifier(this)

            loginViewModel.login(email, password, androidId).observe(this) { result ->
                when (result) {
                    is Resource.Error -> {
                        hideLoading()
                        isButtonEnabled(true)

                        if (!isInternetAvailable(this)) {
                            showToast(getString(R.string.check_internet))
                        } else {
                            showToast("Pastikan email dan password telah benar")
                        }
                    }

                    is Resource.Loading -> {
                        showLoading()
                        isButtonEnabled(false)
                    }

                    is Resource.Message -> {
                        hideLoading()
                        isButtonEnabled(true)

                        Log.d("LoginActivity", result.message.toString())
                    }

                    is Resource.Success -> {
                        result.data?.let { loginData ->
                            // Simpan data user
                            loginViewModel.insertCacheUser(loginData)

                            // Validasi token
                            loginViewModel.executeValidateToken(
                                loginData.tokens.refreshToken.token.orEmpty(),
                                loginData.tokens.accessToken.token.orEmpty()
                            ).observe(this) { token ->
                                if (token.isNotEmpty()) {
                                    navigateToMainActivity()
                                } else {
                                    showToast("Token tidak valid")
                                    hideLoading()
                                    isButtonEnabled(true)
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceIdentifier(context: Context): String {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        val deviceInfo = StringBuilder().apply {
            append(androidId)
            append(Build.BOARD)
            append(Build.BRAND)
            append(Build.DEVICE)
            append(Build.MANUFACTURER)
            append(Build.MODEL)
            append(Build.PRODUCT)
        }.toString()

        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(deviceInfo.toByteArray(StandardCharsets.UTF_8))
            hash.fold("") { str, it -> str + "%02x".format(it) }
        } catch (e: Exception) {
            UUID.randomUUID().toString()
        }
    }

    private fun logDeviceInfo() {
        Log.d("DeviceInfo", "Board: ${Build.BOARD}")
        Log.d("DeviceInfo", "Brand: ${Build.BRAND}")
        Log.d("DeviceInfo", "Device: ${Build.DEVICE}")
        Log.d("DeviceInfo", "Hardware: ${Build.HARDWARE}")
        Log.d("DeviceInfo", "Manufacturer: ${Build.MANUFACTURER}")
        Log.d("DeviceInfo", "Model: ${Build.MODEL}")
        Log.d("DeviceInfo", "Product: ${Build.PRODUCT}")
        Log.d("DeviceInfo", "Device ID: ${getDeviceIdentifier(this)}")
    }

    private fun navigateToMainActivity() {
        loginViewModel.getUser().observe(this) { loginData ->
            val token = loginData.tokens.accessToken.token.orEmpty()
            val isEmailVerified = loginData.user.isEmailVerified
            val id = loginData.user.id

            when {
                token.isEmpty() -> {
                    showToast("Sesi login tidak valid")
                    hideLoading()
                    isButtonEnabled(true)
                }
                //                                    If OTP Ready for Testing
                !isEmailVerified -> {
                    Log.d("LoginActivity", "Email belum terverifikasi, mengarahkan ke verifikasi OTP")
                    hideLoading()
                    startActivity(Intent(this, VerificationActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        putExtra(EXTRA_USER_ID, id)
                        putExtra(EXTRA_AUTO_SEND_OTP, true)
                    })
                    finish()
                }
                else -> {
                    Log.d("LoginActivity", "Email terverifikasi, mengarahkan ke halaman utama")
                    hideLoading()
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                }
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    companion object {
        const val EXTRA_USER_ID = "USER_ID"
        const val EXTRA_AUTO_SEND_OTP = "AUTO_SEND_OTP"
    }
}