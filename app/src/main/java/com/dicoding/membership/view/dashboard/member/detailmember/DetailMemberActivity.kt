package com.dicoding.membership.view.dashboard.member.detailmember

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.core.data.source.Resource
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityDetailMemberBinding
import com.dicoding.membership.databinding.ActivityMainBinding
import com.dicoding.membership.view.dashboard.profile.detail.detail.ubahprofil.UbahProfileActivity
import com.dicoding.membership.view.dialog.GlobalTwoButtonDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailMemberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailMemberBinding
    private val viewModel: DetailMemberViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCloseButton()
        setupMenuButton()
        setupEditButton()

        // Get user ID from intent
        intent.getStringExtra(EXTRA_USER_ID)?.let { userId ->
            viewModel.getUserData(userId)
        }

        observeUserData()
        observeDeleteResult()
    }

    private fun setupCloseButton() {
        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    private fun setupMenuButton() {
        binding.btnMenu.setOnClickListener { view ->
            // Create PopupMenu
            val popupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.menu_member_detail, popupMenu.menu)

            // Handle menu item clicks
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_delete -> {
                        showDeleteDialog()
                        true
                    }
                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    private fun setupEditButton() {
        binding.fbUbahProfile.setOnClickListener {
            intent.getStringExtra(EXTRA_USER_ID)?.let { userId ->
                val intent = Intent(this, UbahProfileActivity::class.java).apply {
                    putExtra(UbahProfileActivity.USER_ID, userId)
                }
                startActivity(intent)
            }
        }
    }

    private fun showDeleteDialog() {
        val dialog = GlobalTwoButtonDialog().apply {
            setDialogTitle("Hapus User?")
            setDialogMessage("Apakah anda yakin ingin menghapus user ini?")
            setOnYesClickListener {
                intent.getStringExtra(EXTRA_USER_ID)?.let { userId ->
                    viewModel.deleteUser(userId)
                }
            }
        }
        dialog.show(supportFragmentManager, "DeleteUserDialog")
    }

    private fun observeUserData() {
        viewModel.userData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    resource.data?.let { user ->
                        binding.apply {
                            tvMembershipType.text = user.memberType ?: "Non-Member"
                            tvUserNama.text = user.name
                            tvUserNIK.text = user.citizenNumber ?: "-"
                            tvUserPhone.text = user.phone ?: "-"
                            tvUserAddress.text = user.address ?: "-"
                            tvUserEmail.text = user.email
                        }
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun observeDeleteResult() {
        viewModel.deleteUserResult.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Show loading
                }
                is Resource.Success -> {
                    Toast.makeText(this, "User berhasil dihapus", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Resource.Error -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun showLoading(boolean: Boolean){
        binding.loadingOverlay.visibility = if(boolean) View.VISIBLE else View.GONE
        binding.constraintLayout2.visibility = if(!boolean) View.VISIBLE else View.GONE

    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}