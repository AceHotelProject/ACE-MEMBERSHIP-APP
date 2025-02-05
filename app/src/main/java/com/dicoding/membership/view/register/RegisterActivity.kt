package com.dicoding.membership.view.register

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
import com.dicoding.core.utils.isInternetAvailable
import com.dicoding.membership.R
import com.dicoding.membership.core.utils.showToast
import com.dicoding.membership.databinding.ActivityRegisterBinding
import com.dicoding.membership.view.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.UUID

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        logDeviceInfo()

        isButtonEnabled(false)
        handleEditText()
        handleButtonRegister()
    }

    private fun handleEditText() {
        binding.edRegisterEmail.addTextChangedListener(object : TextWatcher {
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

        binding.edRegisterPass.addTextChangedListener(object : TextWatcher {
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

        binding.edRegisterPassConfirm.addTextChangedListener(object : TextWatcher {
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

        binding.layoutRegisterPass.setEndIconOnClickListener {
            if (binding.edRegisterPass.transformationMethod == PasswordTransformationMethod.getInstance()) {
                binding.edRegisterPass.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.layoutRegisterPass.endIconDrawable = getDrawable(R.drawable.icons_no_see_pass)
            } else {
                binding.edRegisterPass.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.layoutRegisterPass.endIconDrawable = getDrawable(R.drawable.icons_see_pass)
            }
            binding.edRegisterPass.setSelection(binding.edRegisterPass.text!!.length)
        }

        binding.layoutRegisterPassConfirm.setEndIconOnClickListener {
            if (binding.edRegisterPassConfirm.transformationMethod == PasswordTransformationMethod.getInstance()) {
                binding.edRegisterPassConfirm.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.layoutRegisterPassConfirm.endIconDrawable = getDrawable(R.drawable.icons_no_see_pass)
            } else {
                binding.edRegisterPassConfirm.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.layoutRegisterPassConfirm.endIconDrawable = getDrawable(R.drawable.icons_see_pass)
            }
            binding.edRegisterPassConfirm.setSelection(binding.edRegisterPassConfirm.text!!.length)
        }
    }

    private fun checkForms() {
        binding.apply {
            val email = edRegisterEmail.text.toString()
            val pass = edRegisterPass.text.toString()
            val passConfirm = edRegisterPassConfirm.text.toString()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.layoutRegisterEmail.error = getString(R.string.wrong_email_format)
            } else {
                binding.layoutRegisterEmail.error = null
            }

            if (pass.length < 8) {
                binding.layoutRegisterPass.error = getString(R.string.wrong_password_format)
            } else {
                binding.layoutRegisterPass.error = null
            }

            if (passConfirm.isNotEmpty() && pass != passConfirm) {
                binding.layoutRegisterPassConfirm.error = getString(R.string.password_mismatch)
            } else {
                binding.layoutRegisterPassConfirm.error = null
            }

            isButtonEnabled(
                email.isNotEmpty()
                        && pass.isNotEmpty()
                        && pass.length >= 8
                        && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                        && pass == passConfirm
            )
        }
    }

    private fun isButtonEnabled(isEnabled: Boolean) {
        binding.btnRegister.isEnabled = isEnabled
    }


    private fun handleButtonRegister() {
        binding.tvLogin.setOnClickListener{
            navigateToLoginActivity()
        }
        binding.btnRegister.setOnClickListener {

            hideKeyboard()

            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPass.text.toString()
            val androidId = getDeviceIdentifier(this)

            registerViewModel.register(email, password, androidId).observe(this) { result ->
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

                        Log.d("RegisterActivity", result.message.toString())
                    }

                    is Resource.Success -> {
                        hideLoading()
                        isButtonEnabled(true)
                        showToast(getString(R.string.register_success))

                        navigateToLoginActivity()
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

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            loadingOverlay.visibility = View.VISIBLE
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun hideLoading() {
        binding.apply {
            progressBar.visibility = View.GONE
            loadingOverlay.visibility = View.GONE
        }
    }
}