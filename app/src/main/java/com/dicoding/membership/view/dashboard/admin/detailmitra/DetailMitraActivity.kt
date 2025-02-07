package com.dicoding.membership.view.dashboard.admin.detailmitra

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dicoding.core.data.source.Resource
import com.dicoding.core.data.source.remote.response.merchants.Merchant
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityDetailMitraBinding
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailMitraActivity : AppCompatActivity() {
    private var _binding: ActivityDetailMitraBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailMitraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailMitraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        validateToken()

        getMerchantData()

        binding.rvPromoActive
    }

    private fun validateToken() {
        viewModel.getRefreshToken().observe(this) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(supportFragmentManager, "Token Expired Dialog")
            }
        }
    }

    private fun getMerchantData() {
        intent.getParcelableExtra<Merchant>(EXTRA_MERCHANT_DATA)?.let { merchant ->
            merchant.id?.let {
                viewModel.getMerchantById(it).observe(this) { result ->
                    when (result) {
                        is Resource.Success -> {
                            showLoading(false)
                            result.data?.let { data ->
                                loadFirstImage(data.picturesUrl)
                                binding.apply {
                                    detailMitraName.text = data.name
                                    detailMitraCategory.text = data.merchantType
                                    detailLink.text = data.name
                                    detailDescription.text = data.detail
                                }
                            }
                        }

                        is Resource.Error -> {
                            showLoading(false)
                            showError(result.message.toString())
                        }

                        is Resource.Loading -> showLoading(true)
                        is Resource.Message -> showMessage(result.message.toString())
                    }
                }
            }
        }
    }

    private fun loadFirstImage(images: List<String>) {
        if (images.isNotEmpty()) {
            Glide.with(this)
                .load(images[0])
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.image_empty)
                .into(binding.detailMitraImageView)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_MERCHANT_DATA = "extra_merchant_data"
    }
}