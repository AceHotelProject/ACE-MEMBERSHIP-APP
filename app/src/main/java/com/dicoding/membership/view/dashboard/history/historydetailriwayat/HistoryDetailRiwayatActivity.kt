package com.dicoding.membership.view.dashboard.history.historydetailriwayat

import android.os.Bundle
import android.os.PersistableBundle
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
import com.dicoding.membership.databinding.ActivityHistoryDetailRiwayatBinding
import com.dicoding.membership.view.dialog.GlobalTwoButtonDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryDetailRiwayatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryDetailRiwayatBinding
    private val viewModel: HistoryDetailMemberViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {  // Fixed the onCreate signature
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailRiwayatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCloseButton()
        setupMenuButton()

        // Get user ID and load data
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
            val popupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.menu_member_detail, popupMenu.menu)

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
                    binding.loadingOverlay.visibility = View.VISIBLE
                    binding.scrollableContent.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.loadingOverlay.visibility = View.GONE
                    binding.scrollableContent.visibility = View.VISIBLE

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
                    binding.loadingOverlay.visibility = View.GONE
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun observeDeleteResult() {
        viewModel.deleteUserResult.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.loadingOverlay.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.loadingOverlay.visibility = View.GONE
                    Toast.makeText(this, "User berhasil dihapus", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Resource.Error -> {
                    binding.loadingOverlay.visibility = View.GONE
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}