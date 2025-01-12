package com.dicoding.membership.view.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.core.data.source.Resource
import com.dicoding.membership.R
import com.dicoding.core.utils.isInternetAvailable
import com.dicoding.membership.core.utils.showToast
import com.dicoding.membership.databinding.ActivityRegisterBinding
import com.dicoding.membership.view.login.LoginActivity
import com.dicoding.membership.view.verification.VerificationActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPass.text.toString()

            registerViewModel.register(email, password).observe(this) { result ->
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
//                        If OTP Doesn't Ready
//                        navigateToLoginActivity()

//                        Change to navigate VerificationActivity
                        val id = result.data?.user?.id
                        if (id != null) {
                            navigateToOtpActivity(id)
                        } else {
                            showToast("Error: User ID not found")
                            Log.e(
                                "RegisterActivity",
                                "User ID is null after successful registration"
                            )
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun navigateToOtpActivity(id: String) {
        Log.d("RegisterActivity", "Navigating to Verification Activity with userId: $id")
        val intent = Intent(this, VerificationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(VerificationActivity.EXTRA_USER_ID, id)
            putExtra(VerificationActivity.EXTRA_AUTO_SEND_OTP, true)
        }
        startActivity(intent)
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
}