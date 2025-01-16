package com.dicoding.membership.view.dashboard.member.detailmember.ubahprofile

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.core.data.source.Resource
import com.dicoding.membership.R
import com.dicoding.membership.core.utils.isInternetAvailable
import com.dicoding.membership.core.utils.showToast
import com.dicoding.membership.databinding.ActivityUbahProfileBinding
import com.dicoding.membership.view.dashboard.profile.detail.detail.ProfileDetailViewModel
import com.dicoding.membership.view.dashboard.profile.detail.detail.ubahprofil.UbahProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UbahProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUbahProfileBinding
    private val viewModel: UbahProfileViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUbahProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCloseButton()
        handleButtonSave()
    }

    private fun setupCloseButton(){
        binding.btnClose.setOnClickListener{
            finish()
        }
    }

    private fun handleButtonSave() {
        binding.apply {
            btnSave.setOnClickListener {
                val id = intent.getStringExtra(USER_ID) ?: ""
                //dummy imgpath
                val idPicturePath = "dummy_image_path.jpg"
                val name = tiNama.text?.toString() ?: "Default Name"
                val citizenNumber = tiNik.text?.toString() ?: "0000000000000000"
                val phone = tiTelepon.text?.toString() ?: "000000000000"
                val address = tiTipeAlamat.text?.toString() ?: "Default Address"

                //for debugging
                println("""
    ====== UPDATE USER REQUEST DATA ======
    ID: $id
    Image Path: $idPicturePath
    Name: $name
    Citizen Number: $citizenNumber
    Phone: $phone
    Address: $address
    ====================================
""".trimIndent())

                viewModel.updateUserData(
                    id = id,
                    idPicturePath = idPicturePath,
                    name = name,
                    citizenNumber = citizenNumber,
                    phone = phone,
                    address = address
                ).observe(this@UbahProfileActivity) { result ->
                    when (result) {
                        is Resource.Error -> {
                            showLoading(false)
                            isButtonEnabled(true)

                            if (!isInternetAvailable(this@UbahProfileActivity)) {
                                showToast(getString(R.string.check_internet))
                            } else {
                                showToast(result.message.toString())
                            }
                        }

                        is Resource.Loading -> {
                            showLoading(true)
                            isButtonEnabled(false)
                        }

                        is Resource.Message -> {
                            showLoading(false)
                            isButtonEnabled(true)
                        }

                        is Resource.Success -> {
                            showLoading(false)
                            isButtonEnabled(true)

                            showToast("Profile berhasil diperbarui")
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        // Implement your loading UI here
    }

    private fun isButtonEnabled(isEnabled: Boolean) {
        binding.btnSave.isEnabled = isEnabled
    }

    companion object {
        const val USER_ID = "user_id"
    }
}