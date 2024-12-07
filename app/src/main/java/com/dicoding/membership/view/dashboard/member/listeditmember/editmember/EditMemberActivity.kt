package com.dicoding.membership.view.dashboard.member.listeditmember.editmember

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityDetailMemberBinding
import com.dicoding.membership.databinding.ActivityEditMemberBinding

class EditMemberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditMemberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isButtonEnabled(false)
        handleEditText()
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

        binding.tiTipePotonganHarga.addTextChangedListener(object : TextWatcher {
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
            val tipemember = tiTipeMember.text.toString()
            val bulanberlaku = tiTipeBulan.text.toString()
            val hargamember = tiTipeHarga.text.toString()
            val syaratketentuan = tiTipeSyaratKetentuan.text.toString()
            val potonganharga = tiTipePotonganHarga.text.toString()

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
            } else if (!syaratketentuan.matches(Regex("^[a-zA-Z ]+$"))) {
                binding.tiTipeSyaratKetentuan.error = "Syarat dan ketentuan hanya boleh huruf"
            } else {
                binding.tiTipeSyaratKetentuan.error = null
            }

            // Validasi Potongan Harga
            if (potonganharga.isEmpty()) {
                binding.tiTipePotonganHarga.error = "Potongan harga tidak boleh kosong"
            } else {
                try {
                    val potonganValue = potonganharga.toInt()
                    if (potonganValue <= 0) {
                        binding.tiTipePotonganHarga.error = "Potongan harga tidak boleh 0 atau negatif"
                    } else if (potonganValue > 1000000) {
                        binding.tiTipePotonganHarga.error = "Potongan harga maksimal 1.000.000"
                    } else {
                        binding.tiTipePotonganHarga.error = null
                    }
                } catch (e: NumberFormatException) {
                    binding.tiTipePotonganHarga.error = "Potongan harga harus berupa angka"
                }
            }

            // Enable button jika semua validasi terpenuhi
            isButtonEnabled(
                tipemember.isNotEmpty() && tipemember.length <= 20 && tipemember.matches(Regex("^[a-zA-Z ]+$")) &&
                        bulanberlaku.isNotEmpty() && try { bulanberlaku.toInt() in 1..60 } catch (e: NumberFormatException) { false } &&
                        hargamember.isNotEmpty() && try { hargamember.toInt() in 1..50000000 } catch (e: NumberFormatException) { false } &&
                        syaratketentuan.isNotEmpty() && syaratketentuan.length <= 200 && syaratketentuan.matches(Regex("^[a-zA-Z ]+$")) &&
                        potonganharga.isNotEmpty() && try { potonganharga.toInt() in 1..1000000 } catch (e: NumberFormatException) { false }
            )
        }
    }
}