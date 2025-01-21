package com.dicoding.membership.view.verification

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.core.data.source.Resource
import com.dicoding.membership.databinding.ActivityVerificationBinding
import com.dicoding.membership.view.dashboard.MainActivity
import com.dicoding.membership.view.login.LoginActivity
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerificationBinding
    private val verificationViewModel: VerificationViewModel by viewModels()
    private var timer: CountDownTimer? = null

    private var id: String = ""
    private var autoSendOtp: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        validateToken()

        getIntentData()

        sendOtpAutomatically()

        isButtonEnabled(false)

        setupActionBar()

        handleOtp()

        handleButtonSend()
    }

    private fun validateToken() {
        verificationViewModel.getRefreshToken().observe(this) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(supportFragmentManager, "Token Expired Dialog")
            }
        }
    }

    private fun getIntentData() {
        id = intent.getStringExtra(EXTRA_USER_ID) ?: ""
        autoSendOtp = intent.getBooleanExtra(EXTRA_AUTO_SEND_OTP, false)

        Log.d("VerificationActivity", "Received userId: $id, autoSend: $autoSendOtp")

        validateUserId()
    }

    private fun validateUserId() {
        if (id.isEmpty()) {
            Log.e("VerificationActivity", "No USER_ID received")
            Toast.makeText(this, "Error: No user ID found", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun sendOtpAutomatically() {
        // Reset input fields
        binding.vidFirst.text?.clear()
        binding.vidSecond.text?.clear()
        binding.vidThird.text?.clear()
        binding.vidForth.text?.clear()
        binding.vidFifth.text?.clear()
        binding.vidSixth.text?.clear()

        // Request focus to first field
        binding.vidFirst.requestFocus()

        // Disable button since fields are empty
        isButtonEnabled(false)

        // Send new OTP and restart timer
        verificationViewModel.sendOtp().observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Kode OTP baru telah dikirim", Toast.LENGTH_SHORT).show()
                    startTimer()
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Gagal mengirim OTP baru", Toast.LENGTH_SHORT).show()
                    binding.ivRefresh.isEnabled = true // Re-enable refresh button on error
                }
                else -> {}
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun isButtonEnabled(isEnabled: Boolean) {
        binding.btnSend.isEnabled = isEnabled
    }

    private fun setupActionBar() {
        supportActionBar?.hide()
    }

    private fun handleOtp() {

        binding.vidFirst.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkInputs()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length == 1) {
                    binding.vidSecond.requestFocus()
                }
                checkInputs()
            }

            override fun afterTextChanged(p0: Editable?) {
                checkInputs()
            }
        })

        binding.vidSecond.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkInputs()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length == 1) {
                    binding.vidThird.requestFocus()
                } else if (p0?.isEmpty() == true) {
                    binding.vidFirst.requestFocus()
                }
                checkInputs()
            }

            override fun afterTextChanged(p0: Editable?) {
                checkInputs()
            }
        })

        binding.vidThird.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkInputs()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length == 1) {
                    binding.vidForth.requestFocus()
                } else if (p0?.isEmpty() == true) {
                    binding.vidSecond.requestFocus()
                }
                checkInputs()
            }

            override fun afterTextChanged(p0: Editable?) {
                checkInputs()
            }
        })

        binding.vidForth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkInputs()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length == 1) {
                    binding.vidFifth.requestFocus()
                } else if (p0?.isEmpty() == true) {
                    binding.vidThird.requestFocus()
                }
                checkInputs()
            }

            override fun afterTextChanged(p0: Editable?) {
                checkInputs()
            }
        })

        binding.vidFifth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkInputs()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length == 1) {
                    binding.vidSixth.requestFocus()
                } else if (p0?.isEmpty() == true) {
                    binding.vidForth.requestFocus()
                }
                checkInputs()
            }

            override fun afterTextChanged(p0: Editable?) {
                checkInputs()
            }
        })

        binding.vidSixth.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkInputs()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.isEmpty() == true) {
                    binding.vidFifth.requestFocus()
                }
                checkInputs()
                hideKeyboard()
            }

            override fun afterTextChanged(p0: Editable?) {
                checkInputs()
            }
        })
    }

    private fun checkInputs() {
        val isComplete = binding.vidFirst.text?.length == 1 &&
                binding.vidSecond.text?.length == 1 &&
                binding.vidThird.text?.length == 1 &&
                binding.vidForth.text?.length == 1 &&
                binding.vidFifth.text?.length == 1 &&
                binding.vidSixth.text?.length == 1

        isButtonEnabled(isComplete)
    }

    private fun handleButtonSend() {
        binding.btnSend.setOnClickListener {
            hideKeyboard()
            val otpCode = "${binding.vidFirst.text}${binding.vidSecond.text}${binding.vidThird.text}${binding.vidForth.text}${binding.vidFifth.text}${binding.vidSixth.text}"

            verificationViewModel.verifyOtp(otpCode).observe(this) { result ->
                when (result) {
                    is Resource.Loading -> {
                        showLoading(true)
                        isButtonEnabled(false)
                    }
                    is Resource.Success -> {
                        showLoading(false)
                        handleVerificationSuccess()
                    }
                    is Resource.Error -> {
                        showLoading(false)
                        isButtonEnabled(true)
                        Toast.makeText(this, "Verifikasi gagal", Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }

        binding.ivRefresh.setOnClickListener {
            hideKeyboard()
            sendOtpAutomatically()
        }
    }

    private fun handleVerificationSuccess() {
        Toast.makeText(this, "Verifikasi berhasil", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }


    private fun startTimer() {
        timer?.cancel()
        timer = null

        timer = object : CountDownTimer(5 * 60 * 1000, 1000) {
            @SuppressLint("DefaultLocale")
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                binding.tvTimer.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.tvTimer.text = "00:00"
                timer = null
            }
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        timer = null
    }

    companion object {
        const val EXTRA_USER_ID = "USER_ID"
        const val EXTRA_AUTO_SEND_OTP = "AUTO_SEND_OTP"
    }
}