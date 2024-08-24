package com.dicoding.membership.view.login

import android.annotation.SuppressLint
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
import com.dicoding.membership.core.utils.isInternetAvailable
import com.dicoding.membership.core.utils.showToast
import com.dicoding.membership.databinding.ActivityLoginBinding
import com.dicoding.membership.view.dashboard.MainActivity
import com.dicoding.membership.view.register.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

            if (pass.length < 8) {
                binding.layoutLoginPass.error = getString(R.string.wrong_password_format)
            } else {
                binding.layoutLoginPass.error = null
            }

            isButtonEnabled(
                email.isNotEmpty()
                        && pass.isNotEmpty()
                        && pass.length >= 8
                        && Patterns.EMAIL_ADDRESS.matcher(email).matches()
            )
        }
    }

    private fun isButtonEnabled(isEnabled: Boolean) {
        binding.btnLogin.isEnabled = isEnabled
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun handleButtonLogin() {
        binding.tvRegister.setOnClickListener {
            navigateToRegisterActivity()
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPass.text.toString()

            loginViewModel.login(email, password).observe(this) { result ->
                when (result) {
                    is Resource.Error -> {
                        showLoading(false)
                        isButtonEnabled(true)

                        if (!isInternetAvailable(this)) {
                            showToast(getString(R.string.check_internet))
                        } else {
                            showToast(getString(R.string.check_data))
                        }
                    }

                    is Resource.Loading -> {
                        showLoading(true)
                        isButtonEnabled(false)
                    }

                    is Resource.Message -> {
                        showLoading(false)
                        isButtonEnabled(true)

                        Log.d("LoginActivity", result.message.toString())
                    }

                    is Resource.Success -> {
                        val loginData = result.data?.data
                        if (loginData != null) {
                            val token = loginData.token
                            loginViewModel.executeValidateToken(token ?: "").observe(this) { validatedToken ->
                                if (validatedToken.isNotEmpty()) {
                                    loginViewModel.saveLoginStatus(true).observe(this) { isLoginSaved ->
                                        if (isLoginSaved) {
                                            navigateToMainActivity()
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
    private fun navigateToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}